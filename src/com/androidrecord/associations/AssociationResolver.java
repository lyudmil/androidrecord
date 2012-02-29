package com.androidrecord.associations;

import com.androidrecord.ActiveRecordBase;
import com.androidrecord.query.QueryContext;

import java.lang.reflect.Field;

/**
 * Determines and runs sub-queries to establish one-to-one associations within a query context.
 */
public enum AssociationResolver {
    hasOne {
        @Override
        public boolean matches(Field field) {
            return field.isAnnotationPresent(HasOne.class);
        }

        @Override
        public void establish() {
            String associationName = getField().getAnnotation(HasOne.class).name();
            OneToOneAssociation association = getContext().getOneToOneOwnershipAssociation(getRecord(), associationName);
            if (!association.isFulfilled()) {
                association.connectOwner(getRecord());
                if (association.getOwned() == null) association.connectOwned(lookUpOwnedRecord(getRecord()), getRecord().id);
            }
        }
    },
    belongsTo {
        @Override
        public boolean matches(Field field) {
            return field.isAnnotationPresent(BelongsTo.class);
        }

        @Override
        public void establish() {
            OneToOneAssociation association = getContext().getOneToOneAssociationOwning(getRecord(), getField());
            if (!association.isFulfilled()) {
                ActiveRecordBase owner = lookUpOwnerRecord(association.getOwnerId());
                association.connectOwner(owner);
            }
        }
    },
    none {
        @Override
        public void establish() {}

        @Override
        public boolean matches(Field field) {
            return false;
        }
    };

    public static AssociationResolver association(Field field) {
        for (AssociationResolver resolver : values()) {
            if (resolver.matches(field)) return resolver.onField(field);
        }
        return none;
    }

    private ActiveRecordBase record;
    private QueryContext context;
    private Field field;

    public AssociationResolver on(ActiveRecordBase record) {
        this.record = record;
        return this;
    }

    public AssociationResolver within(QueryContext context) {
        this.context = context;
        return this;
    }

    public ActiveRecordBase getRecord() {
        return record;
    }

    public QueryContext getContext() {
        return context;
    }

    public Field getField() {
        return field;
    }

    private AssociationResolver onField(Field field) {
        this.field = field;
        return this;
    }

    public ActiveRecordBase lookUpOwnedRecord(ActiveRecordBase activeRecordBase) {
        try {
            ActiveRecordBase owned = (ActiveRecordBase) getField().getType().newInstance();
            String associationName = getField().getAnnotation(HasOne.class).name();
            return lookUpRelatedRecord(owned, associationName + "_id=" + activeRecordBase.id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public ActiveRecordBase lookUpOwnerRecord(Long id) {
        try {
            ActiveRecordBase owner = (ActiveRecordBase) getField().getType().newInstance();
            return lookUpRelatedRecord(owner, "id=" + id);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ActiveRecordBase lookUpRelatedRecord(ActiveRecordBase owned, String whereClause) {
        return getContext().findRelated(owned, whereClause);
    }

    public abstract void establish();

    public abstract boolean matches(Field field);

}
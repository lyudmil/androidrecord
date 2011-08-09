package com.androidrecord.relations;

import com.androidrecord.ActiveRecordBase;
import com.androidrecord.query.QueryContext;

import java.lang.reflect.Field;

/**
 * Determines and runs sub-queries to establish one-to-one relationships within a query context.
 */
public enum RelationResolver {
    hasOne {
        @Override
        public boolean matches(Field field) {
            return field.isAnnotationPresent(HasOne.class);
        }

        @Override
        public void establish() {
            String relationName = getField().getAnnotation(HasOne.class).name();
            OneToOneRelation relation = getContext().getOneToOneOwnershipRelation(getRecord(), relationName);
            if (!relation.isFulfilled()) {
                relation.connectOwner(getRecord());
                if (relation.getOwned() == null) relation.connectOwned(lookUpOwnedRecord(getRecord()), getRecord().id);
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
            OneToOneRelation relation = getContext().getOneToOneRelationOwning(getRecord(), getField());
            if (!relation.isFulfilled()) {
                ActiveRecordBase owner = lookUpOwnerRecord(relation.getOwnerId());
                relation.connectOwner(owner);
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

    public static RelationResolver relation(Field field) {
        for (RelationResolver resolver : values()) {
            if (resolver.matches(field)) return resolver.onField(field);
        }
        return none;
    }

    private ActiveRecordBase record;
    private QueryContext context;
    private Field field;

    public RelationResolver on(ActiveRecordBase record) {
        this.record = record;
        return this;
    }

    public RelationResolver within(QueryContext context) {
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

    private RelationResolver onField(Field field) {
        this.field = field;
        return this;
    }

    public ActiveRecordBase lookUpOwnedRecord(ActiveRecordBase activeRecordBase) {
        try {
            ActiveRecordBase owned = (ActiveRecordBase) getField().getType().newInstance();
            String relationName = getField().getAnnotation(HasOne.class).name();
            return lookUpRelatedRecord(owned, relationName + "_id=" + activeRecordBase.id);
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
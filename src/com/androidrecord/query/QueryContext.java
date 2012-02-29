package com.androidrecord.query;

import com.androidrecord.ActiveRecordBase;
import com.androidrecord.associations.OneToOneAssociation;
import com.androidrecord.db.Database;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Each time the user runs a query, it is executed in its own context to resolve all the one-to-one associations for
 * that record. One-to-many associations are lazily loaded and are handled differently.
 *
 * @see com.androidrecord.ActiveCollection
 */
public class QueryContext<T extends ActiveRecordBase> {
    private Database database;
    private T record;
    private Map<String, OneToOneAssociation> oneToOneAssociations;

    public QueryContext(Database database, T record) {
        this.database = database;
        this.record = record;
        oneToOneAssociations = new HashMap<String, OneToOneAssociation>();
    }

    public T find(String whereClause) {
        return new FindQuery<T>(this, database, record, whereClause).run();
    }

    public OneToOneAssociation oneToOneAssociation(String associationName, Long ownerId) {
        String associationIdentifier = identifierFor(associationName, ownerId);
        if (oneToOneAssociations.get(associationIdentifier) == null) oneToOneAssociations.put(associationIdentifier, new OneToOneAssociation(associationName, ownerId));
        return oneToOneAssociations.get(associationIdentifier);
    }

    private String identifierFor(String associationName, Long ownerId) {
        return associationName + "#" + ownerId;
    }

    public List<T> all() {
        return new SelectAllQuery<T>(this, database, record).run();
    }

    public T findRelated(ActiveRecordBase owned, String whereClause) {
        return new FindQuery<T>(this, database, (T) owned, whereClause).run();
    }

    public OneToOneAssociation getOneToOneOwnershipAssociation(ActiveRecordBase owner, String associationName) {
        String associationIdentifier = identifierFor(associationName, owner.id);
        return oneToOneAssociations.get(associationIdentifier);
    }

    public OneToOneAssociation getOneToOneAssociationOwning(ActiveRecordBase ownedEntity, Field field) {
        for (OneToOneAssociation association : oneToOneAssociations.values()) {
            if (association.getOwned() == ownedEntity && association.getName().equals(field.getName())) {
                return association;
            }
        }
        return null;
    }

    private List select(ActiveRecordBase lookupInstance, String whereClause) {
        return new SelectQuery<T>(this, database, lookupInstance, whereClause).run();
    }

    public boolean hasUnfulfilledAssociations() {
        for (OneToOneAssociation oneToOneAssociation : oneToOneAssociations.values()) {
            if (!oneToOneAssociation.isFulfilled()) return true;
        }
        return false;
    }

    public List<T> where(String whereClause) {
        return select(record, whereClause);
    }
}

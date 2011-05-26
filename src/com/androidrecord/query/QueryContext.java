package com.androidrecord.query;

import com.androidrecord.ActiveRecordBase;
import com.androidrecord.db.Database;
import com.androidrecord.relations.OneToOneRelation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryContext<T extends ActiveRecordBase> {
    private Database database;
    private T record;
    private Map<String, OneToOneRelation> oneToOneRelations;

    public QueryContext(Database database, T record) {
        this.database = database;
        this.record = record;
        oneToOneRelations = new HashMap<String, OneToOneRelation>();
    }

    public T find(String whereClause) {
        return new FindQuery<T>(this, database, record, whereClause).run();
    }

    public OneToOneRelation oneToOneRelation(String relationName, Long ownerId) {
        String relationIdentifier = identifierFor(relationName, ownerId);
        if (oneToOneRelations.get(relationIdentifier) == null) oneToOneRelations.put(relationIdentifier, new OneToOneRelation(relationName, ownerId));
        return oneToOneRelations.get(relationIdentifier);
    }

    private String identifierFor(String relationName, Long ownerId) {
        return relationName + "#" + ownerId;
    }

    public List<T> all() {
        return new SelectAllQuery<T>(this, database, record).run();
    }

    public T findRelated(ActiveRecordBase owned, String whereClause) {
        return new FindQuery<T>(this, database, (T) owned, whereClause).run();
    }

    public OneToOneRelation getOneToOneOwnershipRelation(ActiveRecordBase owner, String relationName) {
        String relationIdentifier = identifierFor(relationName, owner.id);
        return oneToOneRelations.get(relationIdentifier);
    }

    public OneToOneRelation getOneToOneRelationOwning(ActiveRecordBase ownedEntity, Field field) {
        for (OneToOneRelation relation : oneToOneRelations.values()) {
            if (relation.getOwned() == ownedEntity && relation.getName().equals(field.getName())) {
                return relation;
            }
        }
        return null;
    }

    private List select(ActiveRecordBase lookupInstance, String whereClause) {
        return new SelectQuery<T>(this, database, lookupInstance, whereClause).run();
    }

    public boolean hasUnfulfilledRelations() {
        for (OneToOneRelation oneToOneRelation : oneToOneRelations.values()) {
            if (!oneToOneRelation.isFulfilled()) return true;
        }
        return false;
    }

    public List<T> where(String whereClause) {
        return select(record, whereClause);
    }
}

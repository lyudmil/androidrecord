package com.androidrecord.query;

import android.database.Cursor;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.db.Database;
import com.androidrecord.relations.BelongsTo;
import com.androidrecord.relations.HasMany;
import com.androidrecord.relations.HasOne;
import com.androidrecord.relations.OneToOneRelation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.androidrecord.query.ResultSetFieldTranslator.translate;

public class Query<T extends ActiveRecordBase> {
    Database database;
    T activeRecordInstance;
    private QueryContext<T> context;

    public Query(QueryContext<T> context, T activeRecordInstance, Database database) {
        this.context = context;
        this.activeRecordInstance = activeRecordInstance;
        this.database = database;
    }

    protected List<T> collectionFrom(Cursor queryResult) {
        ArrayList<T> results = new ArrayList<T>();
        if (queryResult.getCount() == 0) return results;

        queryResult.moveToFirst();
        do {
            results.add(recordFrom(queryResult));
        } while (queryResult.moveToNext());

        return results;
    }

    protected T recordFrom(Cursor result) {
        T activeRecord = newRecord();
        if (result.getCount() == 0) return null;

        List<Field> sortedFields = activeRecord.getSortedFields();
        for (Field field : sortedFields) {
            copyField(field, activeRecord, result);
        }

        if (context.hasUnfulfilledRelations()) activeRecord.linkRelations(context);
        return activeRecord;
    }

    private void copyField(Field field, T activeRecord, Cursor result) {
        if (isRelation(field)) {
            handleRelation(field, activeRecord, result);
        } else {
            translate(field).from(result).to(activeRecord);
        }
    }

    private void handleRelation(Field field, T activeRecord, Cursor result) {
        if (hasOne(field)) {
            handleHasOneRelation(field, activeRecord);
        }
        if (belongsTo(field)) {
            handleBelongsToRelation(field, activeRecord, result);
        }
    }

    private void handleBelongsToRelation(Field field, T activeRecord, Cursor result) {
        String relationName = field.getName();
        long ownerId = result.getLong(result.getColumnIndex(relationName + "_id"));

        OneToOneRelation relation = context.oneToOneRelation(relationName, ownerId);
        relation.connectOwned(activeRecord, ownerId);
    }

    private void handleHasOneRelation(Field field, T activeRecord) {
        String relationName = field.getAnnotation(HasOne.class).name();
        context.oneToOneRelation(relationName, activeRecord.id);
    }

    private boolean isRelation(Field field) {
        return hasOne(field) || belongsTo(field) || hasMany(field);
    }

    private boolean hasMany(Field field) {
        return field.isAnnotationPresent(HasMany.class);
    }

    private boolean belongsTo(Field field) {
        return field.isAnnotationPresent(BelongsTo.class);
    }

    private boolean hasOne(Field field) {
        return field.isAnnotationPresent(HasOne.class);
    }

    private T newRecord() {
        T activeRecord;
        try {
            activeRecord = (T) activeRecordInstance.getClass().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return activeRecord;
    }

}

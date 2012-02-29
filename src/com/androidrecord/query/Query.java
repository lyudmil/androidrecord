package com.androidrecord.query;

import android.database.Cursor;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.associations.OneToOneAssociation;
import com.androidrecord.db.Database;
import com.androidrecord.associations.BelongsTo;
import com.androidrecord.associations.HasMany;
import com.androidrecord.associations.HasOne;

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

        if (context.hasUnfulfilledAssociations()) activeRecord.linkAssociations(context);
        return activeRecord;
    }

    private void copyField(Field field, T activeRecord, Cursor result) {
        if (isAssociation(field)) {
            handleAssociation(field, activeRecord, result);
        } else {
            translate(field).from(result).to(activeRecord);
        }
    }

    private void handleAssociation(Field field, T activeRecord, Cursor result) {
        if (hasOne(field)) {
            handleHasOneAssociation(field, activeRecord);
        }
        if (belongsTo(field)) {
            handleBelongsToAssociation(field, activeRecord, result);
        }
    }

    private void handleBelongsToAssociation(Field field, T activeRecord, Cursor result) {
        String associationName = field.getName();
        long ownerId = result.getLong(result.getColumnIndex(associationName + "_id"));

        OneToOneAssociation association = context.oneToOneAssociation(associationName, ownerId);
        association.connectOwned(activeRecord, ownerId);
    }

    private void handleHasOneAssociation(Field field, T activeRecord) {
        String associationName = field.getAnnotation(HasOne.class).name();
        context.oneToOneAssociation(associationName, activeRecord.id);
    }

    private boolean isAssociation(Field field) {
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

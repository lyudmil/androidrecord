package com.androidrecord.query;

import android.database.Cursor;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.db.Database;

public class FindQuery<T extends ActiveRecordBase> extends Query<T> {
    private Class<T> modelClass;
    private String whereClause;

    public FindQuery(QueryContext<T> context, Database database, Class<T> modelClass, String whereClause) {
        super(context, modelClass, database);
        this.modelClass = modelClass;
        this.whereClause = whereClause;
    }

    public T run() {
        Cursor queryResult = queryResult();
        T record = recordFrom(queryResult);
        queryResult.close();
        return record;
    }

    private Cursor queryResult() {
        Cursor result = database.select(ActiveRecordBase.tableNameFor(modelClass), whereClause);
        if (result.getCount() > 1)
            throw new RuntimeException("Got " + result.getCount() + " results for " + modelClass.getSimpleName() + " [" + whereClause + "]");
        result.moveToFirst();
        return result;
    }

}
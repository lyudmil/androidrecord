package com.androidrecord.query;

import android.database.Cursor;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.db.Database;

import java.util.List;

public abstract class MultiValuedQuery<T extends ActiveRecordBase> extends Query<T> {

    public MultiValuedQuery(QueryContext<T> context, Class<T> modelClass, Database database) {
        super(context, modelClass, database);
    }

    public List<T> run() {
        Cursor queryResult = select();
        List<T> collection = collectionFrom(queryResult);
        queryResult.close();
        return collection;
    }

    protected abstract Cursor select();
}

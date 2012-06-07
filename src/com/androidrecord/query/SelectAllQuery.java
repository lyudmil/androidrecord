package com.androidrecord.query;

import android.database.Cursor;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.db.Database;

public class SelectAllQuery<T extends ActiveRecordBase> extends MultiValuedQuery<T> {

    public SelectAllQuery(QueryContext<T> context, Database database, Class<T> modelClass) {
        super(context, modelClass, database);
    }

    @Override
    protected Cursor select() {
        return database.selectAll(ActiveRecordBase.tableNameFor(modelClass));
    }
}

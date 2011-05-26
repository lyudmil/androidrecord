package com.androidrecord.query;

import android.database.Cursor;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.db.Database;

public class SelectAllQuery<T extends ActiveRecordBase> extends MultiValuedQuery<T> {

    public SelectAllQuery(QueryContext<T> context, Database database, T activeRecordInstance) {
        super(context, activeRecordInstance, database);
    }

    @Override
    protected Cursor select() {
        return database.selectAll(activeRecordInstance.tableName());
    }
}

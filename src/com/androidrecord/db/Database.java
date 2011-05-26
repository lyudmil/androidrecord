package com.androidrecord.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database {
    private SQLiteDatabase database;

    public Database(SQLiteDatabase database) {
        this.database = database;
    }

    public Long insert(String tableName, ContentValues contentValues) {
        return database.insert(tableName, null, contentValues);
    }

    public void update(String tableName, ContentValues contentValues, String whereClause) {
        database.update(tableName, contentValues, whereClause, null);
    }

    public void delete(String tableName, String whereClause) {
        database.delete(tableName, whereClause, null);
    }

    public Cursor select(String tableName, String selection) {
        return database.query(tableName, null, selection, null, null, null, null);
    }

    public Cursor selectAll(String tableName) {
        return select(tableName, null);
    }
}

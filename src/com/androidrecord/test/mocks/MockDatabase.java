package com.androidrecord.test.mocks;

import android.content.ContentValues;
import android.database.Cursor;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.db.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MockDatabase extends Database {
    private ArrayList<MockCursor> usedRecords = new ArrayList<MockCursor>();
    private ArrayList<MockCursor> resultsToReturn = new ArrayList<MockCursor>();;
    private boolean forever = false;

    public long nextIdToReturn;
    public HashMap<String, Object> lastQueryParameters = new HashMap<String, Object>();

    public boolean insertCalled = false;
    public boolean updateCalled = false;
    public boolean selectCalled = false;
    public boolean deleteCalled = false;

    public MockDatabase() {
        super(null);
    }

    @Override
    public Long insert(String tableName, ContentValues contentValues) {
        insertCalled = true;
        updateLastQueryParameters(tableName, contentValues, null);
        return nextIdToReturn;
    }

    @Override
    public void update(String tableName, ContentValues contentValues, String whereClause) {
        updateCalled = true;
        updateLastQueryParameters(tableName, contentValues, whereClause);
    }

    private void updateLastQueryParameters(String tableName, ContentValues contentValues, String whereClause) {
        lastQueryParameters.put("tableName", tableName);
        lastQueryParameters.put("contentValues", contentValues);
        lastQueryParameters.put("whereClause", whereClause);
    }

    @Override
    public Cursor select(String tableName, String selection) {
        selectCalled = true;
        if (resultsToReturn.isEmpty()) {
            if (forever) resultsToReturn = new ArrayList<MockCursor>(usedRecords);
            else throw new RuntimeException("No more results to return.");
        }
        return returnNextRecord();
    }

    private MockCursor returnNextRecord() {
        MockCursor result = resultsToReturn.get(0);
        usedRecords.add(result);
        resultsToReturn.remove(result);
        return result;
    }

    @Override
    public void delete(String tableName, String whereClause) {
        deleteCalled = true;
        updateLastQueryParameters(tableName, null, whereClause);
    }

    public MockDatabase firstReturn(ActiveRecordBase recordToFind) {
        resultsToReturn = new ArrayList<MockCursor>();
        resultsToReturn.add(cursorFor(recordToFind));
        return this;
    }

    public MockDatabase firstReturn(List<? extends ActiveRecordBase> records) {
        resultsToReturn = new ArrayList<MockCursor>();
        return thenReturn(records);
    }

    public MockDatabase thenReturn(ActiveRecordBase recordToFind) {
        resultsToReturn.add(cursorFor(recordToFind));
        return this;
    }

    public MockDatabase thenReturn(List<? extends ActiveRecordBase> records) {
        ArrayList<ContentValues> result = new ArrayList<ContentValues>();
        for (ActiveRecordBase record : records) {
            result.add(record.contentValues());
        }

        resultsToReturn.add(new MockCursor(result));
        return this;
    }

    private MockCursor cursorFor(ActiveRecordBase recordToFind) {
        return new MockCursor(recordToFind.contentValues());
    }

    public MockDatabase returnEmptyResult() {
        return thenReturnEmptyResult();
    }

    public MockDatabase thenReturnEmptyResult() {
        resultsToReturn.add(new MockCursor());
        return this;
    }

    public void forever() {
        forever = true;
    }

    public ContentValues lastQueryContents() {
        return (ContentValues) lastQueryParameters.get("contentValues");
    }
}

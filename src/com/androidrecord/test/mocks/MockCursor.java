package com.androidrecord.test.mocks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockCursor implements Cursor {

    private List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
    private ArrayList<String> columns;
    private Map<String,Object> current;

    public MockCursor(ContentValues contentValues) {
        addResult(contentValues);
        calculateColumnNames();
    }

    public MockCursor(List<ContentValues> contentValues) {
        for (ContentValues contentValue : contentValues) {
            addResult(contentValue);
        }
        calculateColumnNames();
    }

    private void calculateColumnNames() {
        if (values.isEmpty()) return;
        columns = new ArrayList<String>(values.get(0).keySet());
    }

    private void addResult(ContentValues contentValues) {
        HashMap<String, Object> recordResult = new HashMap<String, Object>();
        recordResult.put("updated_at", null);
        for (Map.Entry<String, Object> value : contentValues.valueSet()) {
            recordResult.put(value.getKey(), value.getValue());
        }
        values.add(recordResult);
    }

    public MockCursor() {
        this(new ArrayList<ContentValues>());
    }

    public int getCount() {
        return values.size();
    }

    public int getPosition() {
        return 0;
    }

    public boolean move(int i) {
        return false;
    }

    public boolean moveToPosition(int i) {
        return true;
    }

    public boolean moveToFirst() {
        if (values.isEmpty()) return false;
        current = values.get(0);
        return true;
    }

    public boolean moveToLast() {
        return true;
    }

    public boolean moveToNext() {
        int index = values.indexOf(current);
        if (index == values.size() - 1) return false;
        current = values.get(index + 1);
        return true;
    }

    public boolean moveToPrevious() {
        return false;
    }

    public boolean isFirst() {
        return true;
    }

    public boolean isLast() {
        return true;
    }

    public boolean isBeforeFirst() {
        return false;
    }

    public boolean isAfterLast() {
        return false;
    }

    public int getColumnIndex(String s) {
        return columns.indexOf(s);
    }

    public int getColumnIndexOrThrow(String s) throws IllegalArgumentException {
        return 0;
    }

    public String getColumnName(int i) {
        return columns.get(i);
    }

    public String[] getColumnNames() {
        return columns.toArray(new String[columns.size()]);
    }

    public int getColumnCount() {
        return columns.size();
    }

    public byte[] getBlob(int i) {
        return (byte[]) getValueAtColumnIndex(i);
    }

    private Object getValueAtColumnIndex(int i) {
        return current.get(columns.get(i));
    }

    public String getString(int i) {
        return (String) getValueAtColumnIndex(i);
    }

    public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {
    }

    public short getShort(int i) {
        return (Short) getValueAtColumnIndex(i);
    }

    public int getInt(int i) {
        return (Integer) getValueAtColumnIndex(i);
    }

    public long getLong(int i) {
        return (Long) getValueAtColumnIndex(i);
    }

    public float getFloat(int i) {
        return (Float) getValueAtColumnIndex(i);
    }

    public double getDouble(int i) {
        return (Double) getValueAtColumnIndex(i);
    }

    public boolean isNull(int i) {
        return getValueAtColumnIndex(i) == null;
    }

    public void deactivate() {
    }

    public boolean requery() {
        return false;
    }

    public void close() {
    }

    public boolean isClosed() {
        return false;
    }

    public void registerContentObserver(ContentObserver contentObserver) {
    }

    public void unregisterContentObserver(ContentObserver contentObserver) {
    }

    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    }

    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
    }

    public void setNotificationUri(ContentResolver contentResolver, Uri uri) {
    }

    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    public Bundle getExtras() {
        return null;
    }

    public Bundle respond(Bundle bundle) {
        return null;
    }
}

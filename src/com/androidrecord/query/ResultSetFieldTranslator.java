package com.androidrecord.query;

import android.database.Cursor;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.DateTime;

import java.lang.reflect.Field;

public enum ResultSetFieldTranslator {
    LONG {
        @Override
        public boolean translates(Field field) {
            return field.getType().equals(Long.class);
        }

        @Override
        public Object value() {
            int columnIndex = getResultSet().getColumnIndex(getField().getName());
            return getResultSet().getLong(columnIndex);
        }
    },
    INTEGER {
        @Override
        public boolean translates(Field field) {
            Class<?> type = field.getType();
            return type.equals(Integer.class) || type.equals(int.class);
        }

        @Override
        public Object value() {
            int columnIndex = getResultSet().getColumnIndex(getField().getName());
            return getResultSet().getInt(columnIndex);
        }
    },
    BOOLEAN {
        @Override
        public boolean translates(Field field) {
            Class<?> type = field.getType();
            return type.equals(Boolean.class) || type.equals(boolean.class);
        }

        @Override
        public Object value() {
            int columnIndex = getResultSet().getColumnIndex(getField().getName());
            int value = getResultSet().getInt(columnIndex);
            return value == 1;
        }
    },
    STRING {
        @Override
        public boolean translates(Field field) {
            return field.getType().equals(String.class);
        }

        @Override
        public Object value() {
            int columnIndex = getResultSet().getColumnIndex(getField().getName());
            return getResultSet().getString(columnIndex);
        }
    },
    DATE_TIME {
        @Override
        public boolean translates(Field field) {
            return field.getType().equals(DateTime.class);
        }

        @Override
        public Object value() {
            int columnIndex = getResultSet().getColumnIndex(getField().getName());
            return DateTime.from(getResultSet().getString(columnIndex));
        }
    };

    private Cursor resultSet;
    private Field field;

    public ResultSetFieldTranslator from(Cursor resultSet) {
        this.resultSet = resultSet;
        return this;
    }

    public void to(ActiveRecordBase record) {
        try {
            field.set(record, value());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSetFieldTranslator translate(Field field) {
        for (ResultSetFieldTranslator translator : values()) {
            if (translator.translates(field)) return translator.onField(field);
        }
        throw new RuntimeException("No translator for " + field);
    }

    public abstract boolean translates(Field field);

    public abstract Object value();

    public Cursor getResultSet() {
        return resultSet;
    }

    public Field getField() {
        return field;
    }

    private ResultSetFieldTranslator onField(Field field) {
        this.field = field;
        return this;
    }
}

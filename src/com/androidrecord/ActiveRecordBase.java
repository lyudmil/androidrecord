package com.androidrecord;

import android.content.ContentValues;
import android.content.Context;
import com.androidrecord.db.Database;
import com.androidrecord.query.ColumnValues;
import com.androidrecord.query.CreateQuery;
import com.androidrecord.query.QueryContext;
import com.androidrecord.relations.RelationResolver;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.androidrecord.relations.RelationResolver.relation;
import static com.androidrecord.utils.StringHelper.pluralize;
import static com.androidrecord.utils.StringHelper.underscorize;

public abstract class ActiveRecordBase<T extends ActiveRecordBase> {
    private static Database database;
    private static Context context;

    public Long id;
    public DateTime created_at = DateTime.now();

    protected ActiveRecordBase() {
        initializeCollections();
    }

    private void initializeCollections() {
        for (Field field : getClass().getFields()) {
            if (field.getType().equals(ActiveCollection.class)) {
                try {
                    field.set(this, ActiveCollection.attachTo(this, field));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void save() {
        if (isTransient()) {
            created_at = DateTime.now();
            id = database.insert(tableName(), contentValues());
        } else {
            database.update(tableName(), contentValues(), whereIdMatches());
        }
    }

    public void destroy() {
        if (isTransient()) return;
        database.delete(tableName(), whereIdMatches());
    }

    public T find(long id) {
        return find("id =" + id);
    }

    public T find(String whereClause) {
        return new QueryContext<T>(database, (T) this).find(whereClause);
    }

    public List<T> where(String whereClause) {
        return new QueryContext<T>(database, (T) this).where(whereClause);
    }

    public T first(String whereClause) {
        List<T> records = where(whereClause);
        if (records.isEmpty()) return null;
        return records.get(0);
    }

    public List<T> all() {
        return new QueryContext<T>(database, (T) this).all();
    }

    public String whereIdMatches() {
        return "id=" + id;
    }

    public boolean isTransient() {
        return id == null;
    }

    public ContentValues contentValues() {
        return new ColumnValues(this).generate();
    }

    public String tableName() {
        return tableNameFor(this.getClass());
    }

    public static String tableNameFor(Class activeRecordClass) {
        String className = activeRecordClass.getSimpleName();
        return underscorize(pluralize(className));
    }

    public static String createSqlFor(Class<? extends ActiveRecordBase> activeRecordClass) {
        return new CreateQuery(activeRecordClass).build();
    }

    public void linkRelations(QueryContext<T> context) {
        for (Field field : getClass().getFields()) {
            RelationResolver resolver = relation(field).on(this).within(context);
            resolver.establish();
        }
    }

    public List<Field> getSortedFields() {
        List<Field> fields = Arrays.asList(getClass().getFields());
        Collections.sort(fields, new FieldComparator());
        return fields;
    }

    public String getResourceString(int id) {
        return context.getString(id);
    }

    public static void bootStrap(Database database, Context context) {
        ActiveRecordBase.database = database;
        ActiveRecordBase.context = context;
    }

    private static class FieldComparator implements Comparator<Field> {
        public int compare(Field field1, Field field2) {
            if (field1.getName().equals("id")) return -1;
            return 1;
        }
    }
}
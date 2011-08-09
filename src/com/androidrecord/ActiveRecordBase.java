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

/**
 * Base class for all persistent objects. It provides methods to do CRUD operations on
 * the database.
 * @param <T>   the specific class.
 */
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

    /**
     * Create or update a record in the database.
     *
     * The method checks if the object has ever been saved in the database by checking
     * if the id field has been set.
     * If the id is null, it issues the corresponding insert query. Otherwise, it issues
     * an update statement with the current object data for the record in the database matching
     * the id specified by the id field.
     */
    public void save() {
        if (isTransient()) {
            created_at = DateTime.now();
            id = database.insert(tableName(), contentValues());
        } else {
            database.update(tableName(), contentValues(), whereIdMatches());
        }
    }

    /**
     * Delete a record in the database.
     *
     * If the record has been saved in the database the method issues a delete statement for
     * the record in the database matching the id specified by the id field.
     */
    public void destroy() {
        if (isTransient()) return;
        database.delete(tableName(), whereIdMatches());
    }

    /**
     * Get an object corresponding to the record in the database that matches the specified id.
     *
     * @param id    the id of the record in the database.
     * @return      a new object with its persistent fields set to the values of the database record found
     */
    public T find(long id) {
        return find("id =" + id);
    }

    /**
     * Get an object corresponding to the first record in the database that matches the specified where clause.
     *
     * @param whereClause   the where clause to match against.
     * @return              a new object with its persistent fields set to the values of the database record found
     */
    public T find(String whereClause) {
        return new QueryContext<T>(database, (T) this).find(whereClause);
    }

    /**
     * Get all objects in the database that match the specified where clause.
     *
     * @param whereClause   the where clause to match against.
     * @return              a list of objects, each of which has its persistent fields set to the values of each of the
     *                      database records found.
     */
    public List<T> where(String whereClause) {
        return new QueryContext<T>(database, (T) this).where(whereClause);
    }

    /**
     * Get all objects in the corresponding database table.
     *
     * @return  a list of objects, each of which has a corresponding representation in the database table for its class.
     */
    public List<T> all() {
        return new QueryContext<T>(database, (T) this).all();
    }

    public boolean isTransient() {
        return id == null;
    }

    public ContentValues contentValues() {
        return new ColumnValues(this).generate();
    }

    /**
     * Generate the table name for the class.
     *
     * The table name is derived from the name of the class. First, the camel-case class name is converted to underscores,
     * then the corresponding word is pluralized.
     *
     * @see     com.androidrecord.utils.StringHelper
     * @return  a string corresponding to the table name for the class.
     */
    public String tableName() {
        return tableNameFor(this.getClass());
    }

    public static String tableNameFor(Class activeRecordClass) {
        String className = activeRecordClass.getSimpleName();
        return underscorize(pluralize(className));
    }

    /**
     * Generate the query to create the database table corresponding to a persistent class.
     *
     * @param activeRecordClass the persistent class to create the table for.
     * @return                  the SQL query to create a table
     */
    public static String createSqlFor(Class<? extends ActiveRecordBase> activeRecordClass) {
        return new CreateQuery(activeRecordClass).build();
    }

    /**
     * Connect related records in memory.
     *
     * @param context   the current query context
     * @see             QueryContext
     * @see             RelationResolver
     */
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

    /**
     * Get a string defined as an application resource
     *
     * @param id    the resource id
     * @return      the resource string
     */
    public String getResourceString(int id) {
        return context.getString(id);
    }

    private String whereIdMatches() {
        return "id=" + id;
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
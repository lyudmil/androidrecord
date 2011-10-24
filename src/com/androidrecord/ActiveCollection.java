package com.androidrecord;

import com.androidrecord.relations.HasMany;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A collection class for active records. It provides a consistent interface to model one-to-many relationships and
 * lazy loading functionality.
 *
 * @param <T>
 */
public class ActiveCollection<T extends ActiveRecordBase> implements Iterable<T> {
    private List<T> records = new ArrayList<T>();
    private ActiveRecordBase owner;
    private String relationName;
    private Class relatedType;
    private boolean loaded = false;

    public ActiveCollection(ActiveRecordBase owner, Field field) {
        this.owner = owner;
        HasMany annotation = field.getAnnotation(HasMany.class);
        this.relationName = annotation.name();
        relatedType = annotation.relatedType();
    }

    public static <O extends ActiveRecordBase> ActiveCollection<O> attachTo(ActiveRecordBase owner, Field field) {
        return new ActiveCollection<O>(owner, field);
    }

    public void add(T record) {
        setOwnerOn(record);
        records.add(record);
    }

    public void addAll(T ... records) {
        for (T record : records) {
            add(record);
        }
    }

    public boolean contain(T record) {
        load();
        return records.contains(record);
    }

    public void remove(T record) {
        records.remove(record);
    }

    public boolean areEmpty() {
        load();
        return records.isEmpty();
    }

    public Iterator<T> iterator() {
        load();
        return records.iterator();
    }

    private void setOwnerOn(T record) {
        try {
            Field field = record.getClass().getField(relationName);
            field.set(record, owner);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        load();
        return records.size();
    }

    private void load() {
        if (loaded) return;
        try {
            ActiveRecordBase lookup = (ActiveRecordBase) relatedType.newInstance();
            List<T> relatedRecords = lookup.where(relationName + "_id=" + owner.id);
            for (T relatedRecord : relatedRecords) {
                add(relatedRecord);
            }
            loaded = true;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public T first() {
        load();
        if (records.isEmpty()) return null;
        return records.get(0);
    }

    public List<T> asList() {
        load();
        return records;
    }

    public int indexOf(T record) {
        load();
        return records.indexOf(record);
    }

    public void clear() {
        records.clear();
    }
}

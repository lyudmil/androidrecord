package com.androidrecord.query;

import com.androidrecord.ActiveCollection;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.associations.BelongsTo;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class CreateQuery {
    private static final String ID_FIELD_SPECIFICATION = "id integer primary key autoincrement not null";

    private Class<? extends ActiveRecordBase> activeRecordClass;

    public CreateQuery(Class<? extends ActiveRecordBase> activeRecordClass) {
        this.activeRecordClass = activeRecordClass;
    }

    public String build() {
        return "create table " + ActiveRecordBase.tableNameFor(this.activeRecordClass) + " (" + fieldSpecifications() + ");";
    }

    private static boolean representsAssociation(Field field) {
        Class<?> fieldType = field.getType();
        if (ActiveRecordBase.class.isAssignableFrom(fieldType)) return true;
        return fieldType.equals(ActiveCollection.class);
    }

    private static boolean representsBelonging(Field field) {
        return field.isAnnotationPresent(BelongsTo.class);
    }

    private String fieldSpecifications() {
        String fieldSpecifications = ID_FIELD_SPECIFICATION;
        for (Field field : fieldsIgnoringId()) {
            fieldSpecifications = addSpecificationFor(field, fieldSpecifications);
        }
        return fieldSpecifications;
    }

    private String addSpecificationFor(Field field, String fieldSpecifications) {
        if (representsAssociation(field)) {
            if (representsBelonging(field)) {
                fieldSpecifications += ", " + field.getName() + "_id" + " " + "integer";
            }
        } else {
            fieldSpecifications += ", " + field.getName() + " " + ColumnHelper.sqlTypeFor(field.getType());
        }
        return fieldSpecifications;
    }

    private ArrayList<Field> fieldsIgnoringId() {
        Field[] allFields = activeRecordClass.getFields();
        ArrayList<Field> fieldsIgnoringId = new ArrayList<Field>();
        for (Field field : allFields) {
            if (!field.getName().equals("id")) fieldsIgnoringId.add(field);
        }
        return fieldsIgnoringId;
    }
}

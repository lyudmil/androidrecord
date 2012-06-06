package com.androidrecord.query;

import com.androidrecord.DateTime;

import java.util.HashMap;

public class ColumnHelper {

    private static HashMap<Class, String> types = new HashMap<Class, String>() {{
        put(String.class, "text");

        put(Integer.class, "integer");
        put(int.class, "integer");

        put(Long.class, "integer");
        put(long.class, "integer");

        put(DateTime.class, "varchar(19)");

        put(Boolean.class, "integer");
        put(boolean.class, "integer");

    }};

    public static String sqlTypeFor(Class fieldType) {
        return types.get(fieldType);
    }
}

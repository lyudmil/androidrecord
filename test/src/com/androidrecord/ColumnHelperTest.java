package com.androidrecord;

import com.androidrecord.query.ColumnHelper;
import junit.framework.TestCase;

public class ColumnHelperTest extends TestCase {

    public void testSqlTypeForString() throws Exception {
        assertEquals("text", ColumnHelper.sqlTypeFor(String.class));
    }

    public void testSqlTypeForInteger() throws Exception {
        assertEquals("integer", ColumnHelper.sqlTypeFor(Integer.class));
        assertEquals("integer", ColumnHelper.sqlTypeFor(int.class));
    }

    public void testSqlTypeForDateTime() throws Exception {
        assertEquals("varchar(19)", ColumnHelper.sqlTypeFor(DateTime.class));
    }

    public void testSqlTypeForBoolean() throws Exception {
        assertEquals("integer", ColumnHelper.sqlTypeFor(Boolean.class));
        assertEquals("integer", ColumnHelper.sqlTypeFor(boolean.class));
    }
}

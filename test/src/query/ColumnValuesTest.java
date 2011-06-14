package query;

import com.androidrecord.ActiveRecordBase;
import junit.framework.TestCase;

public class ColumnValuesTest extends TestCase {

    protected Record record;

    protected void setUp() throws Exception {
        super.setUp();
        record = new Record();
    }

    public void testBooleanFieldContentValues() throws Exception {
        record.boolean_field = true;

        assertEquals(1, record.contentValues().get("boolean_field"));

        record.boolean_field = false;
        assertEquals(0, record.contentValues().get("boolean_field"));
    }

    public void testIntegerFieldContentValues() throws Exception {
        record.integer_field = 1234;

        assertEquals(1234, record.contentValues().get("integer_field"));
    }

    public void testNullDateFields() throws Exception {
        record.created_at = null;

        assertEquals(null, record.contentValues().get("created_at"));
    }

    private class Record extends ActiveRecordBase<Record> {
        public boolean boolean_field;
        public int integer_field;
    }
}

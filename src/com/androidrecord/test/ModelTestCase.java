package com.androidrecord.test;

import android.content.res.Resources;
import android.test.mock.MockContext;
import android.test.mock.MockResources;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.test.mocks.MockDatabase;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public abstract class ModelTestCase extends TestCase {
    protected MyMockContext context;
    protected MockDatabase db;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new MyMockContext();

        db = new MockDatabase();
        db.returnEmptyResult().forever();

        ActiveRecordBase.bootStrap(db, context);
    }

    public void assertUpdated(ActiveRecordBase record) {
        assertTrue(db.updateCalled);
        assertEquals(record.tableName(), db.lastQueryParameters.get("tableName"));
        assertEquals(record.contentValues(), db.lastQueryParameters.get("contentValues"));
        assertEquals("id=" + record.id, db.lastQueryParameters.get("whereClause"));
    }

    public class MyMockContext extends MockContext {
        private Map<Integer, String> resources = new HashMap<Integer, String>();

        public Resources getResources() {
            return new MockResources() {
                public String getString(int id) {
                    if (resources.containsKey(id)) return resources.get(id);
                    return "Easy Shopping";
                }
            };
        }

        public void mockStringResource(int id, String string) {
            resources.put(id, string);
        }
    }
}

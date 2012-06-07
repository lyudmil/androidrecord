package com.androidrecord.test;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;
import android.test.mock.MockResources;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.migrations.Migrations;
import com.androidrecord.test.mocks.MockDatabase;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelTestCase<T extends ActiveRecordBase> extends InstrumentationTestCase {
    protected MyMockContext context;
    protected MockDatabase db;
    protected Migrations migrations;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new MyMockContext();

        db = new MockDatabase();
        db.returnEmptyResult().forever();

        AssetManager applicationAssets = getInstrumentation().getTargetContext().getAssets();
        migrations = new Migrations(applicationAssets);

        ActiveRecordBase.bootStrap(db, context);
    }

    public void assertUpdated(ActiveRecordBase record) {
        assertTrue(db.updateCalled);
        assertEquals(record.tableName(), db.lastQueryParameters.get("tableName"));
        assertEquals(record.contentValues(), db.lastQueryParameters.get("contentValues"));
        assertEquals("id=" + record.id, db.lastQueryParameters.get("whereClause"));
    }

    protected void assertDatabaseTableCreated(Class<T> modelClass, int migrationNumber) {
        List<String> migrationStatements = Arrays.asList(migrations.loadMigrationNumber(migrationNumber));
        String createTableSql = ActiveRecordBase.createSqlFor(modelClass).replace(";", "");

        assertTrue("Create SQL missing. Migration " + migrationNumber + " doesn't contain: '" + createTableSql + "'", migrationStatements.contains(createTableSql));
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

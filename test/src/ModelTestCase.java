

import android.content.Context;
import android.content.res.Resources;
import android.test.mock.MockContext;
import android.test.mock.MockResources;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.db.DatabaseManager;
import com.androidrecord.test.mocks.MockDatabase;
import junit.framework.TestCase;

public abstract class ModelTestCase extends TestCase {
    protected Context context;
    protected MockDatabase db;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new MockContext() {
            public Resources getResources() {
                return new MockResources() {
                    public String getString(int id) {
                        return "Easy Shopping";
                    }
                };
            }
        };

        db = new MockDatabase();
        db.returnEmptyResult().forever();

        ActiveRecordBase.bootStrap(db);
    }

    protected abstract Class getClassUnderTest();

    public void testIsRegisteredInTheDatabaseManager() throws Exception {
        DatabaseManager databaseManager = new DatabaseManager(context);
        assertTrue(databaseManager.isRegistered(getClassUnderTest()));
    }
}

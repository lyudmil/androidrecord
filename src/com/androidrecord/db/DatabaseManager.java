package com.androidrecord.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.R;

import java.util.ArrayList;

import static com.androidrecord.utils.StringHelper.underscorize;

/**
 * Once the models are registered from the root activity and bootStrapDatabase is called, it creates a database for
 * the application if one does not already exist.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private ArrayList<Class> registeredModels = new ArrayList<Class>();
    private Context context;

    public DatabaseManager(Context context) {
        super(context, underscorize(context.getResources().getString(R.string.app_name)), null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createTables(database);
    }

    private void createTables(SQLiteDatabase database) {
        for (Class registeredModel : registeredModels) {
            database.execSQL(ActiveRecordBase.createSqlFor(registeredModel));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    public void registerModel(Class<? extends ActiveRecordBase> record) {
        registeredModels.add(record);
    }

    public void bootStrapDatabase() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Database database = new Database(writableDatabase);
        ActiveRecordBase.bootStrap(database, context);
    }
}

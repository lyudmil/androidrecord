package com.androidrecord.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.R;
import com.androidrecord.migrations.Migrations;

import java.util.ArrayList;

import static com.androidrecord.utils.StringHelper.underscorize;

/**
 * Once the models are registered from the root activity and bootStrapDatabase is called, it creates a database for
 * the application if one does not already exist.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private ArrayList<Class> registeredModels = new ArrayList<Class>();
    private Context context;
    private Migrations migrations;

    private DatabaseManager(Context context, Migrations migrations) {
        super(context, underscorize(context.getResources().getString(R.string.app_name)), null, migrations.latest());
        this.migrations = migrations;
        this.context = context;
    }

    public DatabaseManager(Context context) {
        this(context, new Migrations(context.getAssets()));
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
        try {
            runNecessaryMigrations(sqLiteDatabase, oldVersion, newVersion);
        } catch (SQLiteException e) {
            throw new RuntimeException("Problem running migrations.", e);
        }
    }

    private void runNecessaryMigrations(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        int nextVersion = oldVersion + 1;
        for (int i = nextVersion; i <= newVersion; i++) {
            sqLiteDatabase.execSQL(migrations.loadMigrationNumber(i));
        }
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

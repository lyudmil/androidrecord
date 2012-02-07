package com.androidrecord.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.R;
import com.androidrecord.migrations.Migrations;

import static com.androidrecord.utils.StringHelper.underscorize;

/**
 * Once the models are registered from the root activity and bootStrapDatabase is called, it creates a database for
 * the application if one does not already exist.
 */
public class DatabaseManager extends SQLiteOpenHelper {

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
        runAllMigrations(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try {
            runMigrations(sqLiteDatabase, oldVersion + 1, newVersion);
        } catch (SQLiteException e) {
            throw new RuntimeException("Problem running migrations.", e);
        }
    }

    private void runAllMigrations(SQLiteDatabase database) {
        runMigrations(database, 1, migrations.latest());
    }

    private void runMigrations(SQLiteDatabase sqLiteDatabase, int initialVersion, int finalVersion) {
        for (int i = initialVersion; i <= finalVersion; i++) {
            for (String statement : migrations.loadMigrationNumber(i)) {
                sqLiteDatabase.execSQL(statement);
            }
        }
    }

    public void bootStrapDatabase() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Database database = new Database(writableDatabase);
        ActiveRecordBase.bootStrap(database, context);
    }
}

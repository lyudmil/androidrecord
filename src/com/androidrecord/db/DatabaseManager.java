package com.androidrecord.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.R;

import java.util.ArrayList;

import static com.androidrecord.utils.StringHelper.underscorize;

public class DatabaseManager extends SQLiteOpenHelper {

    private ArrayList<Class> registeredModels = new ArrayList<Class>();

    public DatabaseManager(Context context) {
        super(context, underscorize(context.getResources().getString(R.string.app_name)), null, 1);
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
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public void registerModel(Class<? extends ActiveRecordBase> record) {
        registeredModels.add(record);
    }

    public boolean isRegistered(Class activeRecordClass) {
        return registeredModels.contains(activeRecordClass);
    }

    public void bootStrapDatabase() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Database database = new Database(writableDatabase);
        ActiveRecordBase.bootStrap(database);
    }
}

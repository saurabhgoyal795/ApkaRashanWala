package com.dev.apkarashanwala.DataBaseUtility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by saurabhgoyal on 13/03/18.
 */

public class DatabaseInterface {

    private Context _context;
    private static DatabaseHandler databaseHandler;

    public static void initiateDatabaseCreationIfNotExists(Context context) {
        if(databaseHandler == null) {
            databaseHandler = new DatabaseHandler(context);
        }
    }


    public DatabaseInterface(Context context) {
        _context = context;

        if(databaseHandler == null) {
            databaseHandler = new DatabaseHandler(_context);
        }
    }
    public SQLiteDatabase getWritableDatabase() {
        return databaseHandler.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return databaseHandler.getReadableDatabase();
    }

    private boolean isDatabaseExist() {
        return databaseHandler.isDatabaseExist();
    }


}

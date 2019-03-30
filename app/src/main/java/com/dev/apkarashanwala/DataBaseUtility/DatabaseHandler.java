package com.dev.apkarashanwala.DataBaseUtility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.dev.apkarashanwala.Utility.CommonUtility;
import com.dev.apkarashanwala.db.CartItemDB;
import com.dev.apkarashanwala.db.OrderDB;
import com.dev.apkarashanwala.db.ProductItemDB;
import com.dev.apkarashanwala.db.SubCategoryItemDB;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context mContext;

    private static final int DATABASE_VERSION = 3;
    public static String DATABASE_NAME = "ARW";
    private static boolean mIsCreatingDB;
    private static boolean mIsReadingDB;



    DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mContext = context;

        try {
            getWritableDatabase();
        } catch(SQLiteDatabaseLockedException e) {

        } catch(SQLiteException e) {

        }
    }

    public void reloadDatabaseHandler(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mIsCreatingDB = true;
        ProductItemDB.onCreate(db);
        CartItemDB.onCreate(db);
        OrderDB.onCreate(db);
        SubCategoryItemDB.onCreate(db);
        mIsCreatingDB = false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            ProductItemDB.onUpgrade(db,oldVersion,newVersion);
            CartItemDB.onUpgrade(db,oldVersion,newVersion);
            OrderDB.onUpgrade(db,oldVersion,newVersion);
            SubCategoryItemDB.onUpgrade(db,oldVersion,newVersion);

        } catch(Throwable e) {
        if(CommonUtility.isDebugModeOn) {
            CommonUtility.printStackTrace(e);
        }
    }

    }

    public boolean isDatabaseExist() {
        return mContext.getApplicationContext().getDatabasePath(DATABASE_NAME).exists();
    }



}

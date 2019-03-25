package com.dev.apkarashanwala.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.dev.apkarashanwala.DataBaseUtility.DatabaseInterface;
import com.dev.apkarashanwala.Utility.CommonUtility;
import com.dev.apkarashanwala.init.CustomApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubCategoryItemDB implements Parcelable {

    private static final String TABLE_NAME = "SubCategoryItemDB";
    public int categoryId;
    public int subcatId = 0;
    public String subcatImage;
    public String subcatTitle;

    private static final String COL_SUBCAT_ID = "sId";
    private static final String COL_CATEGORY_ID = "sCategoryId";
    private static final String COL_SUBCAT_IMAGE = "sImage";
    private static final String COL_SUBCAT_TITLE = "sTitle";



    public SubCategoryItemDB() {

    }

    public SubCategoryItemDB(int subcatId, int categoryId, String subcatImage, String subcatTitle) {
        this.subcatId = subcatId;
        this.categoryId = categoryId;
        this.subcatImage = subcatImage;
        this.subcatTitle = subcatTitle;
    }


    public SubCategoryItemDB(Parcel in) {
        subcatId = in.readInt();
        categoryId = in.readInt();
        subcatImage = in.readString();
        subcatTitle = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(subcatId);
        dest.writeInt(categoryId);
        dest.writeString(subcatImage);
        dest.writeString(subcatTitle);
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put(COL_SUBCAT_ID, subcatId);
            json.put(COL_CATEGORY_ID, categoryId);
            json.put(COL_SUBCAT_IMAGE, subcatImage);
            json.put(COL_SUBCAT_TITLE, subcatTitle);

        } catch (JSONException e) {
            if (CommonUtility.isDebugModeOn) {
                CommonUtility.printStackTrace(e);
            }
        }
        return json.toString();
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(COL_SUBCAT_ID, subcatId);
        values.put(COL_CATEGORY_ID, categoryId);
        values.put(COL_SUBCAT_IMAGE, subcatImage);
        values.put(COL_SUBCAT_TITLE, subcatTitle);

        return values;
    }

    private static final String QUERY_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COL_SUBCAT_ID + " INTEGER PRIMARY KEY,"
            + COL_CATEGORY_ID + " INTEGER ,"
            + COL_SUBCAT_IMAGE + " TEXT,"
            + COL_SUBCAT_TITLE + " TEXT)";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if (oldVersion > 1) {
                onCreate(db);
            }
        } catch (Throwable e) {
            if (CommonUtility.isDebugModeOn) {
                CommonUtility.printStackTrace(e);
            }
        }
    }


    public static final boolean add(SQLiteDatabase db, SubCategoryItemDB subcat) {
        if (db == null) {
            Context context = CustomApplication.getContext();
            DatabaseInterface dbInterface = new DatabaseInterface(context);
            db = dbInterface.getWritableDatabase();
        }

        boolean st = db.insert(TABLE_NAME, null, subcat.getValues()) != -1;
        Log.d("NewsAdded", "st is " + st);
        return st;
    }


    public static final SubCategoryItemDB getProductInfo(SQLiteDatabase db, int topicId) throws JSONException {
        if (db == null) {
            Context context = CustomApplication.getContext();
            DatabaseInterface dbInterface = new DatabaseInterface(context);
            db = dbInterface.getReadableDatabase();
        }
        SubCategoryItemDB subcat = null;


        String selection = COL_SUBCAT_ID + "=?";
        String selectionArgs[] = {String.valueOf(topicId)};

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
        try {
            Log.d("TRE", "20111: TRy ");
            if (cursor.moveToFirst()) {
                Log.d("TRE", "20111: TRy  IF ");


                subcat = new SubCategoryItemDB(topicId,
                        cursor.getInt(cursor.getColumnIndex(COL_CATEGORY_ID)),
                        cursor.getString(cursor.getColumnIndex(COL_SUBCAT_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(COL_SUBCAT_TITLE)));
            }
        } finally {
            cursor.close();
        }
        Log.d("TRE", "20113:  " + subcat);
        return subcat;
    }

    public static final ArrayList<SubCategoryItemDB> get(SQLiteDatabase db, int categoryId) {
        if (db == null) {
            Context context = CustomApplication.getContext();
            DatabaseInterface dbInterface = new DatabaseInterface(context);
            db = dbInterface.getReadableDatabase();
        }
        ArrayList<SubCategoryItemDB> subcatList = new ArrayList<SubCategoryItemDB>();
        Cursor cursor = null;
        String selection = COL_CATEGORY_ID + "=? ";
        String selectionArgs[] = {String.valueOf(categoryId)};

        try {
            cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, COL_SUBCAT_ID + " ASC");
            if (cursor.moveToFirst()) {
                do {
                    SubCategoryItemDB subcat = new SubCategoryItemDB(
                            cursor.getInt(cursor.getColumnIndex(COL_SUBCAT_ID)),
                            cursor.getInt(cursor.getColumnIndex(COL_CATEGORY_ID)),
                            cursor.getString(cursor.getColumnIndex(COL_SUBCAT_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(COL_SUBCAT_TITLE)));
                    subcatList.add(subcat);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            if (CommonUtility.isDebugModeOn) {
                e.printStackTrace();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return subcatList;
    }

    public static void deletesubcatData(String categoryId) {
        Context context = CustomApplication.getContext();
        DatabaseInterface dbInterface = new DatabaseInterface(context);
        SQLiteDatabase db = dbInterface.getWritableDatabase();
        String where = COL_CATEGORY_ID + "=?";
        String[] whereArgs = {categoryId};
        db.delete(TABLE_NAME, where, whereArgs);
    }

    public static final Creator<SubCategoryItemDB> CREATOR = new Creator<SubCategoryItemDB>() {
        @Override
        public SubCategoryItemDB createFromParcel(Parcel source) {
            return new SubCategoryItemDB(source);
        }

        @Override
        public SubCategoryItemDB[] newArray(int size) {
            return new SubCategoryItemDB[size];
        }
    };

}


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

public class OrderDB implements Parcelable {

    private static final String TABLE_NAME = "OrderDB";
    public int orderId;
    public String total;
    public String date;


    private static final String COL_ORDER_ID = "sId";
    private static final String COL_TOTAL = "sTotal";
    private static final String COL_DATE = "sDate";

    public OrderDB() {

    }

    public OrderDB(int orderId, String total, String date) {
        this.orderId=orderId;
        this.total=total;
        this.date = date;
    }


    public OrderDB(Parcel in) {
        orderId = in.readInt();
        total = in.readString();
        date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderId);
        dest.writeString(total);
        dest.writeString(date);
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put(COL_ORDER_ID, orderId);
            json.put(COL_TOTAL, total);
            json.put(COL_DATE, date);
        } catch(JSONException e) {
            if(CommonUtility.isDebugModeOn) {
                CommonUtility.printStackTrace(e);
            }
        }
        return json.toString();
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(COL_ORDER_ID, orderId);
        values.put(COL_TOTAL, total);
        values.put(COL_DATE, date);
        return values;
    }

    private static final String QUERY_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COL_ORDER_ID + " INTEGER PRIMARY KEY ,"
            + COL_TOTAL + " TEXT ,"
            + COL_DATE + " TEXT )";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if (oldVersion < 3) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }
        } catch(Throwable e) {
            if(CommonUtility.isDebugModeOn) {
                CommonUtility.printStackTrace(e);
            }
        }
    }


    public static final boolean add(SQLiteDatabase db, OrderDB product) {
        if(db == null) {
            Context context = CustomApplication.getContext();
            DatabaseInterface dbInterface = new DatabaseInterface(context);
            db = dbInterface.getWritableDatabase();
        }

        boolean st = db.insert(TABLE_NAME, null, product.getValues()) != -1;
        Log.d("itemadded","st is "+st);
        return st;
    }

    public static final ArrayList<OrderDB> get(SQLiteDatabase db) {
        if(db == null) {
            Context context = CustomApplication.getContext();
            DatabaseInterface dbInterface = new DatabaseInterface(context);
            db = dbInterface.getReadableDatabase();
        }
        ArrayList<OrderDB> productList = new ArrayList<OrderDB>();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, COL_DATE + " DESC");
            if(cursor.moveToFirst()) {
                do {
                    OrderDB product = new OrderDB(
                            cursor.getInt(cursor.getColumnIndex(COL_ORDER_ID)),
                            cursor.getString(cursor.getColumnIndex(COL_TOTAL)),
                            cursor.getString(cursor.getColumnIndex(COL_DATE)));
                    productList.add(product);
                } while(cursor.moveToNext());
            }
        }catch (Exception e){
            if(CommonUtility.isDebugModeOn){
                e.printStackTrace();
            }
        }finally {
            if(cursor != null)
                cursor.close();
        }

        return productList;
    }

    public static final Creator<OrderDB> CREATOR = new Creator<OrderDB>() {
        @Override
        public OrderDB createFromParcel(Parcel source) {
            return new OrderDB(source);
        }
        @Override
        public OrderDB[] newArray(int size) {
            return new OrderDB[size];
        }
    };

}

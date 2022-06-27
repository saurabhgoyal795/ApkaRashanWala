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

public class CartItemDB implements Parcelable {

    private static final String TABLE_NAME = "CartItemDB";
    public int productId;
    public int categoryId;
    public int quantity;
    public String productImage;
    public String productTitle;
    public String productDescription;
    public String productPrice;
    public String productMrp;


    private static final String COL_PRODUCT_ID = "sId";
    private static final String COL_CATEGORY_ID = "sCategoryId";
    private static final String COL_QUANTITY = "sQuantity";
    private static final String COL_PRODUCT_IMAGE= "sImage";
    private static final String COL_PRODUCT_TITLE = "sTitle";
    private static final String COL_PRODUCT_DESCRIPTION= "sDescription";
    private static final String COL_PRODUCT_PRICE= "sPrice";
    private static final String COL_PRODUCT_MRP= "sMrp";

    public CartItemDB() {

    }

    public CartItemDB(int productId,int categoryId,int quantity,String productImage, String productTitle, String productDescription, String productPrice,String productMrp) {
        this.productId=productId;
        this.categoryId=categoryId;
        this.quantity = quantity;
        this.productImage=productImage;
        this.productTitle=productTitle;
        this.productDescription=productDescription;
        this.productPrice=productPrice;
        this.productMrp=productMrp;
    }


    public CartItemDB(Parcel in) {
        productId = in.readInt();
        categoryId = in.readInt();
        quantity = in.readInt();
        productImage=in.readString();
        productTitle=in.readString();
        productDescription=in.readString();
        productPrice=in.readString();
        productMrp=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeInt(categoryId);
        dest.writeInt(quantity);
        dest.writeString(productImage);
        dest.writeString(productTitle);
        dest.writeString(productDescription);
        dest.writeString(productPrice);
        dest.writeString(productMrp);
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put(COL_PRODUCT_ID, productId);
            json.put(COL_CATEGORY_ID, categoryId);
            json.put(COL_QUANTITY, quantity);
            json.put(COL_PRODUCT_IMAGE, productImage);
            json.put(COL_PRODUCT_TITLE, productTitle);
            json.put(COL_PRODUCT_DESCRIPTION, productDescription);
            json.put(COL_PRODUCT_PRICE, productPrice);
            json.put(COL_PRODUCT_MRP, productMrp);

        } catch(JSONException e) {
            if(CommonUtility.isDebugModeOn) {
                CommonUtility.printStackTrace(e);
            }
        }
        return json.toString();
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_ID, productId);
        values.put(COL_CATEGORY_ID, categoryId);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_PRODUCT_IMAGE, productImage);
        values.put(COL_PRODUCT_TITLE, productTitle);
        values.put(COL_PRODUCT_DESCRIPTION, productDescription);
        values.put(COL_PRODUCT_PRICE, productPrice);
        values.put(COL_PRODUCT_MRP, productMrp);
        return values;
    }

    private static final String QUERY_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COL_PRODUCT_ID + " INTEGER PRIMARY KEY,"
            + COL_CATEGORY_ID + " INTEGER ,"
            + COL_QUANTITY + " INTEGER ,"
            + COL_PRODUCT_IMAGE + " TEXT,"
            + COL_PRODUCT_TITLE + " TEXT,"
            + COL_PRODUCT_DESCRIPTION + " TEXT,"
            + COL_PRODUCT_PRICE + " TEXT,"
            + COL_PRODUCT_MRP + " TEXT)";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
//                if(oldVersion <= 75){
            onCreate(db);
//                }
        } catch(Throwable e) {
            if(CommonUtility.isDebugModeOn) {
                CommonUtility.printStackTrace(e);
            }
        }
    }


    public static final boolean add(SQLiteDatabase db, CartItemDB product) {
        if(db == null) {
            Context context = CustomApplication.getContext();
            DatabaseInterface dbInterface = new DatabaseInterface(context);
            db = dbInterface.getWritableDatabase();
        }

        boolean st = db.insert(TABLE_NAME, null, product.getValues()) != -1;
        Log.d("itemadded","st is "+st);
        return st;
    }

    public static final ArrayList<CartItemDB> get(SQLiteDatabase db) {
        if(db == null) {
            Context context = CustomApplication.getContext();
            DatabaseInterface dbInterface = new DatabaseInterface(context);
            db = dbInterface.getReadableDatabase();
        }
        ArrayList<CartItemDB> productList = new ArrayList<CartItemDB>();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, COL_PRODUCT_ID + " ASC");
            if(cursor.moveToFirst()) {
                do {
                    CartItemDB product = new CartItemDB(
                            cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)),
                            cursor.getInt(cursor.getColumnIndex(COL_CATEGORY_ID)),
                            cursor.getInt(cursor.getColumnIndex(COL_QUANTITY)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TITLE)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_PRICE)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_MRP)));
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
    public static void deleteproductInCart(String productId){
        Context context = CustomApplication.getContext();
        DatabaseInterface dbInterface = new DatabaseInterface(context);
        SQLiteDatabase db = dbInterface.getWritableDatabase();
        String where = COL_PRODUCT_ID + "=?";
        String[] whereArgs = {productId};
        db.delete(TABLE_NAME, where, whereArgs);
    }

    public static void deleteCart(){
        Context context = CustomApplication.getContext();
        DatabaseInterface dbInterface = new DatabaseInterface(context);
        SQLiteDatabase db = dbInterface.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    public static final Creator<CartItemDB> CREATOR = new Creator<CartItemDB>() {
        @Override
        public CartItemDB createFromParcel(Parcel source) {
            return new CartItemDB(source);
        }
        @Override
        public CartItemDB[] newArray(int size) {
            return new CartItemDB[size];
        }
    };

}

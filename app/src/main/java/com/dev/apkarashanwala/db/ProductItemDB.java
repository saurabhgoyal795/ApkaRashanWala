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

public class ProductItemDB implements Parcelable {
    
        private static final String TABLE_NAME = "ProductItemDB";
        public int productId;
        public int categoryId;
        public String productImage;
        public String productTitle;
        public String productDescription;
        public String productPrice;
        public String productMrp;

        private static final String COL_PRODUCT_ID = "sId";
        private static final String COL_CATEGORY_ID = "sCategoryId";
        private static final String COL_PRODUCT_IMAGE= "sImage";
        private static final String COL_PRODUCT_TITLE = "sTitle";
        private static final String COL_PRODUCT_DESCRIPTION= "sDescription";
        private static final String COL_PRODUCT_PRICE= "sPrice";
        private static final String COL_PRODUCT_MRP= "sMrp";

        public ProductItemDB() {

        }

        public ProductItemDB(int productId,int categoryId,String productImage, String productTitle, String productDescription, String productPrice,String productMrp) {
            this.productId=productId;
            this.categoryId=categoryId;
            this.productImage=productImage;
            this.productTitle=productTitle;
            this.productDescription=productDescription;
            this.productPrice=productPrice;
            this.productMrp=productMrp;
        }


        public ProductItemDB(Parcel in) {
            productId = in.readInt();
            categoryId = in.readInt();
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


        public static final boolean add(SQLiteDatabase db, ProductItemDB product) {
            if(db == null) {
                Context context = CustomApplication.getContext();
                DatabaseInterface dbInterface = new DatabaseInterface(context);
                db = dbInterface.getWritableDatabase();
            }

            boolean st = db.insert(TABLE_NAME, null, product.getValues()) != -1;
            Log.d("NewsAdded","st is "+st);
            return st;
        }



        public static final ProductItemDB getProductInfo(SQLiteDatabase db, int topicId) throws JSONException {
            if(db == null) {
                Context context = CustomApplication.getContext();
                DatabaseInterface dbInterface = new DatabaseInterface(context);
                db = dbInterface.getReadableDatabase();
            }
            ProductItemDB product = null;


            String selection = COL_PRODUCT_ID + "=?";
            String selectionArgs[] = {String.valueOf(topicId)};

            Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
            try {
                Log.d("TRE","20111: TRy ");
                if(cursor.moveToFirst()) {
                    Log.d("TRE","20111: TRy  IF ");


                    product = new ProductItemDB(topicId,
                            cursor.getInt(cursor.getColumnIndex(COL_CATEGORY_ID)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TITLE)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_PRICE)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_MRP)));
                }
            } finally {
                cursor.close();
            }
            Log.d("TRE","20113:  "+product);
            return product;
        }

        public static final ArrayList<ProductItemDB> get(SQLiteDatabase db, int categoryId) {
            if(db == null) {
                Context context = CustomApplication.getContext();
                DatabaseInterface dbInterface = new DatabaseInterface(context);
                db = dbInterface.getReadableDatabase();
            }
            ArrayList<ProductItemDB> productList = new ArrayList<ProductItemDB>();
            Cursor cursor = null;
            String selection = COL_CATEGORY_ID + "=? ";
            String selectionArgs[] = {String.valueOf(categoryId) };

            try {
                cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, COL_PRODUCT_ID + " ASC");
                if(cursor.moveToFirst()) {
                    do {
                        ProductItemDB product = new ProductItemDB(
                                cursor.getInt(cursor.getColumnIndex(COL_PRODUCT_ID)),
                                cursor.getInt(cursor.getColumnIndex(COL_CATEGORY_ID)),
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
        public static void deleteproductData(String categoryId){
            Context context = CustomApplication.getContext();
            DatabaseInterface dbInterface = new DatabaseInterface(context);
            SQLiteDatabase db = dbInterface.getWritableDatabase();
            String where = COL_CATEGORY_ID + "=?";
            String[] whereArgs = {categoryId};
            db.delete(TABLE_NAME, where, whereArgs);
        }

        public static final Creator<ProductItemDB> CREATOR = new Creator<ProductItemDB>() {
            @Override
            public ProductItemDB createFromParcel(Parcel source) {
                return new ProductItemDB(source);
            }
            @Override
            public ProductItemDB[] newArray(int size) {
                return new ProductItemDB[size];
            }
        };

}

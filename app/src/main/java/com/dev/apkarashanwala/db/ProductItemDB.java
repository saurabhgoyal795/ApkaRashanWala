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
        public int subcat = 0;
        public String productImage;
        public String productTitle;
        public String productDescription;
        public String productPrice;
        public String productMrp;
        public String facebookUrl;
        public String instaUrl;
        public String profileUrl;
        public String otherUrl;

        private static final String COL_PRODUCT_ID = "sId";
        private static final String COL_CATEGORY_ID = "sCategoryId";
        private static final String COL_PRODUCT_IMAGE= "sImage";
        private static final String COL_PRODUCT_TITLE = "sTitle";
        private static final String COL_PRODUCT_DESCRIPTION= "sDescription";
        private static final String COL_PRODUCT_PRICE= "sPrice";
        private static final String COL_PRODUCT_MRP= "sMrp";
        private static final String COL_SUBCATEGORY_ID = "sSub";
        private static final String COL_FACEBOOK_URL= "fId";
        private static final String COL_INSTA_URL= "sInsta";
        private static final String COL_PROFILE_URL= "sProfileLink";
        private static final String COL_OTHER_URL= "fOtherLink";



    public ProductItemDB() {

        }

        public ProductItemDB(int productId,int categoryId,String productImage, String productTitle, String productDescription, String productPrice,String productMrp,int subcat, String facebookLink, String instaLink, String profileLink, String otherUrl) {
            this.productId=productId;
            this.categoryId=categoryId;
            this.productImage=productImage;
            this.productTitle=productTitle;
            this.productDescription=productDescription;
            this.productPrice=productPrice;
            this.productMrp=productMrp;
            this.subcat = subcat;
            this.facebookUrl = facebookUrl;
            this.instaUrl = instaUrl;
            this.profileUrl = profileUrl;
            this.otherUrl = otherUrl;
        }


        public ProductItemDB(Parcel in) {
            productId = in.readInt();
            categoryId = in.readInt();
            productImage=in.readString();
            productTitle=in.readString();
            productDescription=in.readString();
            productPrice=in.readString();
            productMrp=in.readString();
            subcat = in.readInt();
            facebookUrl = in.readString();
            instaUrl = in.readString();
            profileUrl = in.readString();
            otherUrl = in.readString();

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
            dest.writeInt(subcat);
            dest.writeString(facebookUrl);
            dest.writeString(instaUrl);
            dest.writeString(profileUrl);
            dest.writeString(otherUrl);

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
                json.put(COL_SUBCATEGORY_ID,subcat);
                json.put(COL_FACEBOOK_URL,facebookUrl);
                json.put(COL_INSTA_URL,instaUrl);
                json.put(COL_PROFILE_URL,profileUrl);
                json.put(COL_OTHER_URL,otherUrl);

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
            values.put(COL_SUBCATEGORY_ID,subcat);
            values.put(COL_FACEBOOK_URL,facebookUrl);
            values.put(COL_INSTA_URL,instaUrl);
            values.put(COL_PROFILE_URL,profileUrl);
            values.put(COL_OTHER_URL,otherUrl);
            return values;
        }

        private static final String QUERY_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COL_PRODUCT_ID + " INTEGER PRIMARY KEY,"
                + COL_CATEGORY_ID + " INTEGER ,"
                + COL_PRODUCT_IMAGE + " TEXT,"
                + COL_PRODUCT_TITLE + " TEXT,"
                + COL_PRODUCT_DESCRIPTION + " TEXT,"
                + COL_PRODUCT_PRICE + " TEXT,"
                + COL_PRODUCT_MRP + " TEXT,"
                + COL_SUBCATEGORY_ID + " INTEGER,"
                + COL_FACEBOOK_URL + " TEXT,"
                + COL_INSTA_URL + " TEXT,"
                + COL_PROFILE_URL + " TEXT,"
                + COL_OTHER_URL + " TEXT)";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(QUERY_CREATE_TABLE);
        }

        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {

                if (oldVersion < 5) {
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                    onCreate(db);
                }
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
                    product =  new ProductItemDB(topicId,
                            cursor.getInt(cursor.getColumnIndex(COL_CATEGORY_ID)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_TITLE)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_PRICE)),
                            cursor.getString(cursor.getColumnIndex(COL_PRODUCT_MRP)),
                            cursor.getInt(cursor.getColumnIndex(COL_SUBCATEGORY_ID)),
                            cursor.getString(cursor.getColumnIndex(COL_FACEBOOK_URL)),
                            cursor.getString(cursor.getColumnIndex(COL_INSTA_URL)),
                            cursor.getString(cursor.getColumnIndex(COL_PROFILE_URL)),
                            cursor.getString(cursor.getColumnIndex(COL_OTHER_URL)));
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
                                cursor.getString(cursor.getColumnIndex(COL_PRODUCT_MRP)),
                                cursor.getInt(cursor.getColumnIndex(COL_SUBCATEGORY_ID)),
                                cursor.getString(cursor.getColumnIndex(COL_FACEBOOK_URL)),
                                cursor.getString(cursor.getColumnIndex(COL_INSTA_URL)),
                                cursor.getString(cursor.getColumnIndex(COL_PROFILE_URL)),
                                cursor.getString(cursor.getColumnIndex(COL_OTHER_URL)));
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

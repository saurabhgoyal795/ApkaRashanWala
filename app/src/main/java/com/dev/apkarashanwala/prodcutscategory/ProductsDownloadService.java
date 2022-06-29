package com.dev.apkarashanwala.prodcutscategory;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.dev.apkarashanwala.Utility.ServerInterface;
import com.dev.apkarashanwala.db.ProductItemDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by saurabhgoyal on 13/03/18.
 */

public class ProductsDownloadService extends IntentService {

    public ProductsDownloadService() {
        super("ProductsDownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String categoryId = "20";
        Bundle extras = intent.getExtras();
        if(extras !=  null){
            categoryId = extras.getString("categoryId", categoryId);
        }
        try {
            String response = ServerInterface.doSync("http://apkarashanwala.com/Services/ProductList.php?categoryId="+categoryId);
            try {
                JSONObject responseObject= new JSONObject(response);

                JSONArray dataArray=responseObject.getJSONArray("success");
                ProductItemDB news;
                ProductItemDB.deleteproductData(categoryId);

                for(int i=0;i<dataArray.length();i++){

                    news=new ProductItemDB();
                    news.productId=dataArray.getJSONObject(i).getInt("productId");
                    news.categoryId=dataArray.getJSONObject(i).getInt("categoryId");
                    news.productTitle=dataArray.getJSONObject(i).getString("name");
                    news.productPrice=dataArray.getJSONObject(i).getString("price");
                    news.productDescription=dataArray.getJSONObject(i).getString("description");
                    news.productMrp=dataArray.getJSONObject(i).optString("mrp");
                    news.productImage=dataArray.getJSONObject(i).getString("image");
                    news.subcat = dataArray.getJSONObject(i).optInt("subcat");
                    news.facebookUrl = dataArray.getJSONObject(i).optString("facebookUrl");
                    news.instaUrl = dataArray.getJSONObject(i).optString("instaUrl");
                    news.profileUrl = dataArray.getJSONObject(i).optString("profileLink");
                    news.otherUrl = dataArray.getJSONObject(i).optString("anyotherLink");
                    news.events = dataArray.getJSONObject(i).optString("events");
                    if(dataArray.getJSONObject(i).has("morePhotos")){
                        try{
                            Log.d("MorePhoto", "productData.morePhoto"+dataArray.getJSONObject(i).optJSONArray("morePhotos"));
                            news.morePhotos = dataArray.getJSONObject(i).optJSONArray("morePhotos").toString();
                        } catch (Exception e){
                            try {
                                news.morePhotos = dataArray.getJSONObject(i).optString("morePhotos");
                            } catch (Exception ex){
                                news.morePhotos = "";
                            }
                        }
                    }
                    news.amountLocal = dataArray.getJSONObject(i).optString("amountLocal");
                    news.amountOutSide = dataArray.getJSONObject(i).optString("amountOutside");
                    news.location = dataArray.getJSONObject(i).optString("location");
                    news.contactNumber = dataArray.getJSONObject(i).optString("contactNumber");
                    ProductItemDB.add(null,news);
                }

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(SubCategory.Product_LIST_REFRESH));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

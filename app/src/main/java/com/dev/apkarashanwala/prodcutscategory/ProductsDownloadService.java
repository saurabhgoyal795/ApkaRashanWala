package com.dev.apkarashanwala.prodcutscategory;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

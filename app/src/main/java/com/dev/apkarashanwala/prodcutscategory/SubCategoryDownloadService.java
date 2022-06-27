


package com.dev.apkarashanwala.prodcutscategory;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;


import com.dev.apkarashanwala.Utility.ServerInterface;
import com.dev.apkarashanwala.db.ProductItemDB;
import com.dev.apkarashanwala.db.SubCategoryItemDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by saurabhgoyal on 13/03/18.
 */

public class SubCategoryDownloadService extends IntentService {

    public SubCategoryDownloadService() {
        super("ProductsDownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String categoryId = "20";
        Bundle extras = intent.getExtras();
        if (extras != null) {
            categoryId = extras.getString("categoryId", categoryId);
        }
        try {
            String response = ServerInterface.doSync("http://apkarashanwala.com/Services/subcategory.php?categoryId=" + categoryId);
            try {
                JSONObject responseObject = new JSONObject(response);

                JSONArray dataArray = responseObject.getJSONArray("success");
                SubCategoryItemDB news;
                SubCategoryItemDB.deletesubcatData(categoryId);

                for (int i = 0; i < dataArray.length(); i++) {

                    news = new SubCategoryItemDB();
                    news.subcatId = dataArray.getJSONObject(i).getInt("subcatId");
                    news.categoryId = dataArray.getJSONObject(i).getInt("categoryId");
                    news.subcatTitle = dataArray.getJSONObject(i).getString("name");
                    news.subcatImage = dataArray.getJSONObject(i).getString("imagename");

                    SubCategoryItemDB.add(null, news);
                }

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(SubCategory.SUBCAT_LIST_REFRESH));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

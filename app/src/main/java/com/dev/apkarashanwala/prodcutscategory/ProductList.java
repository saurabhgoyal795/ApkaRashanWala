package com.dev.apkarashanwala.prodcutscategory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.dev.apkarashanwala.Cart;
import com.dev.apkarashanwala.IndividualProduct;
import com.dev.apkarashanwala.R;
import com.dev.apkarashanwala.Utility.CommonUtility;
import com.dev.apkarashanwala.db.ProductItemDB;
import com.dev.apkarashanwala.db.SubCategoryItemDB;
import com.dev.apkarashanwala.init.CustomApplication;
import com.dev.apkarashanwala.networksync.CheckInternetConnection;

import java.util.ArrayList;


public class ProductList extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerView2;
    private GridLayoutManager mLayoutManager;
    private GridLayoutManager mLayoutManager2;
    private LottieAnimationView tv_no_item;
    ArrayList<ProductItemDB> productList;
    ArrayList<SubCategoryItemDB> subCategoryList;
    private ProductItemAdataper adapter;
    private SubCategoryItemAdataper adapter2;
    private boolean isFetched = false;
    private boolean isFetchedSubCat = false;
    String productTitle = "Products";
    RelativeLayout searchViewlayout;
    private ImageView categoryImage;
    TextView productText;
    LinearLayout cartView;
    LinearLayout searchView;
    Button search_button;
    EditText searchEdittext;
    ArrayList<ProductItemDB> startProductList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras !=  null){
            productList = extras.getParcelableArrayList("productList");
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(productTitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();


        //Initializing our Recyclerview
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView2 = findViewById(R.id.my_recycler_view2);

        searchViewlayout = findViewById(R.id.searchViewlayout);
        tv_no_item = findViewById(R.id.tv_no_cards);
        cartView = findViewById(R.id.cartView);
        searchView = findViewById(R.id.searchView);
        productText= findViewById(R.id.productText);
        search_button = findViewById(R.id.search_button);
        searchEdittext = findViewById(R.id.searchEdittext);
        categoryImage = findViewById(R.id.categoryImage);
        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if (mRecyclerView2 != null) {
            //to enable optimization of recyclerview
            mRecyclerView2.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager2 = new GridLayoutManager(getApplicationContext(),1);
        mRecyclerView2.setLayoutManager(mLayoutManager2);
        if(productList.size()==0 ) {
            mRecyclerView.setVisibility(View.GONE);
            tv_no_item.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            tv_no_item.setVisibility(View.GONE);
            if(adapter == null) {
                adapter = new ProductItemAdataper(productList,R.layout.product_list,getApplicationContext());
                mRecyclerView.setAdapter(adapter);
            }else{
                adapter.refreshAdapter(productList);
            }
        }

    }





    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void viewCart(View view) {
        startActivity(new Intent(this, Cart.class));
        finish();
    }

    public void searchItem(View view){
        searchViewlayout.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
        cartView.setVisibility(View.GONE);
        productText.setVisibility(View.GONE);
    }

    public void search(View view){
        if (search_button.getText().toString().equalsIgnoreCase("search")) {
            if (searchEdittext.getText().toString().trim().equalsIgnoreCase("")){
                return;
            }
            search_button.setText("Cancel");
            ArrayList<ProductItemDB> temp = new ArrayList<>();
            for (int i =0 ; i<productList.size();i++){
                if(productList.get(i).productTitle.toLowerCase().contains(searchEdittext.getText().toString().trim().toLowerCase())){
                    temp.add(productList.get(i));
                }
            }
            adapter.refreshAdapter(temp);
        }else  {
            search_button.setText("Search");
            adapter.refreshAdapter(productList);
            searchViewlayout.setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);
            cartView.setVisibility(View.VISIBLE);
            productText.setVisibility(View.VISIBLE);

        }
    }

    public void Notifications(View view) {
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }
}

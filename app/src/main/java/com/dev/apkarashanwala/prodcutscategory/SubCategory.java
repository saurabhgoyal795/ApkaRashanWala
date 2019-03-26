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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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


public class SubCategory extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerView2;
    private GridLayoutManager mLayoutManager;
    private GridLayoutManager mLayoutManager2;
    private LottieAnimationView tv_no_item;
    private ListFetchTask mListFetchTask;
    private ListFetchTask2 mListFetchTask2;
    ArrayList<ProductItemDB> productList;
    ArrayList<SubCategoryItemDB> subCategoryList;
    private ProductItemAdataper adapter;
    private SubCategoryItemAdataper adapter2;
    private boolean isFetched = false;
    private boolean isFetchedSubCat = false;
    int categoryId = 20;
    String productTitle = "Products";
    RelativeLayout searchViewlayout;
    private ImageView categoryImage;
    public static String Product_LIST_REFRESH = "com.productlist.list.refresh";
    public static String SUBCAT_LIST_REFRESH = "com.subcat.list.refresh";
    TextView productText;
    LinearLayout cartView;
    LinearLayout searchView;
    Button search_button;
    EditText searchEdittext;
    ArrayList<ProductItemDB> startProductList;
    FrameLayout frameContainer2;
    FrameLayout frameContainer;

    private BroadcastReceiver event = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isFetched = true;
            getList();
        }
    };
    private BroadcastReceiver event2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isFetchedSubCat = true;
            getCategoryList();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitysubcategory);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras !=  null){
            categoryId = Integer.valueOf(extras.getString("categoryId"));
//            productTitle = extras.getString("title");

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
        frameContainer2 = findViewById(R.id.frame_container2);
        frameContainer = findViewById(R.id.frame_container);
        searchViewlayout = findViewById(R.id.searchViewlayout);
        tv_no_item = findViewById(R.id.tv_no_cards);
        cartView = findViewById(R.id.cartView);
        searchView = findViewById(R.id.searchView);
        productText= findViewById(R.id.productText);
        search_button = findViewById(R.id.search_button);
        searchEdittext = findViewById(R.id.searchEdittext);
        categoryImage = findViewById(R.id.categoryImage);
        Glide.with(getApplicationContext()).load(CustomApplication.imagePath + categoryId+"_cat.jpg").placeholder(R.drawable.noimage).into(categoryImage);

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if (mRecyclerView2 != null) {
            //to enable optimization of recyclerview
            mRecyclerView2.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager2 = new GridLayoutManager(getApplicationContext(),1);
        mRecyclerView2.setLayoutManager(mLayoutManager2);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(event, new IntentFilter(Product_LIST_REFRESH));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(event2, new IntentFilter(SUBCAT_LIST_REFRESH));

        getList();
    }

    private void getList(){
        if(mListFetchTask != null){
            mListFetchTask.cancel(true);
        }
        mListFetchTask = new ListFetchTask();
        mListFetchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getCategoryList(){
        if(mListFetchTask2 != null){
            mListFetchTask2.cancel(true);
        }
        mListFetchTask2 = new ListFetchTask2();
        mListFetchTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class ListFetchTask extends AsyncTask<Void, Void , Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            productList=ProductItemDB.get(null, categoryId);
            return productList.size()!=0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            tv_no_item.setVisibility(View.GONE);
            if (!isFetched && CommonUtility.isConnectedToInternet(getApplicationContext())) {
                getApplicationContext().startService(new Intent(getApplicationContext(),ProductsDownloadService.class).putExtra("categoryId", String.valueOf(categoryId)));
            }
            if (aBoolean) {
                getCategoryList();
            }

        }
    }

    class ListFetchTask2 extends AsyncTask<Void, Void , Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            subCategoryList= SubCategoryItemDB.get(null, categoryId);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            tv_no_item.setVisibility(View.GONE);
            if (!isFetchedSubCat && CommonUtility.isConnectedToInternet(getApplicationContext())) {
                getApplicationContext().startService(new Intent(getApplicationContext(),SubCategoryDownloadService.class).putExtra("categoryId", String.valueOf(categoryId)));
            } else {
                    if(subCategoryList.size()==0 ) {
                        frameContainer2.setVisibility(View.GONE);
                        frameContainer.setVisibility(View.VISIBLE);
                        categoryImage.setVisibility(View.GONE);
                        if(adapter == null) {
                            adapter = new ProductItemAdataper(productList,R.layout.product_list,getApplicationContext());
                            mRecyclerView.setAdapter(adapter);
                        }else{
                            adapter.refreshAdapter(productList);
                        }
                    } else {
                        frameContainer2.setVisibility(View.VISIBLE);
                        frameContainer.setVisibility(View.GONE);
                        categoryImage.setVisibility(View.VISIBLE);
                        ArrayList<SubCategoryItemDB> temp = new ArrayList<>();
                        for (int p = 0; p<subCategoryList.size();p++){
                            for(int k =0; k<productList.size();k++){
                                if(subCategoryList.get(p).subcatId == productList.get(k).subcat ){
                                    temp.add(subCategoryList.get(p));
                                    Log.d("Saurabh", "item "+ productList.get(k));
                                    break;
                                }
                            }
                        }
                        if(adapter2 == null) {
                            adapter2 = new SubCategoryItemAdataper(temp,R.layout.subcatlist,getApplicationContext(),productList);
                            mRecyclerView2.setAdapter(adapter2);
                        }else{
                            adapter2.refreshAdapter(temp);
                        }

                    }
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
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(event);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(event2);


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


class ProductItemAdataper extends RecyclerView.Adapter<ProductItemAdataper.NewsViewHolder> {

    private ArrayList<ProductItemDB> newsItems;
    private int rowLayout;
    private Context context;

    protected class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView cardname;
        ImageView cardimage;
        TextView cardprice;
        TextView cardprice2;
        CardView cardView;


        public NewsViewHolder(View v) {
            super(v);
            cardname = v.findViewById(R.id.cardcategory);
            cardimage = v.findViewById(R.id.cardimage);
            cardprice = v.findViewById(R.id.cardprice);
            cardprice2 = v.findViewById(R.id.cardprice2);
            cardView = v.findViewById(R.id.card_view);
        }
    }

    public ProductItemAdataper(ArrayList<ProductItemDB> news, int rowLayout, Context context) {
        this.newsItems = news;
        this.rowLayout = rowLayout;
        this.context = context;
    }


    @Override
    public ProductItemAdataper.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ProductItemAdataper.NewsViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final ProductItemAdataper.NewsViewHolder holder, int position) {
        holder.cardname.setText(newsItems.get(position).productTitle);
        holder.cardprice.setText("₹"+newsItems.get(position).productMrp);
        holder.cardprice2.setText("Price: ₹"+newsItems.get(position).productPrice);
        holder.cardprice.setPaintFlags(holder.cardprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with(context).load(CustomApplication.imagePath +newsItems.get(position).productImage).placeholder(R.drawable.noimage).into(holder.cardimage);

        CardView RLContainer = holder.cardView;

        View.OnClickListener mClickListener;

        final int pos = position;
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, IndividualProduct.class);
                intent.putExtra("product", newsItems.get(pos));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        };
        RLContainer.setOnClickListener(mClickListener);


    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }
    public void refreshAdapter(ArrayList<ProductItemDB> items) {
        newsItems = items;
        notifyDataSetChanged();
    }

    public ArrayList<ProductItemDB> getList() {
        return newsItems;
    }



}

class SubCategoryItemAdataper extends RecyclerView.Adapter<SubCategoryItemAdataper.NewsViewHolder> {

    private ArrayList<SubCategoryItemDB> newsItems;
    private int rowLayout;
    private Context context;
    private ArrayList<ProductItemDB> productList;

    protected class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView cardname;
        ImageView cardimage;
        CardView cardView;


        public NewsViewHolder(View v) {
            super(v);
            cardname = v.findViewById(R.id.text);
            cardimage = v.findViewById(R.id.image);
            cardView = v.findViewById(R.id.card_view);
        }
    }

    public SubCategoryItemAdataper(ArrayList<SubCategoryItemDB> news, int rowLayout, Context context,ArrayList<ProductItemDB> productList) {
        this.newsItems = news;
        this.rowLayout = rowLayout;
        this.context = context;
        this.productList = productList;
    }


    @Override
    public SubCategoryItemAdataper.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new SubCategoryItemAdataper.NewsViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final SubCategoryItemAdataper.NewsViewHolder holder, int position) {
        holder.cardname.setText(newsItems.get(position).subcatTitle);
        Glide.with(context).load(CustomApplication.imagePath + newsItems.get(position).subcatImage).placeholder(R.drawable.noimage).into(holder.cardimage);
        CardView RLContainer = holder.cardView;

        View.OnClickListener mClickListener;

        final int pos = position;
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ProductList.class);
                ArrayList<ProductItemDB> temp = new ArrayList<>();
                for(int k= 0;k<productList.size();k++) {
                    if (productList.get(k).subcat == newsItems.get(pos).subcatId) {
                        temp.add(productList.get(k));
                    }
                }
                intent.putExtra("productList", temp);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        };
        RLContainer.setOnClickListener(mClickListener);


    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public void refreshAdapter(ArrayList<SubCategoryItemDB> items) {
        newsItems = items;
        notifyDataSetChanged();
    }

    public ArrayList<SubCategoryItemDB> getList() {
        return newsItems;
    }


}
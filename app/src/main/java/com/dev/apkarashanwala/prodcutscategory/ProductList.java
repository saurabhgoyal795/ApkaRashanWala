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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.dev.apkarashanwala.Cart;
import com.dev.apkarashanwala.IndividualProduct;
import com.dev.apkarashanwala.NotificationActivity;
import com.dev.apkarashanwala.R;
import com.dev.apkarashanwala.Utility.CommonUtility;
import com.dev.apkarashanwala.db.ProductItemDB;
import com.dev.apkarashanwala.init.CustomApplication;
import com.dev.apkarashanwala.networksync.CheckInternetConnection;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProductList extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private LottieAnimationView tv_no_item;
    private ListFetchTask mListFetchTask;
    ArrayList<ProductItemDB> productList;
    private ProductItemAdataper adapter;
    private boolean isFetched = false;
    int categoryId = 20;
    String productTitle = "Products";
    public static String Product_LIST_REFRESH = "com.productlist.list.refresh";

    private BroadcastReceiver event = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isFetched = true;
            getList();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
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
        tv_no_item = findViewById(R.id.tv_no_cards);


        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(event, new IntentFilter(Product_LIST_REFRESH));
        getList();

    }

    private void getList(){
        if(mListFetchTask != null){
            mListFetchTask.cancel(true);
        }
        mListFetchTask = new ListFetchTask();
        mListFetchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
            } else {
                if(productList.size()==0 ) {
                    mRecyclerView.setVisibility(View.GONE);
                }
            }
            if (aBoolean) {
                mRecyclerView.setVisibility(View.VISIBLE);

                if(adapter == null) {
                    adapter = new ProductItemAdataper(productList,R.layout.product_list,getApplicationContext());
                    mRecyclerView.setAdapter(adapter);
                }else{
                    adapter.refreshAdapter(productList);
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

    }

    public void viewCart(View view) {
        startActivity(new Intent(this, Cart.class));
        finish();
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
        holder.cardprice.setText("Mrp: ₹ "+newsItems.get(position).productMrp);
        holder.cardprice2.setText("Price: ₹ "+newsItems.get(position).productPrice);
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
package com.dev.apkarashanwala;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.dev.apkarashanwala.db.CartItemDB;
import com.dev.apkarashanwala.init.CustomApplication;
import com.dev.apkarashanwala.models.SingleProductModel;
import com.dev.apkarashanwala.networksync.CheckInternetConnection;
import com.dev.apkarashanwala.usersession.UserSession;

import java.util.ArrayList;
import java.util.HashMap;

public class PreviousOrderDetailsActivity extends AppCompatActivity {

    private UserSession session;
    private HashMap<String, String> user;
    private String name, email, photo, mobile;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    ArrayList<CartItemDB> cartList;
    private LinearLayout activitycartlist;
    private CartItemAdataper2 adapter;
    TextView text_action_bottom2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_order_details);
        Bundle bundle = getIntent().getExtras();
        cartList = (ArrayList<CartItemDB>) bundle.get("cartproducts");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Previous Order");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();
        text_action_bottom2 = findViewById(R.id.text_action_bottom2);
        mRecyclerView = findViewById(R.id.recyclerview);
        activitycartlist = findViewById(R.id.activity_cart_list);

        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setVisibility(View.VISIBLE);

        if (adapter == null) {
            adapter = new CartItemAdataper2(cartList, R.layout.cart_item_layout, PreviousOrderDetailsActivity.this);
            mRecyclerView.setAdapter(adapter);
        } else {
            adapter.refreshAdapter(cartList);
        }

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
    }

}

class CartItemAdataper2 extends RecyclerView.Adapter<CartItemAdataper2.NewsViewHolder> {

    private ArrayList<CartItemDB> newsItems;
    private int rowLayout;
    private Context context;

    protected class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView cardname;
        ImageView cardimage;
        TextView cardprice;
        TextView cardcount;
        ImageView carddelete;

        public NewsViewHolder(View v) {
            super(v);
            cardname = v.findViewById(R.id.cart_prtitle);
            cardimage = v.findViewById(R.id.image_cartlist);
            cardprice = v.findViewById(R.id.cart_prprice);
            cardcount = v.findViewById(R.id.cart_prcount);
            carddelete = v.findViewById(R.id.deletecard);
        }
    }

    public CartItemAdataper2(ArrayList<CartItemDB> news, int rowLayout, Context context) {
        this.newsItems = news;
        this.rowLayout = rowLayout;
        this.context = context;
    }


    @Override
    public CartItemAdataper2.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new CartItemAdataper2.NewsViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final CartItemAdataper2.NewsViewHolder holder, int position) {
        holder.cardname.setText(newsItems.get(position).productTitle);
        holder.cardprice.setText("₹ " + newsItems.get(position).productPrice);
        holder.cardcount.setText("Quantity: " + newsItems.get(position).quantity);
        holder.carddelete.setVisibility(View.GONE);
        Glide.with(context).load(CustomApplication.imagePath + newsItems.get(position).productImage).placeholder(R.drawable.noimage).into(holder.cardimage);
    }
    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public void refreshAdapter(ArrayList<CartItemDB> items) {
        newsItems = items;
        notifyDataSetChanged();
    }

    public ArrayList<CartItemDB> getList() {
        return newsItems;
    }

}


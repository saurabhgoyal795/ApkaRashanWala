package com.dev.apkarashanwala;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import com.dev.apkarashanwala.networksync.CheckInternetConnection;
import com.dev.apkarashanwala.usersession.UserSession;
import java.util.ArrayList;
import java.util.HashMap;

public class Cart extends AppCompatActivity {

    private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    ArrayList<CartItemDB> cartList;
    private CartListFetchTask mListFetchTask;
    private LottieAnimationView tv_no_item;
    private LinearLayout activitycartlist;
    private LottieAnimationView emptycart;
    private CartItemAdataper adapter;
    TextView text_action_bottom2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cart");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        //retrieve session values and display on listviews
        getValues();

        //SharedPreference for Cart Value
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();
        text_action_bottom2 = findViewById(R.id.text_action_bottom2);
        mRecyclerView = findViewById(R.id.recyclerview);
        tv_no_item = findViewById(R.id.tv_no_cards);
        activitycartlist = findViewById(R.id.activity_cart_list);
        emptycart = findViewById(R.id.empty_cart);

        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }
        mLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(session.getCartValue()>0) {
            getList();
        }else if(session.getCartValue() == 0)  {
            tv_no_item.setVisibility(View.GONE);
            activitycartlist.setVisibility(View.GONE);
            emptycart.setVisibility(View.VISIBLE);
        }
    }


    private void getList(){
        if(mListFetchTask != null){
            mListFetchTask.cancel(true);
        }
        mListFetchTask = new CartListFetchTask();
        mListFetchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class CartListFetchTask extends AsyncTask<Void, Void , Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            cartList=CartItemDB.get(null);
            return cartList.size()!=0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            tv_no_item.setVisibility(View.GONE);

                if(cartList.size()==0 ) {
                    mRecyclerView.setVisibility(View.GONE);
                    emptycart.setVisibility(View.VISIBLE);
                    text_action_bottom2.setVisibility(View.GONE);

                }
            if (aBoolean) {
                mRecyclerView.setVisibility(View.VISIBLE);

                if(adapter == null) {
                    adapter = new CartItemAdataper(cartList,R.layout.cart_item_layout,Cart.this);
                    mRecyclerView.setAdapter(adapter);
                }else{
                    adapter.refreshAdapter(cartList);
                }
            }
        }
    }

    public void checkout(View view) {
        try {
            Intent intent = new Intent(Cart.this, OrderDetails.class);
            intent.putExtra("totalprice", Float.toString(adapter.getTotalcost()));
            intent.putExtra("totalproducts", Integer.toString(adapter.getTotalproducts()));
            intent.putExtra("cartproducts", cartList);
            startActivity(intent);
            finish();
        } catch (Exception e){

        }
    }

    private void getValues() {

        //create new session object by passing application context
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();

        //get User details if logged in
        user = session.getUserDetails();

        name = user.get(UserSession.KEY_NAME);
        email = user.get(UserSession.KEY_EMAIL);
        mobile = user.get(UserSession.KEY_MOBiLE);
        photo = user.get(UserSession.KEY_PHOTO);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void viewProfile(View view) {
        startActivity(new Intent(Cart.this,Profile.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

    }

    public void Notifications(View view) {

//        startActivity(new Intent(Cart.this,NotificationActivity.class));
//        finish();
    }

    public void callRefreshAdapter(ArrayList<CartItemDB> items){
        if(items.size() <=0){
            mRecyclerView.setVisibility(View.GONE);
            emptycart.setVisibility(View.VISIBLE);
            text_action_bottom2.setVisibility(View.GONE);
        }
        mRecyclerView.setAdapter(adapter);
        adapter.refreshAdapter(items);
    }
}

class CartItemAdataper extends RecyclerView.Adapter<CartItemAdataper.NewsViewHolder> {

    private ArrayList<CartItemDB> newsItems;
    private int rowLayout;
    private Context context;
    private float totalcost=0;
    private int totalproducts=0;
    DeleteCartItem mList;

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

    public CartItemAdataper(ArrayList<CartItemDB> news, int rowLayout, Context context) {
        this.newsItems = news;
        this.rowLayout = rowLayout;
        this.context = context;
    }


    @Override
    public CartItemAdataper.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new CartItemAdataper.NewsViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final CartItemAdataper.NewsViewHolder holder, int position) {
        holder.cardname.setText(newsItems.get(position).productTitle);
        holder.cardprice.setText("₹ "+newsItems.get(position).productPrice);
        holder.cardcount.setText("Quantity: "+newsItems.get(position).quantity);

        Glide.with(context).load(CustomApplication.imagePath +newsItems.get(position).productImage).placeholder(R.drawable.noimage).into(holder.cardimage);
        totalcost += newsItems.get(position).quantity*Float.valueOf(newsItems.get(position).productPrice);
        totalproducts += newsItems.get(position).quantity;
        ImageView RLContainer = holder.carddelete;

        View.OnClickListener mClickListener;

        final int pos = position;
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mList != null){
                    mList.cancel(true);
                }

                mList = new DeleteCartItem();
                mList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,String.valueOf(newsItems.get(pos).productId),String.valueOf(pos));

            }
        };
        RLContainer.setOnClickListener(mClickListener);
    }

    public class DeleteCartItem extends AsyncTask<String, Void , Boolean> {
        int pos;

        @Override
        protected Boolean doInBackground(String... params) {
            CartItemDB.deleteproductInCart(params[0]);
            pos = Integer.valueOf(params[1]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            newsItems.remove(pos);

            Toast.makeText(context,"Item Removed Successfully",Toast.LENGTH_LONG).show();
            ((Cart)context).callRefreshAdapter(newsItems);
        }
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

    public float getTotalcost() {
        totalcost = 0;
        for (int i =0 ;i<newsItems.size();i++){
            totalcost += newsItems.get(i).quantity*Float.valueOf(newsItems.get(i).productPrice);
        }
        return totalcost;
    }

    public int getTotalproducts() {
        totalproducts = 0;
        for (int i =0 ;i<newsItems.size();i++) {
            totalproducts += newsItems.get(i).quantity;
        }
        return totalproducts;
    }
}

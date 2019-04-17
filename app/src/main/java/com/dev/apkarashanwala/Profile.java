package com.dev.apkarashanwala;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.apkarashanwala.db.CartItemDB;
import com.dev.apkarashanwala.db.OrderDB;
import com.dev.apkarashanwala.init.CustomApplication;
import com.dev.apkarashanwala.networksync.CheckInternetConnection;
import com.dev.apkarashanwala.usersession.UserSession;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.Drawer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    private TextView tvemail,tvphone;

    private TextView namebutton;
    private CircleImageView primage;
//    private TextView updateDetails;
    private LinearLayout addressview;
    private LinearLayout activitycartlist;
    private RecyclerView mRecyclerView;
    private TextView myorderText;

    //to get user session data
    private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile;
    private SliderLayout sliderShow;
    private GridLayoutManager mLayoutManager;
    ArrayList<OrderDB> orderList;
    private OrderListFetchTask mListFetchTask;
    private MyOrderAdataper adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activitycartlist = findViewById(R.id.activity_cart_list);
        mRecyclerView = findViewById(R.id.recyclerview);
        myorderText = findViewById(R.id.myorderText);
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }
        mLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        initialize();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        //retrieve session values and display on listviews
        getValues();

        //ImageSLider
        inflateImageSlider();
        getList();

    }

    private void initialize() {

        addressview = findViewById(R.id.addressview);
        primage=findViewById(R.id.profilepic);
        tvemail=findViewById(R.id.emailview);
        tvphone=findViewById(R.id.mobileview);
        namebutton=findViewById(R.id.name_button);
//        updateDetails=findViewById(R.id.updatedetails);

//        updateDetails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(Profile.this,UpdateData.class));
//                finish();
//            }
//        });

        addressview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this,Wishlist.class));
            }
        });
    }


    private void getValues() {

        //create new session object by passing application context
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();

        //get User details if logged in
        user = session.getUserDetails();

        name=user.get(UserSession.KEY_NAME);
        email=user.get(UserSession.KEY_EMAIL);
        mobile=user.get(UserSession.KEY_MOBiLE);
        photo=user.get(UserSession.KEY_PHOTO);

        //setting values
        tvemail.setText(email);
        tvphone.setText(mobile);
        namebutton.setText(name);
//        Picasso.with(Profile.this).load(photo).into(primage);
    }

    private void inflateImageSlider() {

        // Using Image Slider -----------------------------------------------------------------------
        sliderShow = findViewById(R.id.slider);

        //populating Image slider
        ArrayList<String> sliderImages= new ArrayList<>();
        sliderImages.add("http://aapkarashanwala.com/img/slider/slider1.jpg");
        sliderImages.add("http://aapkarashanwala.com/img/slider/slider2.jpg");

        for (String s:sliderImages){
            DefaultSliderView sliderView=new DefaultSliderView(this);
            sliderView.image(s);
            sliderShow.addSlider(sliderView);
        }

        sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);

    }

    private void getList(){
        if(mListFetchTask != null){
            mListFetchTask.cancel(true);
        }
        mListFetchTask = new OrderListFetchTask();
        mListFetchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class OrderListFetchTask extends AsyncTask<Void, Void , Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            orderList=OrderDB.get(null);
            return orderList.size()!=0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if(orderList.size()==0 ) {
                mRecyclerView.setVisibility(View.GONE);
                activitycartlist.setVisibility(View.GONE);

            }
            if (aBoolean) {
                mRecyclerView.setVisibility(View.VISIBLE);
                myorderText.setVisibility(View.VISIBLE);
                if(adapter == null) {
                    adapter = new MyOrderAdataper(orderList,R.layout.my_order_item_layout,Profile.this);
                    mRecyclerView.setAdapter(adapter);
                }else{
                    adapter.refreshAdapter(orderList);
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void viewCart(View view) {
        startActivity(new Intent(Profile.this,Cart.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    public void Notifications(View view) {
        startActivity(new Intent(Profile.this,NotificationActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();

    }

    public void callRefreshAdapter(ArrayList<OrderDB> items){
        if(items.size() <=0){
            mRecyclerView.setVisibility(View.GONE);
        }
        mRecyclerView.setAdapter(adapter);
        adapter.refreshAdapter(items);
    }

    class MyOrderAdataper extends RecyclerView.Adapter<MyOrderAdataper.NewsViewHolder> {

        private ArrayList<OrderDB> newsItems;
        private int rowLayout;
        private Context context;
        private float totalcost=0;
        private int totalproducts=0;

        protected class NewsViewHolder extends RecyclerView.ViewHolder {
            TextView date;
            TextView transactionId;
            TextView amount;
            LinearLayout layout_item_desc;

            public NewsViewHolder(View v) {
                super(v);
                date = v.findViewById(R.id.date);
                transactionId = v.findViewById(R.id.transactionId);
                amount = v.findViewById(R.id.amount);
                layout_item_desc = v.findViewById(R.id.layout_item_desc);
            }
        }

        public MyOrderAdataper(ArrayList<OrderDB> news, int rowLayout, Context context) {
            this.newsItems = news;
            this.rowLayout = rowLayout;
            this.context = context;
        }


        @Override
        public MyOrderAdataper.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
            return new MyOrderAdataper.NewsViewHolder(view);

        }


        @Override
        public void onBindViewHolder(final MyOrderAdataper.NewsViewHolder holder, int position) {
            holder.date.setText("DATE: "+newsItems.get(position).date);
            holder.transactionId.setText("Transaction ID: #"+newsItems.get(position).orderId);
            holder.amount.setText("₹ "+newsItems.get(position).total);
            final ArrayList<CartItemDB> cartItemDBArrayList = new ArrayList<>();
            try{
                JSONArray json = new JSONArray(newsItems.get(position).data);
                for(int p =0 ; p<json.length();p++){
                  JSONObject jsonObject = json.getJSONObject(p);
                  CartItemDB cartItemDB = new CartItemDB();
                  cartItemDB.productTitle = jsonObject.getString("productTitle");
                  cartItemDB.quantity = jsonObject.getInt("quantity");
                  cartItemDB.productImage = jsonObject.getString("productImage");
                  cartItemDB.productPrice = jsonObject.getString("productPrice");
                  cartItemDBArrayList.add(cartItemDB);
                }
            } catch (Exception e){

            }
            holder.layout_item_desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Profile.this, PreviousOrderDetailsActivity.class);
                    intent.putExtra("cartproducts", cartItemDBArrayList);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return newsItems.size();
        }
        public void refreshAdapter(ArrayList<OrderDB> items) {
            newsItems = items;
            notifyDataSetChanged();
        }

        public ArrayList<OrderDB> getList() {
            return newsItems;
        }
    }
}

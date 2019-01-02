package com.dev.apkarashanwala;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.dev.apkarashanwala.db.CartItemDB;
import com.dev.apkarashanwala.db.ProductItemDB;
import com.dev.apkarashanwala.init.CustomApplication;
import com.dev.apkarashanwala.networksync.CheckInternetConnection;
import com.dev.apkarashanwala.usersession.UserSession;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IndividualProduct extends AppCompatActivity {

    @BindView(R.id.productimage)
    ImageView productimage;
    @BindView(R.id.productname)
    TextView productname;
    @BindView(R.id.productprice)
    TextView productprice;
    @BindView(R.id.add_to_cart)
    TextView addToCart;
    @BindView(R.id.productdesc)
    TextView productdesc;
    @BindView(R.id.quantityProductPage)
    EditText quantityProductPage;
    LottieAnimationView addToWishlist;
    private String usermobile, useremail;
    private int quantity = 1;
    private UserSession session;
    private ProductItemDB productData;
    private AddToCartTask mAddToCartTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);
        ButterKnife.bind(this);
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initialize();

    }

    private void initialize() {
        Bundle extras = getIntent().getExtras();
        productData = extras.getParcelable("product");

        productprice.setText("₹ " + productData.productPrice);

        productname.setText(productData.productTitle);
        productdesc.setText(Html.fromHtml(productData.productDescription));
        quantityProductPage.setText("1");
        Glide.with(getApplicationContext()).load(CustomApplication.imagePath +productData.productImage).placeholder(R.drawable.noimage).into(productimage);

        //SharedPreference for Cart Value
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();
        usermobile = session.getUserDetails().get(UserSession.KEY_MOBiLE);
        useremail = session.getUserDetails().get(UserSession.KEY_EMAIL);

        //setting textwatcher for no of items field
        quantityProductPage.addTextChangedListener(productcount);

        //get firebase instance
        //initializing database reference
    }

    public void Notifications(View view) {
        startActivity(new Intent(IndividualProduct.this, NotificationActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void shareProduct(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Found amazing " + productname.getText().toString() + "on Magic Prints App";
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void similarProduct(View view) {
        finish();
    }

//    private SingleProductModel getProductObject() {
//
//        return new SingleProductModel(model.getCardid(), Integer.parseInt(quantityProductPage.getText().toString()), useremail, usermobile, model.getCardname(), Float.toString(model.getCardprice()), model.getCardimage(), model.carddiscription,customheader.getText().toString(),custommessage.getText().toString());
//
//    }

    public void decrement(View view) {
        if (quantity > 1) {
            quantity--;
            quantityProductPage.setText(String.valueOf(quantity));
        }
    }

    public void increment(View view) {
        if (quantity < 500) {
            quantity++;
            quantityProductPage.setText(String.valueOf(quantity));
        } else {
            Toast.makeText(IndividualProduct.this, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
        }
    }

    TextWatcher productcount = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (quantityProductPage.getText().toString().equals("")) {
                quantityProductPage.setText("0");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //none
            if (Integer.parseInt(quantityProductPage.getText().toString()) >= 20) {
                Toast.makeText(IndividualProduct.this, "Product Count Must be less than 20", Toast.LENGTH_LONG).show();
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    public void addToCart(View view) {
           addToCart(false);
    }
    public void addToCart2(View view) {
        addToCart(true);
    }

    public void viewCart(View view) {
        startActivity(new Intent(this, Cart.class));
        finish();
    }


    private void addToCart(Boolean flag){
        if(mAddToCartTask != null){
            mAddToCartTask.cancel(true);
        }
        mAddToCartTask = new AddToCartTask();
        mAddToCartTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,flag);
    }

    class AddToCartTask extends AsyncTask<Boolean, Void , Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            CartItemDB item=new CartItemDB();
            item.productId=productData.productId;
            item.categoryId=productData.categoryId;
            item.productTitle=productData.productTitle;
            item.productPrice=productData.productPrice;
            item.productDescription=productData.productDescription;
            item.productMrp=productData.productMrp;
            item.productImage=productData.productImage;
            item.quantity = quantity;
            CartItemDB.add(null,item);
            return booleans[0];
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            session.increaseCartValue();
            Toast.makeText(IndividualProduct.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            if (aBoolean){
                startActivity(new Intent(IndividualProduct.this,Cart.class));
                finish();
            }

        }
    }


}
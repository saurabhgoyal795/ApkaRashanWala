package com.dev.apkarashanwala;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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

import static com.dev.apkarashanwala.MainActivity.MY_PERMISSIONS_REQUEST_CALL_PHONE;

public class IndividualAtrtist extends AppCompatActivity {

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
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 1;
    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_artist);
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
        startActivity(new Intent(IndividualAtrtist.this, NotificationActivity.class));
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
            Toast.makeText(IndividualAtrtist.this, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
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
                Toast.makeText(IndividualAtrtist.this, "Product Count Must be less than 20", Toast.LENGTH_LONG).show();
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
    public void callNow(View view) {
        String number = ("tel:9024088049");
        mIntent = new Intent(Intent.ACTION_CALL);
        mIntent.setData(Uri.parse(number));
// Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(IndividualAtrtist.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);

            // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            //You already have permission
            try {
                startActivity(mIntent);
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the phone call
                    startActivity(mIntent);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(),"Please allow calling permission. To allow go to settings and check app permissions there",Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
            Toast.makeText(IndividualAtrtist.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            if (aBoolean){
                startActivity(new Intent(IndividualAtrtist.this,Cart.class));
                finish();
            }

        }
    }


}
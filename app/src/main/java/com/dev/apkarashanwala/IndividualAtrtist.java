package com.dev.apkarashanwala;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.dev.apkarashanwala.db.CartItemDB;
import com.dev.apkarashanwala.db.ProductItemDB;
import com.dev.apkarashanwala.init.CustomApplication;
import com.dev.apkarashanwala.networksync.CheckInternetConnection;
import com.dev.apkarashanwala.usersession.UserSession;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dev.apkarashanwala.MainActivity.MY_PERMISSIONS_REQUEST_CALL_PHONE;

public class IndividualAtrtist extends AppCompatActivity {

    @BindView(R.id.productimage)
    ImageView productimage;
    @BindView(R.id.productname)
    TextView productname;
    @BindView(R.id.yearsofExperience)
    TextView yearsofExperience;
    @BindView(R.id.add_to_cart)
    TextView addToCart;
    @BindView(R.id.productdesc)
    TextView productdesc;
    @BindView(R.id.quantityProductPage)
    EditText quantityProductPage;
    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.place)
    TextView place;
    @BindView(R.id.fblink)
    TextView fblink;
    @BindView(R.id.instaLink)
    TextView instaLink;
    @BindView(R.id.profileLink)
    TextView profileLink;
    @BindView(R.id.anyOtherLink)
    TextView anyOtherLink;
    @BindView(R.id.events)
    TextView events;
    @BindView(R.id.amountLocal)
    TextView amountLocal;
    @BindView(R.id.amountOutside)
    TextView amountOutside;


    LottieAnimationView addToWishlist;
    private String usermobile, useremail;
    private int quantity = 1;
    private UserSession session;
    private ProductItemDB productData;
    private AddToCartTask mAddToCartTask;
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 1;
    Intent mIntent;
    private SliderLayout sliderShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_artist);
        ButterKnife.bind(this);
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        productData = extras.getParcelable("product");
        toolbar.setTitle(productData.productTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initialize();

    }

    private void initialize() {
        yearsofExperience.setText("Years of Experience: " + productData.productPrice);
        if(productData.productMrp.trim().equalsIgnoreCase("1")){
            type.setText("SINGER");
        } else if (productData.productMrp.trim().equalsIgnoreCase("2")){
            type.setText("DANCER");
        }else {
            type.setText("ARTIST");
        }
        place.setText("Place: "+ productData.location);
        if(productData.instaUrl != null && !productData.instaUrl.trim().equalsIgnoreCase("")){
            SpannableString content = new SpannableString("Insta Link: "+productData.instaUrl.trim());
            content.setSpan(new UnderlineSpan(), 12, productData.instaUrl.trim().length()+12, 0);
            content.setSpan(new ForegroundColorSpan(Color.BLUE), 12, productData.instaUrl.trim().length()+12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            instaLink.setText(content);
        } else {
            instaLink.setVisibility(View.GONE);
        }
        instaLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(productData.instaUrl);
            }
        });
        if(productData.facebookUrl != null && !productData.facebookUrl.trim().equalsIgnoreCase("")){
            SpannableString content = new SpannableString("Facebook Link: "+productData.facebookUrl.trim());
            content.setSpan(new UnderlineSpan(), 15, productData.facebookUrl.trim().length() + 15, 0);
            content.setSpan(new ForegroundColorSpan(Color.BLUE), 15, productData.facebookUrl.trim().length() + 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            fblink.setText(content);
        } else {
            fblink.setVisibility(View.GONE);
        }
        fblink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(productData.facebookUrl);
            }
        });

        if(productData.profileUrl != null && !productData.profileUrl.trim().equalsIgnoreCase("")){
            SpannableString content = new SpannableString("Profile Link: "+productData.profileUrl.trim());
            content.setSpan(new UnderlineSpan(), 14, productData.profileUrl.trim().length()+14, 0);
            content.setSpan(new ForegroundColorSpan(Color.BLUE), 14, productData.profileUrl.trim().length()+14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            profileLink.setText(content);
        } else {
            profileLink.setVisibility(View.GONE);
        }

        profileLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(productData.profileUrl);
            }
        });


        if(productData.otherUrl != null && !productData.otherUrl.trim().equalsIgnoreCase("")){
            SpannableString content = new SpannableString("Other Link: "+productData.otherUrl.trim());
            content.setSpan(new UnderlineSpan(), 12, productData.otherUrl.trim().length()+12, 0);
            content.setSpan(new ForegroundColorSpan(Color.BLUE), 12, productData.otherUrl.trim().length()+12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            anyOtherLink.setText(content);
        } else {
            anyOtherLink.setVisibility(View.GONE);
        }

        anyOtherLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(productData.otherUrl);
            }
        });
        if(productData.events != null && !productData.events.trim().equalsIgnoreCase("")){
            events.setText(productData.events);
        }
        if(productData.amountLocal != null && !productData.amountLocal.trim().equalsIgnoreCase("")){
            amountLocal.setText("Amount in "+productData.location+": "+productData.amountLocal);
        }
        if(productData.amountOutSide != null && !productData.amountOutSide.trim().equalsIgnoreCase("")){
            amountOutside.setText("Amount outside "+productData.location+": "+productData.amountOutSide);
        }
        // Using Image Slider -----------------------------------------------------------------------
        sliderShow = findViewById(R.id.slider);

        //populating Image slider
        ArrayList<String> sliderImages= new ArrayList<>();
        sliderImages.add(CustomApplication.imagePath +productData.productImage);
        if(productData.morePhotos != null && !productData.morePhotos.trim().equalsIgnoreCase("")){
            try {
                JSONArray morePhotosJson = new JSONArray(productData.morePhotos);
                for(int k = 0; k< morePhotosJson.length(); k++){
                    if(morePhotosJson.getString(k).contains("http")){
                        sliderImages.add(morePhotosJson.getString(k));
                    } else {
                        sliderImages.add(CustomApplication.imagePath +morePhotosJson.getString(k));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        for (String s:sliderImages){
            DefaultSliderView sliderView=new DefaultSliderView(this);
            sliderView.image(s);
            sliderShow.addSlider(sliderView);
        }

        sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);


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
    private void openLink(String url){
        if (url.startsWith("https://") || url.startsWith("http://")) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "Invalid Url", Toast.LENGTH_SHORT).show();
        }
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
        String number = ("tel:"+productData.contactNumber);
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

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();

    }

}
package com.dev.apkarashanwala;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dev.apkarashanwala.db.CartItemDB;
import com.dev.apkarashanwala.db.OrderDB;
import com.dev.apkarashanwala.networksync.CheckInternetConnection;
import com.dev.apkarashanwala.networksync.OrderDetailsRequest;
import com.dev.apkarashanwala.usersession.UserSession;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetails extends AppCompatActivity {

    TextView deliveryDate;
    TextView noOfItems;
    MaterialEditText ordername;
    MaterialEditText ordernumber;
    MaterialEditText orderaddress;
    MaterialEditText orderpincode;
    ImageView placeorderButton;
    TextView totalAmount;


    private ArrayList<CartItemDB> cartcollect;
    private String payment_mode="COD",order_reference_id;
    private String placed_user_name,getPlaced_user_email,getPlaced_user_mobile_no;
    private UserSession session;
    private HashMap<String,String> user;
    private String currdatetime;
    public static final String ORDERTAG = "MyTag";
    private RequestQueue requestQueue;
    String totalAmounValue ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        totalAmount = findViewById(R.id.total_amount2);
        deliveryDate = findViewById(R.id.delivery_date);
        noOfItems = findViewById(R.id.no_of_items);
        ordername = findViewById(R.id.ordername);
        ordernumber = findViewById(R.id.ordernumber);
        orderaddress = findViewById(R.id.orderaddress);
        orderpincode = findViewById(R.id.orderpincode);
        placeorderButton = findViewById(R.id.placeorderButton);

        requestQueue = Volley.newRequestQueue(OrderDetails.this);//Creating the RequestQueue

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //SharedPreference for Cart Value
        session = new UserSession(getApplicationContext());
        //validating session
        session.isLoggedIn();
        productdetails();

    }

    private void productdetails() {

        Bundle bundle = getIntent().getExtras();

        //setting total price
        totalAmount.setText(bundle.get("totalprice").toString());

        //setting number of products
        noOfItems.setText(bundle.get("totalproducts").toString());

        cartcollect = (ArrayList<CartItemDB>) bundle.get("cartproducts");

        //delivery date
        SimpleDateFormat formattedDate = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
        if (currentHourIn24Format >= 17 ){
            c.add(Calendar.DATE, 2);  // number of days to add
        } else {
            c.add(Calendar.DATE, 1);  // number of days to add
        }
        String tomorrow = (formattedDate.format(c.getTime()));
        deliveryDate.setText(tomorrow);

        user = session.getUserDetails();

        placed_user_name=user.get(UserSession.KEY_NAME);
        getPlaced_user_email=user.get(UserSession.KEY_EMAIL);
        getPlaced_user_mobile_no=user.get(UserSession.KEY_MOBiLE);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currdatetime = sdf.format(new Date());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void PlaceOrder(View view) {
        try {
            if (Integer.parseInt(totalAmount.getText().toString().trim()) < 499) {
                Toast.makeText(getApplicationContext(), "Minimum order value should be more than 500. Please add more item", Toast.LENGTH_LONG).show();
                return;
            }
        }catch (Exception e){
            try {
                if (Float.parseFloat(totalAmount.getText().toString().trim()) < 499) {
                    Toast.makeText(getApplicationContext(), "Minimum order value should be more than 500. Please add more item", Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (Exception ex){
                if (Double.parseDouble(totalAmount.getText().toString().trim()) < 499) {
                    Toast.makeText(getApplicationContext(), "Minimum order value should be more than 500. Please add more item", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        placeorderButton.setClickable(false);
        placeorderButton.setEnabled(false);
        if (validateFields(view)) {
            final KProgressHUD progressDialog=  KProgressHUD.create(OrderDetails.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            JSONArray jsonArray = new JSONArray();
            for (int i=0; i< cartcollect.size();i++){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("product_id", String.valueOf(cartcollect.get(i).productId));
                    jsonObject.put("quantity", String.valueOf(cartcollect.get(i).quantity));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonArray.length()<= 0){
                progressDialog.dismiss();
                placeorderButton.setClickable(true);
                placeorderButton.setEnabled(true);
                Toast.makeText(getApplicationContext(),"Please Add More Items",Toast.LENGTH_SHORT).show();
                return;
            }

            OrderDetailsRequest orderDetailsRequest = new OrderDetailsRequest(ordername.getText().toString(), orderaddress.getText().toString(),ordernumber.getText().toString(),user.get(UserSession.KEY_USERID),user.get(UserSession.KEY_EMAIL),orderpincode.getText().toString(),jsonArray.toString(),user.get(UserSession.KEY_REFID),totalAmount.getText().toString().trim(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();
                    // Response from the server is in the form if a JSON, so we need a JSON Object
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success")) {
                            try {
                                if (Integer.parseInt(totalAmount.getText().toString().trim()) > 1999) {
                                    session.setShowOffer();
                                }
                            }catch (Exception e){
                                try {
                                    if (Float.parseFloat(totalAmount.getText().toString().trim()) > 1999) {
                                        session.setShowOffer();

                                    }
                                } catch (Exception ex){
                                    if (Double.parseDouble(totalAmount.getText().toString().trim()) > 1999) {
                                        session.setShowOffer();
                                    }
                                }
                            }

                            JSONObject successObject = jsonObject.getJSONObject("success");
                            order_reference_id = successObject.getString("payid");
                            totalAmounValue = totalAmount.getText().toString();
                            try {
                                new DeleteCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, order_reference_id);
                            }catch (Exception e ){
                                session.setCartValue(0);
                                Intent intent = new Intent(OrderDetails.this, OrderPlaced.class);
                                intent.putExtra("orderid",order_reference_id);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            progressDialog.dismiss();
                            placeorderButton.setClickable(true);
                            placeorderButton.setEnabled(true);
                            if(jsonObject.has("error"))
                                Toast.makeText(OrderDetails.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            else{
                                Toast.makeText(OrderDetails.this, "Error Occured", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        placeorderButton.setClickable(true);
                        placeorderButton.setEnabled(true);
                        Toast.makeText(OrderDetails.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    placeorderButton.setClickable(true);
                    placeorderButton.setEnabled(true);
                    if (error instanceof ServerError)
                        Toast.makeText(OrderDetails.this, "Server Error", Toast.LENGTH_SHORT).show();
                    else if (error instanceof TimeoutError)
                        Toast.makeText(OrderDetails.this, "Connection Timed Out", Toast.LENGTH_SHORT).show();
                    else if (error instanceof NetworkError)
                        Toast.makeText(OrderDetails.this, "Bad Network Connection", Toast.LENGTH_SHORT).show();
                }
            });
            orderDetailsRequest.setTag(ORDERTAG);
            requestQueue.add(orderDetailsRequest);

        } else {
            placeorderButton.setClickable(true);
            placeorderButton.setEnabled(true);
        }
    }

    public class DeleteCart extends AsyncTask<String, Void , Boolean> {
        int pos;

        @Override
        protected Boolean doInBackground(String... params) {
            OrderDB item=new OrderDB();
            item.orderId = Integer.valueOf(order_reference_id);
            item.total=totalAmounValue;
            item.date=currdatetime;
            JSONArray jsonArray = new JSONArray();
            for (int i= 0 ; i<cartcollect.size();i++){
                CartItemDB cartItemDB = cartcollect.get(i);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("quantity",cartItemDB.quantity);
                    jsonObject.put("productImage",cartItemDB.productImage);
                    jsonObject.put("productTitle",cartItemDB.productTitle);
                    jsonObject.put("productPrice",cartItemDB.productPrice);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            item.data = jsonArray.toString();
            OrderDB.add(null,item);
            CartItemDB.deleteCart();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            session.setCartValue(0);
            Intent intent = new Intent(OrderDetails.this, OrderPlaced.class);
            intent.putExtra("orderid",order_reference_id);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateFields(View view) {

        if (ordername.getText().toString().length() == 0  || ordernumber.getText().toString().length() == 0 || orderaddress.getText().toString().length() == 0 ||
                orderpincode.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Kindly Fill all the fields", Toast.LENGTH_LONG).show();
            return false;
        } else if (orderpincode.getText().toString().length() < 6 || orderpincode.getText().toString().length() > 8){
            orderpincode.setError("Pincode must be of 6 digits");
            return false;
        }

        return true;
    }

    public String getordernumber() {

        return currdatetime.replaceAll("-","");
    }
}

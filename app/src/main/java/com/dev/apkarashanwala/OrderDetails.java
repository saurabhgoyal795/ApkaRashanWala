package com.dev.apkarashanwala;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

    @BindView(R.id.delivery_date)
    TextView deliveryDate;
    @BindView(R.id.no_of_items)
    TextView noOfItems;
    @BindView(R.id.total_amount)
    TextView totalAmount;
    @BindView(R.id.ordername)
    MaterialEditText ordername;
    @BindView(R.id.ordernumber)
    MaterialEditText ordernumber;
    @BindView(R.id.orderaddress)
    MaterialEditText orderaddress;
    @BindView(R.id.orderpincode)
    MaterialEditText orderpincode;

    private ArrayList<CartItemDB> cartcollect;
    private String payment_mode="COD",order_reference_id;
    private String placed_user_name,getPlaced_user_email,getPlaced_user_mobile_no;
    private UserSession session;
    private HashMap<String,String> user;
    private String currdatetime;
    public static final String ORDERTAG = "MyTag";
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
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
        c.add(Calendar.DATE, 7);  // number of days to add
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
                    jsonObject.put("produzzct_id", String.valueOf(cartcollect.get(i).productId));
                    jsonObject.put("quantity", String.valueOf(cartcollect.get(i).quantity));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonArray.length()<= 0){
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Please Add More Items",Toast.LENGTH_SHORT).show();
                return;
            }

            OrderDetailsRequest orderDetailsRequest = new OrderDetailsRequest(ordername.getText().toString(), orderaddress.getText().toString(),ordernumber.getText().toString(),user.get(UserSession.KEY_USERID),user.get(UserSession.KEY_EMAIL),orderpincode.getText().toString(),jsonArray.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();
                    // Response from the server is in the form if a JSON, so we need a JSON Object
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success")) {
                            JSONObject successObject = jsonObject.getJSONObject("success");
                            order_reference_id = successObject.getString("payid");
                            new DeleteCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,order_reference_id);

                        } else {
                            if(jsonObject.has("error"))
                                Toast.makeText(OrderDetails.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            else{
                                Toast.makeText(OrderDetails.this, "Error Occured", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(OrderDetails.this, "Bad Response From Server", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
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

        }
    }

    public class DeleteCart extends AsyncTask<String, Void , Boolean> {
        int pos;

        @Override
        protected Boolean doInBackground(String... params) {
            CartItemDB.deleteCart();
            OrderDB item=new OrderDB();
            item.orderId = order_reference_id;
            item.total=totalAmount.getText().toString();
            item.date=currdatetime;
            OrderDB.add(null,item);
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

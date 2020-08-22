package com.dev.apkarashanwala;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dev.apkarashanwala.db.CartItemDB;
import com.dev.apkarashanwala.networksync.CheckInternetConnection;
import com.dev.apkarashanwala.usersession.UserSession;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderPlaced extends AppCompatActivity {

    @BindView(R.id.orderid)
    TextView orderidview;
    private String orderid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        ButterKnife.bind(this);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        initialize();
        try {
            new DeleteCart2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }catch (Exception e ){

        }
    }

    private void initialize() {
        orderid = getIntent().getStringExtra("orderid");
        orderidview.setText(orderid);
    }

    public void finishActivity(View view) {
        finish();
    }

    public class DeleteCart2 extends AsyncTask<Void, Void , Boolean> {
        int pos;

        @Override
        protected Boolean doInBackground(Void... voids) {
            CartItemDB.deleteCart();

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            try {
                UserSession session = new UserSession(getApplicationContext());
                session.setCartValue(0);
            }catch (Exception e){

            }
        }
    }
}

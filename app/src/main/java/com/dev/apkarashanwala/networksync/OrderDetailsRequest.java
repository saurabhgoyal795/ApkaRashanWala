package com.dev.apkarashanwala.networksync;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kshitij on 12/17/17.
 */

public class OrderDetailsRequest extends StringRequest {
    private static final String CART_URL = "http://apkarashanwala.com/Services/cart.php";
    private Map<String, String> parameters;

    public OrderDetailsRequest(String name, String address,String number,String userId,String email,String pin,String details,String refId,String totalAmount, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, CART_URL, listener, errorListener);
        parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("address", address);
        parameters.put("city", "Jaipur");
        parameters.put("phone", number);
        parameters.put("user_id", userId);
        parameters.put("email", email);
        parameters.put("pin", pin);
        parameters.put("details", details);
        parameters.put("referenceId",refId);
        parameters.put("totalAmount",totalAmount);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        String time = sdf.format(new Date());
        parameters.put("dateTime",time);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
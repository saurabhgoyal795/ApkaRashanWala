package com.dev.apkarashanwala.networksync;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kshitij on 12/17/17.
 */

public class RegisterRequest  extends StringRequest {

    private static final String REGISTER_URL = "http://apkarashanwala.com/Services/signup.php";
    private Map<String, String> parameters;
    public RegisterRequest(String name, String password, String mobile, String email,String referenceId, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("password", password);
        parameters.put("phone", mobile);
        parameters.put("email", email);
        parameters.put("referenceId",referenceId);

    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}

package com.dev.apkarashanwala.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by saurabhgoyal on 13/03/18.
 */

public class CommonUtility {
    public static boolean isDebugModeOn = true;
    public static final String TAG = "Sgit";

    public static boolean isConnectedToInternet(Context context) {
        if(context == null)
            return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if(cm == null)
            return false;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
    public static void printStackTrace(Throwable e) {
        printStackTrace(TAG, e);
    }

    public static void printStackTrace(String tag, Throwable e) {
        if(tag == null || tag.trim().equals("")) {
            tag = TAG;
        }
        String stackTrace = "";
        for(StackTraceElement element: e.getStackTrace()) {
            stackTrace += element.getClassName() + ": " +
                    element.getMethodName() + ": " +
                    element.getLineNumber() + "\n";
        }
        Log.e(tag, "Exception: " + e.getClass());
        Log.e(tag, "Localized Message: " + e.getLocalizedMessage());
        Log.e(tag, "Message: " + e.getMessage());
        Log.e(tag, "StackTrace: " + stackTrace);
        if(e.getCause() != null) {
            printStackTrace(tag, e.getCause());
        }
    }


}

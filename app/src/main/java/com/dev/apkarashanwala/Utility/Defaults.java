package com.dev.apkarashanwala.Utility;

import android.content.Context;

/**
 * Created by saurabhgoyal on 14/03/18.
 */

public class Defaults {

    private static Defaults instance = null;

    private Defaults(Context context) {

    }

    public static Defaults getInstance(Context context) {
        if(instance == null) {
            instance = initInstance(context.getApplicationContext());
        }

        return instance;
    }

    public static Defaults initInstance(Context context) {
        instance = new Defaults(context);
        return instance;
    }
}

package com.dev.apkarashanwala.init;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.activeandroid.ActiveAndroid;
import com.dev.apkarashanwala.DataBaseUtility.DatabaseInterface;
import com.dev.apkarashanwala.R;

import com.bumptech.glide.Glide;
import com.dev.apkarashanwala.Utility.Defaults;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

/**
 * Created by mikepenz on 27.03.15.
 */
public class CustomApplication extends Application {

    private static Context context;
    private static CustomApplication mApplication;
    public static String imagePath = "http://apkarashanwala.com/images/";

    public static Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();


        //Database ORMhelper class
//        ActiveAndroid.initialize(this);

        context = getApplicationContext();
        mApplication = this;
        DatabaseInterface.initiateDatabaseCreationIfNotExists(this);
        Defaults.initInstance(mApplication);

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        mApplication = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
}


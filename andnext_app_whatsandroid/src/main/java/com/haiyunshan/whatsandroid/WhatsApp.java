package com.haiyunshan.whatsandroid;

import android.app.Application;
import club.andnext.navigation.NavigationHelper;

public class WhatsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        NavigationHelper.onCreate(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

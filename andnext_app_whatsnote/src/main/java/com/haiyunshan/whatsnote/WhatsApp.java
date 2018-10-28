package com.haiyunshan.whatsnote;

import android.app.Application;
import club.andnext.navigation.NavigationHelper;

public class WhatsApp extends Application {

    private static WhatsApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        NavigationHelper.onCreate(this);
    }

    public static final WhatsApp getContext() {
        return instance;
    }
}

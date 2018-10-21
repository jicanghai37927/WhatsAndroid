package com.haiyunshan.preview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import club.andnext.utils.ContentUtils;

public class PreviewEntity {

    static final String TAG = PreviewEntity.class.getSimpleName();

    String uri;
    long size;
    String displayName;

    String extraTitle;
    String extraText;

    PreviewEntity(String uri, long size, String displayName) {
        this.uri = uri == null? "": uri;
        this.size = size;
        this.displayName = displayName == null? "": displayName;

        this.extraTitle = "";
        this.extraText = "";
    }

    public Bundle toBundle() {

        Bundle bundle = new Bundle();
        bundle.putString("uri", uri);
        bundle.putLong("size", size);
        bundle.putString("displayName", displayName);

        bundle.putString(Intent.EXTRA_TITLE, extraTitle);
        bundle.putString(Intent.EXTRA_TEXT, extraText);

        return bundle;
    }

    public String getUri() {
        return uri;
    }

    public long getSize() {
        return size;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getExtension() {
        return getExtension(this.uri);
    }

    public String getExtraTitle() {
        return extraTitle;
    }

    public String getExtraText() {
        return extraText;
    }

    public static final PreviewEntity create(Bundle bundle) {
        String uri = bundle.getString("uri");
        long size = bundle.getLong("size");
        String displayName = bundle.getString("displayName");

        PreviewEntity entity = new PreviewEntity(uri, size, displayName);
        {
            entity.extraTitle = bundle.getString(Intent.EXTRA_TITLE, "");
            entity.extraText = bundle.getString(Intent.EXTRA_TEXT, "");
        }

        return entity;
    }

    public static final PreviewEntity create(Context context, Intent intent) {

        String uri = null;
        long size = 0;
        String displayName = null;

        {
            String action = intent.getAction();
            Uri data = null;

            if (action.equalsIgnoreCase(Intent.ACTION_VIEW)) {
                data = intent.getData();
            } else if (action.equalsIgnoreCase(Intent.ACTION_SEND)) {
                data = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            } else if (action.equalsIgnoreCase(Intent.ACTION_SEND_MULTIPLE)) {
                data = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            }

            if (data != null) {
                uri = data.toString();
                size = ContentUtils.getSize(context, data);
                displayName = ContentUtils.getDisplayName(context, data);
            }
        }

        PreviewEntity entity = new PreviewEntity(uri, size, displayName);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            entity.extraTitle = bundle.getString(Intent.EXTRA_TITLE, "");
            entity.extraText = bundle.getString(Intent.EXTRA_TEXT, "");

        }

        return entity;
    }

    public static String getExtension(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return "";
        }

        int index = uri.lastIndexOf('.');
        if (index <= 0) {
            return "";
        }

        return uri.substring(index + 1);
    }
}

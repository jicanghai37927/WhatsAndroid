package com.haiyunshan.preview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import club.andnext.utils.buffer.ByteBuffer;
import club.andnext.utils.CharsetUtils;
import club.andnext.utils.ContentUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class PreviewMessage {

    static final String TAG = PreviewMessage.class.getSimpleName();

    String uri;
    long size;
    String displayName;

    String extraTitle;
    String extraText;

    PreviewMessage(String uri, long size, String displayName) {
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

    public String getFilePath(Context context) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }

        String path = ContentUtils.getFilePath(context, Uri.parse(uri));
        return path;
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

    public String getText(Context context) {
        PreviewMessage entity = this;
        ByteBuffer stream = getBytes(context);
        if (stream == null || stream.size() == 0) {
            return entity.getExtraText();
        }

        int length = 6 * 1024;
        length = (stream.size() > length)? length: stream.size();
        String charset = CharsetUtils.getCharset(stream.getData(), length, "utf-8");

        String str = "";
        try {
            str = new String(stream.getData(), 0, stream.size(), charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }

    public ByteBuffer getBytes(Context context) {
        PreviewMessage entity = this;

        ByteBuffer buf = null;

        String uri = entity.getUri();
        if (TextUtils.isEmpty(uri)) {
            return buf;
        }

        Uri data = Uri.parse(uri);
        String path = ContentUtils.getFilePath(context, data);
        if (TextUtils.isEmpty(path)) {
            buf = ByteBuffer.create(context, data, entity.getSize());
        } else {
            buf = ByteBuffer.create(new File(path));
        }

        return buf;
    }

    public static final PreviewMessage create(Bundle bundle) {
        String uri = bundle.getString("uri");
        long size = bundle.getLong("size");
        String displayName = bundle.getString("displayName");

        PreviewMessage entity = new PreviewMessage(uri, size, displayName);
        {
            entity.extraTitle = bundle.getString(Intent.EXTRA_TITLE, "");
            entity.extraText = bundle.getString(Intent.EXTRA_TEXT, "");
        }

        return entity;
    }

    public static final PreviewMessage create(Context context, Intent intent) {

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

        PreviewMessage entity = new PreviewMessage(uri, size, displayName);

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

package com.haiyunshan.whatsnote;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import club.andnext.utils.PackageUtils;
import com.haiyunshan.whatsnote.extract.ExtractProvider;

public class ExtractActivity extends AppCompatActivity {

    static final String TAG = ExtractActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.handIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        this.handIntent(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.overridePendingTransition(0, 0);
    }

    void handIntent(Intent intent) {
        CharSequence text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);

        Uri uri = add(text);
        if (uri != null) {
            showResult();
        } else {
            onBackPressed();
        }
    }

    Uri add(CharSequence text) {
        Uri uri = ExtractProvider.obtainUri();
        ContentValues values = ExtractProvider.create(text);
        if (values == null) {
            return null;
        }

        uri = this.getContentResolver().insert(uri, values);
        if (uri != null) {
            Log.v(TAG, "uri = " + uri);
            Log.v(TAG, "content = " + text);
        }

        return uri;
    }

    Dialog showResult() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEUTRAL) {
                    PackageUtils.start(ExtractActivity.this, getPackageName());
                }
            }
        };

        Context context = this;
        CharSequence msg = "摘录完成。";
        CharSequence btnView = "查看";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);

        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNeutralButton(btnView, listener);

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onBackPressed();
            }
        });

        Dialog dialog = builder.show();
        return dialog;
    }
}

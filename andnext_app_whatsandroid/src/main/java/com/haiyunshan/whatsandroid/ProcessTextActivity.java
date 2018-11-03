package com.haiyunshan.whatsandroid;

import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.utils.UUIDUtils;

public class ProcessTextActivity extends AppCompatActivity {

    static final String AUTHORITY = "com.haiyunshan.whatsnote.extractprovider";
    static final String PATH_EXTRACT = "extract";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_text);

        if (false) {
            this.getContentResolver().registerContentObserver(obtainUri(), true, new ContentObserver(new Handler()) {
                @Override
                public boolean deliverSelfNotifications() {
                    return super.deliverSelfNotifications();
                }

                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                }

                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);

                    if (selfChange) {
                        return;
                    }

                    Log.w("AA", "uri = " + uri);

                    if (uri != null) {
                        print();
                        getContentResolver().delete(uri, null, null);
                    }
                }
            });
        }

        if (false) {
            findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    print();
                    clear();
                }
            });
        }

        this.handIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        this.handIntent(intent);
    }

    void handIntent(Intent intent) {
        CharSequence text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);

        TextView view = findViewById(R.id.tv_content);
        view.setText(text);

//        add(text);
    }

    void add(CharSequence text) {
        Uri uri = obtainUri();
        ContentValues values = create(text);

        this.getContentResolver().insert(uri, values);
    }

    void print() {
        Cursor cursor = getContentResolver().query(obtainUri(), null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String text = cursor.getString(1);
                    Log.w("AA", text);

                } while (cursor.moveToNext());
            }

            Log.w("AA", "count = " + cursor.getCount());
            cursor.close();
        }
    }

    void clear() {
        getContentResolver().delete(obtainUri(), null, null);
    }

    public static final Uri obtainUri() {
        String text = "content://" + AUTHORITY + "/" + PATH_EXTRACT;
        Uri uri = Uri.parse(text);

        return uri;
    }

    public static final ContentValues create(CharSequence text) {
        ContentValues values = new ContentValues();

        values.put("uuid", UUIDUtils.next());
        values.put("content", text.toString());

        return values;
    }
}

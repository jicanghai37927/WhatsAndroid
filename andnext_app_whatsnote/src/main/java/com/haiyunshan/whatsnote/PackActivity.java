package com.haiyunshan.whatsnote;

import android.text.TextUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class PackActivity extends AppCompatActivity {

    public static final String KEY_FRAGMENT = "pack.fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String name = this.getIntent().getStringExtra(KEY_FRAGMENT);
        if (TextUtils.isEmpty(name)) {
            this.finish();
            return;
        }

        Fragment f = null;

        try {
            Class clz = Class.forName(name);
            f = (Fragment)(clz.newInstance());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        if (f == null) {
            this.finish();
            return;
        }

        f.setArguments(this.getIntent().getExtras());

        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.replace(android.R.id.content, f);
        t.commit();
    }

}

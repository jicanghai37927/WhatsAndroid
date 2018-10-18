package com.haiyunshan.whatsandroid;

import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import club.andnext.navigation.NavigationHelper;

public class DemoFragmentActivity extends AppCompatActivity {

    String name;
    String fragment;

    public static final void start(Activity context, String name, String fragment) {
        Intent intent = new Intent(context, DemoFragmentActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("fragment", fragment);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_demo_fragment);

        {
            NavigationHelper.attach(this);
        }

        {
            Intent intent = this.getIntent();
            this.name = intent.getStringExtra("name");
            this.fragment = intent.getStringExtra("fragment");
        }

        {
            this.setTitle(name);
        }

        if (TextUtils.isEmpty(fragment)) {
            return;
        }

        Fragment f = this.create(fragment);
        if (f == null) {
            return;
        }

        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.replace(R.id.container, f);
            t.commit();
        }
    }

    Fragment create(String name) {

        try {
            Class clz = Class.forName(name);
            Object obj = clz.newInstance();

            return (Fragment)obj;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }
}

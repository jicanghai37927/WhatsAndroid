package com.haiyunshan.whatsandroid;

import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.navigation.NavigationHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        View view;
        int id = android.R.id.content;
        view = findViewById(id);
        Log.w("AA", view.getClass().getName() + " = " + view.getId());

        view = findViewById(R.id.container);
        ViewParent parent = view.getParent();
        while (parent != null) {
            if (parent instanceof View) {
                view = (View) parent;
                Log.w("AA", parent.getClass().getName() + " = " + view.getId());
            }

            parent = parent.getParent();
        }

        getWindow().getDecorView();
    }
}

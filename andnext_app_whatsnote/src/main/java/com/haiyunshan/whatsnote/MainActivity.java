package com.haiyunshan.whatsnote;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.navigation.NavigationHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        NavigationHelper.onCreate(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

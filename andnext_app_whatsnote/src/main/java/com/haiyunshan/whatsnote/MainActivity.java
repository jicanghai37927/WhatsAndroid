package com.haiyunshan.whatsnote;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.haiyunshan.whatsnote.entrance.EntranceFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EntranceFragment f = new EntranceFragment();
        f.setArguments(this.getIntent().getExtras());

        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.replace(android.R.id.content, f);
        t.commit();
    }


}

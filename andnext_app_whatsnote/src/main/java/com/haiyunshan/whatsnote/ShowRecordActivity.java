package com.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.haiyunshan.whatsnote.record.ShowRecordFragment;

public class ShowRecordActivity extends AppCompatActivity {

    public static final void start(Activity context, String parent) {
        Intent intent = new Intent(context, ShowRecordActivity.class);
        intent.putExtra(ShowRecordFragment.KEY_PARENT, parent);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = this.getIntent();
        Bundle args = intent.getExtras();
        args = (args == null)? new Bundle(): args;

        ShowRecordFragment f = new ShowRecordFragment();
        f.setArguments(args);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.replace(android.R.id.content, f);
        t.commit();
    }
}

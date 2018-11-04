package com.haiyunshan.whatsnote;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.haiyunshan.record.RecordEntity;
import com.haiyunshan.whatsnote.record.RecentRecordFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_recent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecent("");
            }
        });

        findViewById(R.id.btn_folder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRecordActivity.start((Activity)(v.getContext()), RecordEntity.ROOT_NOTE);
            }
        });

        findViewById(R.id.btn_extract).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ShowRecordActivity.start((Activity)(v.getContext()), RecordEntity.ROOT_EXTRACT);
            }
        });

        findViewById(R.id.btn_trash).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ShowRecordActivity.start((Activity)(v.getContext()), RecordEntity.ROOT_TRASH);
            }
        });
    }

    void showRecent(String tag) {
        Intent intent = new Intent(this, PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, RecentRecordFragment.class.getName());

        intent.putExtra(RecentRecordFragment.KEY_TAG, tag);

        this.startActivity(intent);
    }
}

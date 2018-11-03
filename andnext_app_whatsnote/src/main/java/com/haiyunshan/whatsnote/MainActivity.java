package com.haiyunshan.whatsnote;

import android.app.Activity;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.haiyunshan.record.RecordEntity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}

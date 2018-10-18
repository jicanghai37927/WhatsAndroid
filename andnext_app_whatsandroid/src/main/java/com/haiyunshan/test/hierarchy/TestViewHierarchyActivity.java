package com.haiyunshan.test.hierarchy;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import club.andnext.navigation.NavigationHelper;
import com.haiyunshan.whatsandroid.R;

public class TestViewHierarchyActivity extends Activity {

    View contentLayout;
    TextView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationHelper.setContentView(this, R.layout.activity_test_view_hierarchy);

//        setContentView(R.layout.activity_test_view_hierarchy);


        {
            this.contentLayout = findViewById(R.id.container);
            this.contentView = findViewById(R.id.tv_content);
        }

        {
            findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), TestViewHierarchyActivity.class);
                    startActivity(intent);
                }
            });
        }

        {
            this.updateInfo();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.updateInfo();
    }

    void updateInfo() {

        StringBuilder sb = new StringBuilder();

        sb.append("设置的ContentView = " + contentLayout.getClass().getName());
        sb.insert(0, '\n');

        ViewParent parent = contentLayout.getParent();
        while (parent != null) {
            String text = getInfo(parent);
            sb.insert(0, '\n');
            sb.insert(0, text);

            parent = parent.getParent();
        }

        contentView.setText(sb);

    }

    String getInfo(ViewParent parent) {
        String name = parent.getClass().getName();

        if (parent instanceof ViewGroup) {
            ViewGroup view = (ViewGroup)parent;
            name += "[" + view.getId() + "]" + "[子控件数 = " + view.getChildCount() + "]";
        } else if (parent instanceof View) {
            View view = (View)parent;
            name += "[" + view.getId() + "]" + "[只是View]";
        }

        return name;
    }
}

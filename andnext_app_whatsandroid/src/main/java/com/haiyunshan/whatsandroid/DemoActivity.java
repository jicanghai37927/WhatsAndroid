package com.haiyunshan.whatsandroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import club.andnext.overscroll.OverScrollHelper;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeHolder;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.utils.GsonUtils;
import club.andnext.utils.PackageUtils;
import com.haiyunshan.dataset.DemoDataset;

public class DemoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BridgeAdapter adapter;

    DemoDataset dataset;

    public static final void start(Activity context, String target) {
        Intent intent = new Intent(context, DemoActivity.class);
        intent.putExtra("target", target);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_demo);

        {
            Intent intent = this.getIntent();
            String target = intent.getStringExtra("target");
            target = (TextUtils.isEmpty(target))? "main_ds.json": target;

            this.dataset = GsonUtils.fromJson(this, "demo/" + target, DemoDataset.class);
        }

        {
            this.setTitle(dataset.getName());
        }

        {
            this.recyclerView = this.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layout);
        }

        {
            MarginDividerDecoration decor = new MarginDividerDecoration(this);
            decor.setMargin(42);
            recyclerView.addItemDecoration(decor);
        }

        {
            OverScrollHelper.attach(recyclerView);
        }

        {
            this.adapter = new BridgeAdapter(this, new BridgeAdapterProvider() {
                @Override
                public Object get(int position) {
                    return dataset.get(position);
                }

                @Override
                public int size() {
                    return dataset.size();
                }
            });

            adapter.bind(DemoDataset.DemoEntity.class,
                    new BridgeBuilder(DemoViewHolder.class, DemoViewHolder.LAYOUT_RES_ID, this));
        }

        {
            recyclerView.setAdapter(adapter);
        }
    }

    void startActivity(String name) {
        try {
            Class clz = Class.forName(name);

            Intent intent = new Intent(this, clz);
            this.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private static class DemoViewHolder extends BridgeHolder<DemoDataset.DemoEntity> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = R.layout.layout_demo_list_item;

        TextView nameView;
        TextView descView;
        View helpView;

        DemoActivity parent;

        public DemoViewHolder(DemoActivity parent, View itemView) {
            super(itemView);

            this.parent = parent;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this);

            this.nameView = view.findViewById(R.id.tv_name);
            this.descView = view.findViewById(R.id.tv_desc);

            this.helpView = view.findViewById(R.id.iv_help);
            helpView.setOnClickListener(this);
        }

        @Override
        public void onBind(DemoDataset.DemoEntity item, int position) {
            nameView.setText(item.getName());
            descView.setText(item.getDesc());

            {
                int visible = (isHelpVisible(item))? View.VISIBLE: View.INVISIBLE;
                helpView.setVisibility(visible);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                DemoDataset.DemoEntity entity = getEntity();

                if (!TextUtils.isEmpty(entity.getTarget())) {
                    DemoActivity.start(parent, entity.getTarget());
                } else if (!TextUtils.isEmpty(entity.getFragment())) {
                    DemoFragmentActivity.start(parent, entity.getName(), entity.getFragment());
                } else if (!TextUtils.isEmpty(entity.getActivity())) {
                    parent.startActivity(entity.getActivity());
                } else if (!TextUtils.isEmpty(entity.getHelp())) {
                    PackageUtils.openBrowser(parent, Uri.parse(entity.getHelp()));
                }

            } else if (v == helpView) {
                DemoDataset.DemoEntity entity = getEntity();

                PackageUtils.openBrowser(parent, Uri.parse(entity.getHelp()));
            }
        }

        boolean isHelpVisible(DemoDataset.DemoEntity entity) {
            if (TextUtils.isEmpty(entity.getHelp())) {
                return false;
            }

            if (TextUtils.isEmpty(entity.getTarget())
                    && TextUtils.isEmpty(entity.getFragment())
                    && TextUtils.isEmpty(entity.getActivity())) {
                return false;
            }

            return true;
        }

        DemoDataset.DemoEntity getEntity() {
            return parent.dataset.get(getAdapterPosition());
        }
    }
}

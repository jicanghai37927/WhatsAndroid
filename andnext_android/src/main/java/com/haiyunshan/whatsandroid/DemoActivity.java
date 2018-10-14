package com.haiyunshan.whatsandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeHolder;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.overscroll.OverScrollHelper;
import club.andnext.utils.GsonUtils;
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
        setContentView(R.layout.activity_demo);

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
                int visible = (TextUtils.isEmpty(item.getHelp()))? View.INVISIBLE: View.VISIBLE;
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
                }
            } else if (v == helpView) {

            }
        }

        DemoDataset.DemoEntity getEntity() {
            return parent.dataset.get(getAdapterPosition());
        }
    }
}

package com.haiyunshan.demo.recyclerview;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.TextView;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.recyclerview.swipe.SwipeHolder;
import club.andnext.recyclerview.swipe.SwipeViewHolder;
import club.andnext.recyclerview.swipe.runner.SnapRightRunner;
import com.haiyunshan.dataset.PiliDataset;
import com.haiyunshan.whatsandroid.R;

public class SwipeRedefineDemoFragment extends BaseSwipeRVDemoFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected BridgeAdapter createAdapter() {
        this.adapter = new BridgeAdapter(getActivity(), new BridgeAdapterProvider<PiliDataset.PiliEntry>() {

            @Override
            public PiliDataset.PiliEntry get(int position) {
                return dataset.get(position);
            }

            @Override
            public int size() {
                return dataset.size();
            }
        });

        adapter.bind(PiliDataset.PiliEntry.class,
                new BridgeBuilder(DemoViewHolder.class, DemoViewHolder.LAYOUT_RES_ID, this));

        return adapter;
    }

    /**
     *
     */
    private static class DemoViewHolder extends SwipeViewHolder<PiliDataset.PiliEntry> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = R.layout.layout_pili_swipe_redefine_list_item;

        SwipeRedefineDemoFragment parent;

        View contentView;

        View redefineBtn;
        TextView nameView;
        TextView poemView;

        public DemoViewHolder(SwipeRedefineDemoFragment parent, View itemView) {
            super(itemView);

            this.parent = parent;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            {
                this.contentView = view.findViewById(R.id.content_layout);

                this.redefineBtn = view.findViewById(R.id.action_redefine);
                this.nameView = view.findViewById(R.id.tv_name);
                this.poemView = view.findViewById(R.id.tv_poem);

                contentView.setOnClickListener(this);

                redefineBtn.setOnClickListener(this);
            }

            {
                SwipeHolder swipe = new SwipeHolder(parent.swipeActionhelper, view, contentView);

                SnapRightRunner r = new SnapRightRunner();
                r.add(redefineBtn);

                swipe.add(r);

                this.setSwipeHolder(swipe);
            }

        }

        @Override
        public void onBind(PiliDataset.PiliEntry item, int position) {
            super.onBind(item, position);

            nameView.setText(item.getName());
            poemView.setText(item.getPoem());

            this.updateColor(item);
        }

        @Override
        public void onActionBegin(SwipeActionHelper helper, int action) {
            if (action == SwipeActionHelper.ACTION_RIGHT) {
                this.redefine();
            }
        }

        @Override
        public void onActionEnd(SwipeActionHelper helper, int action) {

        }

        @Override
        public void onClick(View v) {
            if (v == contentView) {
                this.click();
            } else if (v == redefineBtn) {
                this.redefine();
            }
        }

        void click() {
            int position = this.getAdapterPosition();
            PiliDataset.PiliEntry entity = parent.dataset.get(position);

            Snackbar.make(itemView, entity.getPoem(), Snackbar.LENGTH_LONG).show();

        }

        void redefine() {
            int position = this.getAdapterPosition();
            PiliDataset.PiliEntry entity = parent.dataset.get(position);
            Object obj = (entity.getObject());
            obj = (obj == null)? new Object(): null;
            entity.setObject(obj);

            this.updateColor(entity);
        }

        void updateColor(PiliDataset.PiliEntry entity) {
            if (entity.getObject() != null) {
                nameView.setTextColor(Color.MAGENTA);
                poemView.setTextColor(Color.LTGRAY);
            } else {
                nameView.setTextColor(Color.BLACK);
                poemView.setTextColor(Color.BLACK);
            }
        }
    }
}

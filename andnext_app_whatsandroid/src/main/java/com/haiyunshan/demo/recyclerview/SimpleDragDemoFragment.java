package com.haiyunshan.demo.recyclerview;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.itemtouch.ItemDragHelper;
import com.haiyunshan.dataset.PiliDataset;
import com.haiyunshan.whatsandroid.R;

public class SimpleDragDemoFragment extends BaseSimpleRVDemoFragment {

    ItemDragHelper itemDragHelper;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            marginDividerDecoration.setDrawOver(false);
            marginDividerDecoration.setBackground(new ColorDrawable(Color.WHITE));
        }

        {
            ItemDragHelper helper = new ItemDragHelper(overScrollHelper);
            helper.attachToRecyclerView(recyclerView);
            helper.setLongPressDragEnable(true);
//            helper.setRightEnable(false);
//            helper.setLeftEnable(false);

            this.itemDragHelper = helper;
        }
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
    private static class DemoViewHolder extends BridgeViewHolder<PiliDataset.PiliEntry> implements View.OnClickListener, ItemDragHelper.Adapter, MarginDividerDecoration.Adapter {

        static final int LAYOUT_RES_ID = R.layout.layout_pili_simple_drag_list_item;

        SimpleDragDemoFragment parent;

        TextView nameView;
        TextView poemView;
        View dragView;

        public DemoViewHolder(SimpleDragDemoFragment parent, View itemView) {
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
                view.setOnClickListener(this);

                this.nameView = view.findViewById(R.id.tv_name);
                this.poemView = view.findViewById(R.id.tv_poem);

                this.dragView = view.findViewById(R.id.iv_drag);
                dragView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        parent.itemDragHelper.startDrag(DemoViewHolder.this);

                        return true;
                    }
                });

            }
        }

        @Override
        public void onBind(PiliDataset.PiliEntry item, int position) {

            nameView.setText(item.getName());
            poemView.setText(item.getPoem());
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                this.click();
            }
        }

        @Override
        public boolean isEnable(ItemDragHelper helper) {
            return true;
        }

        @Override
        public void onBegin(ItemDragHelper helper) {
            parent.marginDividerDecoration.setDragViewHolder(this);
        }

        @Override
        public boolean onMove(ItemDragHelper helper, int from, int to) {
            PiliDataset.PiliEntry entity = parent.dataset.remove(from);
            parent.dataset.add(to, entity);

            parent.adapter.notifyItemMoved(from, to);

            return true;
        }

        @Override
        public void onEnd(ItemDragHelper helper) {
            parent.marginDividerDecoration.setDragViewHolder(null);
        }

        @Override
        public float getTranslation(MarginDividerDecoration decoration) {
            return itemView.getTranslationX();
        }

        void click() {
            int position = this.getAdapterPosition();
            PiliDataset.PiliEntry entity = parent.dataset.get(position);

            Snackbar.make(itemView, entity.getPoem(), Snackbar.LENGTH_LONG).show();

        }

    }
}

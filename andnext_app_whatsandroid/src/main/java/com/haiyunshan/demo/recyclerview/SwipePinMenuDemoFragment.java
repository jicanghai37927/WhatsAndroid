package com.haiyunshan.demo.recyclerview;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.recyclerview.swipe.SwipeHolder;
import club.andnext.recyclerview.swipe.SwipeViewHolder;
import club.andnext.recyclerview.swipe.runner.RightActionRunner;
import club.andnext.recyclerview.swipe.runner.SnapLeftRunner;
import club.andnext.utils.AlertDialogUtils;
import com.haiyunshan.dataset.PiliDataset;
import com.haiyunshan.whatsandroid.R;

public class SwipePinMenuDemoFragment extends BaseSwipeRVDemoFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected BridgeAdapter createAdapter() {
        this.adapter = new BridgeAdapter(getActivity(), new BridgeAdapterProvider<PiliDataset.PiliEntity>() {

            @Override
            public PiliDataset.PiliEntity get(int position) {
                return dataset.get(position);
            }

            @Override
            public int size() {
                return dataset.size();
            }
        });

        adapter.bind(PiliDataset.PiliEntity.class,
                new BridgeBuilder(DemoViewHolder.class, DemoViewHolder.LAYOUT_RES_ID, this));

        return adapter;
    }

    /**
     *
     */
    private static class DemoViewHolder extends SwipeViewHolder<PiliDataset.PiliEntity> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = R.layout.layout_pili_swipe_pin_menu_list_item;

        SwipePinMenuDemoFragment parent;

        View contentView;

        View pinBtn;
        ImageView pinView;

        View lockBtn;
        View folderBtn;
        View deleteBtn;

        TextView nameView;
        TextView poemView;

        DemoViewHolder(SwipePinMenuDemoFragment parent, View itemView) {
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
                contentView.setOnClickListener(this);

                this.nameView = view.findViewById(R.id.tv_name);
                this.poemView = view.findViewById(R.id.tv_poem);

            }

            {
                this.pinBtn = view.findViewById(R.id.action_pin);
                pinBtn.setOnClickListener(this);

                this.pinView = view.findViewById(R.id.iv_pin);

                this.lockBtn = view.findViewById(R.id.action_lock);
                lockBtn.setOnClickListener(this);

                this.folderBtn = view.findViewById(R.id.action_folder);
                folderBtn.setOnClickListener(this);

                this.deleteBtn = view.findViewById(R.id.action_delete);
                deleteBtn.setOnClickListener(this);

            }

            {
                SwipeHolder swipe = new SwipeHolder(parent.swipeActionhelper, view, contentView);

                {
                    SnapLeftRunner r = new SnapLeftRunner();
                    r.add(pinBtn);

                    swipe.add(r);
                }

                {
                    RightActionRunner r = new RightActionRunner();
                    r.add(lockBtn);
                    r.add(folderBtn);
                    r.add(deleteBtn);

                    swipe.add(r);
                }

                this.setSwipeHolder(swipe);
            }

        }

        @Override
        public void onBind(PiliDataset.PiliEntity item, int position) {
            super.onBind(item, position);

            nameView.setText(item.getName());
            poemView.setText(item.getPoem());

            updatePin(item, true);
        }

        @Override
        public void onActionBegin(SwipeActionHelper helper, int action) {
            if (action == SwipeActionHelper.ACTION_LEFT) {
                this.pin();
            }
        }

        @Override
        public void onActionEnd(SwipeActionHelper helper, int action) {

        }

        @Override
        public void onClear(SwipeActionHelper helper, int direction) {
            super.onClear(helper, direction);

            if (direction == SwipeActionHelper.DIRECTION_LTR) {
                updatePin(parent.dataset.get(this.getAdapterPosition()), true);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == contentView) {
                this.click();
            } else if (v == pinBtn) {
                this.pin();
            } else if (v == lockBtn) {
                this.lock();
            } else if (v == folderBtn) {
                this.folder();
            } else if (v == deleteBtn) {
                this.delete();
            }
        }

        void click() {
            int position = this.getAdapterPosition();
            PiliDataset.PiliEntity entity = parent.dataset.get(position);

            Snackbar.make(itemView, entity.getPoem(), Snackbar.LENGTH_LONG).show();

        }

        void pin() {
            int position = this.getAdapterPosition();
            PiliDataset.PiliEntity entity = parent.dataset.get(position);
            Object obj = (entity.getObject());
            obj = (obj == null)? new Object(): null;
            entity.setObject(obj);

            this.updatePin(entity, false);
        }

        void lock() {
            AlertDialogUtils.showMessage(getContext(), "Lock");
        }

        void folder() {
            AlertDialogUtils.showMessage(getContext(), "Folder");
        }

        void delete() {
            int position = this.getAdapterPosition();
            parent.dataset.remove(position);
            parent.adapter.notifyItemRemoved(position);
        }

        void updatePin(PiliDataset.PiliEntity entity, boolean changeIcon) {
            Object obj = entity.getObject();
            if (obj != null) {
                nameView.setTextColor(Color.MAGENTA);

                if (changeIcon) {
                    pinView.setImageResource(R.drawable.ic_pin_cancel);
                }
            } else {
                nameView.setTextColor(Color.BLACK);

                if (changeIcon) {
                    pinView.setImageResource(R.drawable.ic_pin);
                }
            }
        }
    }
}

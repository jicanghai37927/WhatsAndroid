package com.haiyunshan.whatsnote.record;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.recyclerview.swipe.SwipeHolder;
import club.andnext.recyclerview.swipe.SwipeViewHolder;
import club.andnext.recyclerview.swipe.runner.RightActionRunner;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.record.entity.RecordEntity;

/**
 *
 * @param <CB>
 */
abstract class RecordViewHolder<CB extends RecordViewHolder.Callback> extends SwipeViewHolder<RecordEntity> implements View.OnClickListener, View.OnLongClickListener {

    static final int LAYOUT_RES_ID = R.layout.layout_note_file_list_item;

    View contentLayout;
    ImageView iconView;
    TextView nameView;
    TextView infoView;

    View deleteBtn;

    CB callback;

    public RecordViewHolder(CB callback, View itemView) {
        super(itemView);

        this.callback = callback;
    }

    @Override
    public int getLayoutResourceId() {
        return LAYOUT_RES_ID;
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view) {

        {
            this.contentLayout = view.findViewById(R.id.content_layout);
            contentLayout.setOnClickListener(this);
            contentLayout.setOnLongClickListener(this);

            this.iconView = view.findViewById(R.id.iv_icon);
            this.nameView = view.findViewById(R.id.tv_name);
            this.infoView = view.findViewById(R.id.tv_info);
        }

        {
            this.deleteBtn = view.findViewById(R.id.btn_delete);
            deleteBtn.setOnClickListener(this);
        }

        {
            SwipeHolder holder = new SwipeHolder(callback.getSwipeActionHelper(), view, contentLayout);

            {
                RightActionRunner r = new RightActionRunner(deleteBtn);
                holder.add(r);
            }

            this.setSwipeHolder(holder);
        }

    }

    @Override
    public void onBind(RecordEntity item, int position) {
        super.onBind(item, position);

        nameView.setText(item.getName());
        infoView.setText(getInfo(item));
    }

    @Override
    @CallSuper
    public void onClick(View v) {
        callback.getSwipeActionHelper().clear();

        if (v == deleteBtn) {
            callback.onRequestDelete(getItem());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == contentLayout) {
            return callback.onLongClick(this, this.getItem());
        }

        return false;
    }

    abstract CharSequence getInfo(RecordEntity item);

    /**
     *
     * @param <CB>
     */
    static abstract class Note<CB extends RecordViewHolder.Callback> extends RecordViewHolder<CB> {

        public Note(CB callback, View itemView) {
            super(callback, itemView);
        }

        @Override
        public void onBind(RecordEntity item, int position) {
            super.onBind(item, position);

            iconView.setImageResource(R.drawable.ic_note_white_24dp);

        }

        @Override
        public void onClick(View v) {
            super.onClick(v);

            if (v == contentLayout) {
                callback.onRequestCompose(getItem());
            }
        }
    }

    /**
     *
     * @param <CB>
     */
    static abstract class Folder<CB extends RecordViewHolder.Callback> extends RecordViewHolder<CB> {

        public Folder(CB callback, View itemView) {
            super(callback, itemView);
        }

        @Override
        public void onBind(RecordEntity item, int position) {
            super.onBind(item, position);

            iconView.setImageResource(R.drawable.ic_folder_white_24dp);

        }

        @Override
        public void onClick(View v) {
            super.onClick(v);

            if (v == contentLayout) {
                callback.onRequestFolder(getItem());
            }
        }
    }

    /**
     *
     */
    public interface Callback {

        SwipeActionHelper getSwipeActionHelper();

        boolean onLongClick(RecordViewHolder viewHolder, RecordEntity item);

        void onRequestCompose(RecordEntity item);

        void onRequestFolder(RecordEntity item);

        void onRequestDelete(RecordEntity item);

    }
}

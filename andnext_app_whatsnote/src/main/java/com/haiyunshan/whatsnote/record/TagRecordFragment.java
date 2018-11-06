package com.haiyunshan.whatsnote.record;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import club.andnext.recyclerview.bridge.*;
import com.haiyunshan.record.RecordEntity;
import com.haiyunshan.record.TagRecordSet;
import com.haiyunshan.whatsnote.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TagRecordFragment extends BaseRecordFragment {

    public static final String KEY_TAG = "recent.tag";

    TagRecordSet tagRecordSet;

    public TagRecordFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            toolbar.inflateMenu(R.menu.menu_record);
            toolbar.setOnMenuItemClickListener(new FolderMenuItemListener(this));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            String tag = getArguments().getString(KEY_TAG, "");
            this.tagRecordSet = TagRecordSet.create(tag);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new RecordProvider());

            adapter.bind(RecordEntity.class,
                    new BridgeBuilder(FolderViewHolder.class, FolderViewHolder.LAYOUT_RES_ID, this),
                    new BridgeBuilder(NoteViewHolder.class, NoteViewHolder.LAYOUT_RES_ID, this));
            adapter.bind(RecordEntity.class, new BridgeFilter<RecordEntity>() {
                @Override
                public Class<? extends BridgeHolder> getHolder(RecordEntity obj) {
                    if (obj.isDirectory()) {
                        return FolderViewHolder.class;
                    }

                    return NoteViewHolder.class;
                }
            });
        }

        {
            recyclerView.setAdapter(adapter);
        }

        {
            getView().findViewById(R.id.btn_create_folder).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_create_note).setVisibility(View.GONE);

            toolbar.getMenu().findItem(R.id.menu_create_note).setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v == createFolderBtn) {

            swipeActionHelper.clear();

            requestCreateFolder();
        } else if (v == createNoteBtn) {

            swipeActionHelper.clear();

            requestCreateNote();;
        }
    }

    @Override
    void create(int type, String name) {

    }

    @Override
    void rename(String id, String name) {
        RecordEntity entity = tagRecordSet.get(id);
        if (entity == null) {
            return;
        }

        boolean equals = entity.getName().equals(name);
        if (equals) {
            return;
        }

        entity.setName(name);
        int position = tagRecordSet.indexOf(entity);
        if (position < 0) {
            return;
        }

        adapter.notifyItemChanged(position);
    }

    @Override
    void requestDelete(RecordEntity entity) {

        int index = tagRecordSet.remove(entity);
        if (index >= 0) {
            adapter.notifyItemRemoved(index);
        }
    }

    /**
     *
     */
    private class RecordProvider implements BridgeAdapterProvider<RecordEntity> {

        @Override
        public RecordEntity get(int position) {
            return tagRecordSet.get(position);
        }

        @Override
        public int size() {
            return tagRecordSet.size();
        }
    }

    /**
     *
     */
    private static class FolderViewHolder extends NoteViewHolder {

        public FolderViewHolder(TagRecordFragment f, View itemView) {
            super(f, itemView);
        }
    }

    /**
     *
     */
    private static class NoteViewHolder extends com.haiyunshan.whatsnote.record.NoteViewHolder<TagRecordFragment> {

        public NoteViewHolder(TagRecordFragment f, View itemView) {
            super(f, itemView);
        }

    }

    /**
     *
     */
    private static class FolderMenuItemListener implements Toolbar.OnMenuItemClickListener {

        final TagRecordFragment parent;

        public FolderMenuItemListener(TagRecordFragment f) {
            this.parent = f;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            parent.swipeActionHelper.clear();

            int id = item.getItemId();
            switch (id) {
                case R.id.menu_create_note: {

                    parent.requestCreateNote();

                    break;
                }
            }

            return false;
        }
    }

}

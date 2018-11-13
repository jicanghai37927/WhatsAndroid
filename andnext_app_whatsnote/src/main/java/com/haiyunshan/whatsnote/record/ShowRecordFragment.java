package com.haiyunshan.whatsnote.record;


import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import club.andnext.recyclerview.bridge.*;
import com.haiyunshan.article.Document;
import com.haiyunshan.extract.ExtractProvider;
import com.haiyunshan.record.RecordEntity;
import com.haiyunshan.whatsnote.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowRecordFragment extends BaseRecordFragment {

    public static final String KEY_PARENT = "parent";

    RecordEntity recordEntity;

    public ShowRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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
            String parent = getArguments().getString(KEY_PARENT, RecordEntity.ROOT_NOTE);
            this.recordEntity = RecordEntity.create(parent);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new FileProvider());

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

        if (recordEntity.isTrash() || recordEntity.isExtract()) {
            getView().findViewById(R.id.btn_create_folder).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_create_note).setVisibility(View.GONE);

            toolbar.getMenu().findItem(R.id.menu_create_note).setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (recordEntity.isExtract()) {
            checkExtract();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        recordEntity.save();
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

    void checkExtract() {
        int count = 0;

        Cursor cursor = getActivity().getContentResolver().query(ExtractProvider.obtainUri(), null, null, null, null);
        if (cursor != null) {
            count = cursor.getCount();

            if (cursor.moveToFirst()) {
                do {

                    String content = cursor.getString(1);
                    long created = cursor.getLong(2);

                    addExtract(content, created);

                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        if (count != 0) {
            getActivity().getContentResolver().delete(ExtractProvider.obtainUri(), null, null);
        }
    }

    void addExtract(String content, long created) {

        RecordEntity entity;

        {
            String name = getTitle(content, 56);
            entity = recordEntity.add(RecordEntity.TYPE_NOTE, name);
            entity.setCreated(created);

            int position = recordEntity.indexOf(entity);
            if (position >= 0) {
                adapter.notifyItemInserted(position);
            }
        }

        if (entity != null) {
            Document.create(entity, content);
        }

    }

    String getTitle(String content, int max) {
        String text = content.trim();
        if (text.length() > max) {
            text = text.substring(0, max).trim();
        }

        int pos = text.indexOf('\n');
        if (pos > 0) {
            return text.substring(0, pos);
        }

        return text;
    }

    @Override
    void create(int type, String name) {
        RecordEntity entity = recordEntity.add(type, name);
        int position = recordEntity.indexOf(entity);
        if (position >= 0) {
            adapter.notifyItemInserted(position);
        }

        if (type == RecordEntity.TYPE_NOTE) {
            requestCompose(entity);
        }
    }

    @Override
    void rename(String id, String name) {
        RecordEntity entity = recordEntity.get(id);
        if (entity == null) {
            return;
        }

        boolean equals = entity.getName().equals(name);
        if (equals) {
            return;
        }

        entity.setName(name);
        int position = recordEntity.indexOf(entity);
        if (position < 0) {
            return;
        }

        adapter.notifyItemChanged(position);
    }

    @Override
    void requestDelete(RecordEntity entity) {

        int index = recordEntity.remove(entity, true);
        if (index >= 0) {
            adapter.notifyItemRemoved(index);
        }
    }

    @Override
    boolean move(String id, String target) {
        boolean result = super.move(id, target);
        if (!result) {
            return result;
        }

        int index = recordEntity.remove(id, false);
        if (index >= 0) {
            adapter.notifyItemRemoved(index);
        }

        return result;
    }

    @Override
    RecordEntity getEntity(String id) {
        return recordEntity.get(id);
    }

    /**
     *
     */
    private class FileProvider implements BridgeAdapterProvider {

        @Override
        public Object get(int position) {
            return recordEntity.get(position);
        }

        @Override
        public int size() {
            return recordEntity.size();
        }
    }

    /**
     *
     */
    private static class FolderViewHolder extends NoteViewHolder {

        public FolderViewHolder(ShowRecordFragment f, View itemView) {
            super(f, itemView);
        }
    }

    /**
     *
     */
    private static class NoteViewHolder extends com.haiyunshan.whatsnote.record.NoteViewHolder<ShowRecordFragment> {

        public NoteViewHolder(ShowRecordFragment f, View itemView) {
            super(f, itemView);
        }

    }

    /**
     *
     */
    private static class FolderMenuItemListener implements Toolbar.OnMenuItemClickListener {

        final ShowRecordFragment parent;

        public FolderMenuItemListener(ShowRecordFragment f) {
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

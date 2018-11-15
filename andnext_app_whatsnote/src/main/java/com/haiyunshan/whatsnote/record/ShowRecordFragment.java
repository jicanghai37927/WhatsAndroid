package com.haiyunshan.whatsnote.record;


import android.app.Activity;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;
import club.andnext.recyclerview.bridge.*;
import com.haiyunshan.whatsnote.article.entity.Document;
import com.haiyunshan.whatsnote.extract.ExtractProvider;
import com.haiyunshan.whatsnote.record.entity.RecordEntity;
import com.haiyunshan.whatsnote.record.entity.RecordFactory;
import com.haiyunshan.whatsnote.record.entity.SortEntity;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.record.entity.SortFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowRecordFragment extends BaseRecordFragment {

    public static final String KEY_PARENT = "record.parent";

    SortedList<RecordEntity> sortedList;
    RecordSortedCallback sortedCallback;

    RecordEntity folderEntity;
    SortEntity sortEntity;

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
            this.sortedCallback = new RecordSortedCallback(adapter);
            this.sortedList = new SortedList<>(RecordEntity.class, sortedCallback, 1024);
        }

        {
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        {
            if (sortEntity == null) {
                sortEntity = SortFactory.create(getActivity(), null);
            }

            {
                String text = String.format("已按%1$s排序", sortEntity.getName());
                sortBtn.setText(text);
            }
        }

        {
            String parent = getArguments().getString(KEY_PARENT, RecordEntity.ROOT_NOTE);
            this.folderEntity = RecordFactory.create(getActivity(), parent);

            if (folderEntity.isExtract()) {
                checkExtract(getActivity(), folderEntity);
            }
        }

        {
            sortedList.addAll(folderEntity.getList());
        }

        if (folderEntity.isTrash() || folderEntity.isExtract()) {
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

        folderEntity.save();
    }

    @Override
    public void onClick(View v) {
        if (v == createFolderBtn) {

            swipeActionHelper.clear();

            requestCreateFolder();
        } else if (v == createNoteBtn) {

            swipeActionHelper.clear();

            requestCreateNote();
        } else {
            super.onClick(v);
        }
    }

    @Override
    void create(int type, String name) {
        RecordEntity entity = folderEntity.add(type, name);
        int position = folderEntity.indexOf(entity);
        if (position >= 0 && entity.isDirectory()) {
            sortedList.add(entity);
        }

        if (type == RecordEntity.TYPE_NOTE) {
            requestCompose(entity);
        }
    }

    @Override
    void rename(String id, String name) {
        RecordEntity entity = folderEntity.get(id);
        if (entity == null) {
            return;
        }

        boolean equals = entity.getName().equals(name);
        if (equals) {
            return;
        }

        entity.setName(name);
        int index = sortedList.indexOf(entity);
        if (index >= 0) {
            sortedList.updateItemAt(index, entity);
        }
    }

    @Override
    void requestDelete(RecordEntity entity) {

        int index = folderEntity.remove(entity.getId(), true);
        if (index >= 0) {
            sortedList.remove(entity);
        }
    }

    @Override
    boolean move(String id, String target) {
        boolean result = super.move(id, target);
        if (!result) {
            return result;
        }

        RecordEntity entity = folderEntity.get(id);
        if (entity == null) {
            return result;
        }

        int index = folderEntity.remove(entity.getId(), false);
        if (index >= 0) {
            sortedList.remove(entity);
        }

        return result;
    }

    @Override
    RecordEntity getEntity(String id) {
        return folderEntity.get(id);
    }

    @Override
    void sort(SortEntity entity) {
        if (sortEntity.getId().equals(entity.getId())) {
            sortEntity.toggle();
        } else {
            sortEntity = entity;
        }

        {
            sortedList.replaceAll(folderEntity.getList());
            recyclerView.scrollToPosition(0);
        }

        {
            String text = String.format("已按%1$s排序", sortEntity.getName());
            sortBtn.setText(text);
        }
    }

    static void checkExtract(Activity context, RecordEntity target) {
        if (!target.isExtract()) {
            return;
        }

        int count = 0;

        Cursor cursor = context.getContentResolver().query(ExtractProvider.obtainUri(), null, null, null, null);
        if (cursor != null) {
            count = cursor.getCount();

            if (cursor.moveToFirst()) {
                do {

                    String content = cursor.getString(1);
                    long created = cursor.getLong(2);

                    {
                        String name = getTitle(content, 56);
                        RecordEntity entity = target.add(RecordEntity.TYPE_NOTE, name);
                        entity.setCreated(created);

                        Document.create(context, entity, content);
                    }

                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        if (count != 0) {
            context.getContentResolver().delete(ExtractProvider.obtainUri(), null, null);
        }
    }

    static String getTitle(String content, int max) {
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

    /**
     *
     */
    private class RecordSortedCallback extends SortedListAdapterCallback<RecordEntity> {

        public RecordSortedCallback(RecyclerView.Adapter adapter) {
            super(adapter);
        }

        @Override
        public int compare(RecordEntity o1, RecordEntity o2) {

            int result = sortEntity.getComparator().compare(o1, o2);

            return result;
        }

        @Override
        public boolean areContentsTheSame(RecordEntity oldItem, RecordEntity newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(RecordEntity item1, RecordEntity item2) {
            return item1.getId().equals(item2.getId());
        }
    }

    /**
     *
     */
    private class RecordProvider implements BridgeAdapterProvider<RecordEntity> {

        @Override
        public RecordEntity get(int position) {
            return sortedList.get(position);
        }

        @Override
        public int size() {
            return sortedList.size();
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

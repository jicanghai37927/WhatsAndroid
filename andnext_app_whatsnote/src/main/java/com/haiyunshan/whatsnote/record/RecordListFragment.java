package com.haiyunshan.whatsnote.record;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;
import club.andnext.recyclerview.bridge.*;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import com.google.android.material.snackbar.Snackbar;
import com.haiyunshan.whatsnote.ShowRecordActivity;
import com.haiyunshan.whatsnote.record.entity.*;
import com.haiyunshan.whatsnote.PackActivity;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.article.ComposeArticleFragment;

import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class RecordListFragment extends Fragment implements View.OnClickListener {

    static final int REQUEST_CREATE_FOLDER  = 1001;
    static final int REQUEST_MOVE           = 1003;
    static final int REQUEST_RENAME         = 2001;
    static final int REQUEST_TAG            = 3001;
    static final int REQUEST_COMPOSE        = 4001;

    protected Toolbar toolbar;

    protected TextView sortBtn;

    protected View createNoteBtn;
    protected View createFolderBtn;

    private RecyclerView recyclerView;
    private BridgeAdapter adapter;
    private MarginDividerDecoration dividerDecoration;
    private SwipeActionHelper swipeActionHelper;

    private SortedList<RecordEntity> sortedList;    // sorted list for adapter
    private SortEntity sortEntity;          // for sort list
    private RecordSortedCallback sortedCallback;

    private RecordItemCallback itemCallback;          // item callback
    private RecordListFragment.Callback callback;     // list callback

    public RecordListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_list, container, false);
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.toolbar = view.findViewById(R.id.toolbar);

            toolbar.inflateMenu(R.menu.menu_record);
            toolbar.setOnMenuItemClickListener(new FolderMenuItemListener());

        }

        {
            this.sortBtn = view.findViewById(R.id.tv_sort);
            sortBtn.setOnClickListener(this);
        }

        {
            this.createFolderBtn = view.findViewById(R.id.btn_create_folder);
            createFolderBtn.setOnClickListener(this);

            this.createNoteBtn = view.findViewById(R.id.btn_create_note);
            createNoteBtn.setOnClickListener(this);
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }

    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.sortEntity = getCallback().getSort();

            if (sortEntity == null) {
                sortEntity = SortFactory.create(getActivity(), null);
            }

            {
                String text = String.format("已按%1$s排序", sortEntity.getName());
                sortBtn.setText(text);
            }
        }

        {
            this.itemCallback = new RecordItemCallback(this);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new RecordProvider());

            adapter.bind(RecordEntity.class,
                    new BridgeBuilder(FolderViewHolder.class, FolderViewHolder.LAYOUT_RES_ID, itemCallback),
                    new BridgeBuilder(NoteViewHolder.class, NoteViewHolder.LAYOUT_RES_ID, itemCallback));

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
            this.dividerDecoration = new MarginDividerDecoration(getActivity());
            dividerDecoration.setMargin(48);
            recyclerView.addItemDecoration(dividerDecoration);
        }

        {
            this.swipeActionHelper = new SwipeActionHelper();
            swipeActionHelper.attach(recyclerView);
        }

        {
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CREATE_FOLDER: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String name = data.getStringExtra(CreateRecordFragment.KEY_NAME);
                    if (!TextUtils.isEmpty(name)) {
                        RecordEntity entity = getCallback().onCreate(RecordEntity.TYPE_FOLDER, name);
                        if (entity != null) {
                            sortedList.add(entity);

                            int position = sortedList.indexOf(entity);
                            recyclerView.scrollToPosition(position);
                        }
                    }
                }

                break;
            }

            case REQUEST_RENAME: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String id = data.getStringExtra(RenameRecordFragment.KEY_ID);
                    String name = data.getStringExtra(RenameRecordFragment.KEY_NAME);
                    if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(name)) {
                        RecordEntity entity = getEntity(id);
                        if (entity != null) {
                            entity = getCallback().onRename(entity, name);
                            if (entity != null) {
                                int index = sortedList.indexOf(entity);
                                if (index >= 0) {
                                    sortedList.updateItemAt(index, entity);
                                }
                            }
                        }
                    }
                }

                break;
            }
            case REQUEST_TAG: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String id = data.getStringExtra(ShowTagFragment.KEY_ID);
                    if (!TextUtils.isEmpty(id)) {

                    }
                }

                break;
            }
            case REQUEST_COMPOSE: {

                break;
            }
            case REQUEST_MOVE: {
                if (resultCode == Activity.RESULT_OK && data != null) {

                    String id = data.getStringExtra(TargetFolderFragment.KEY_ID);
                    String target = data.getStringExtra(TargetFolderFragment.KEY_TARGET);
                    if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(target)) {
                        RecordEntity entity = getEntity(id);
                        if (entity != null) {
                            entity = getCallback().onMove(entity, target);
                            if (entity != null) {
                                sortedList.remove(entity);
                            }
                        }
                    }
                }

                break;
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getCallback().applySort();

    }

    @Override
    @CallSuper
    public void onClick(View v) {
        swipeActionHelper.clear();

        if (v == sortBtn) {
            this.requestSort();
        } else if (v == createFolderBtn) {
            this.requestCreateFolder();
        } else if (v == createNoteBtn) {
            this.requestCreateNote();
        }
    }

    abstract Callback createCallback();

    protected SortEntity createSort() {
        return null;
    }

    final Callback getCallback() {
        if (callback != null) {
            return callback;
        }

        callback = createCallback();
        return callback;
    }

    final SortEntity getSort() {
        return this.sortEntity;
    }

    final void setSort(SortEntity sort) {
        this.sortEntity = sort;
    }

    final void addAll(Collection<RecordEntity> items) {
        sortedList.addAll(items);
    }

    final void replaceAll(Collection<RecordEntity> items) {
        sortedList.replaceAll(items);
    }

    final void requestCreateFolder() {

        Intent intent = new Intent(getActivity(), PackActivity.class);
        intent.putExtra(PackActivity.KEY_FRAGMENT, CreateRecordFragment.class.getName());
        intent.putExtra(CreateRecordFragment.KEY_HINT, "新建文件夹");
        intent.putExtra(CreateRecordFragment.KEY_NAME, "新建文件夹");

        this.startActivityForResult(intent, REQUEST_CREATE_FOLDER);
    }

    final void requestCreateNote() {

        RecordEntity entity = getCallback().onCreate(RecordEntity.TYPE_NOTE, "空白笔记");
        if (entity != null) {
            sortedList.add(entity);
        }

    }

    final void requestSort() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        {
            builder.setTitle("排序方式：");
        }

        {
            final SortEntity entity = SortFactory.all(getActivity());
            String[] items = new String[entity.size()];
            for (int i = 0, size = items.length; i < size; i++) {
                items[i] = entity.get(i).getName();
            }

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean result = getCallback().onSort(entity.get(which));
                    if (result) {
                        recyclerView.scrollToPosition(0);
                    }
                }
            });
        }

        {
            builder.setPositiveButton(android.R.string.ok, null);
        }

        builder.show();
    }

    final void requestRename(RecordEntity entity) {
        Intent intent = new Intent(getActivity(), PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, RenameRecordFragment.class.getName());

        intent.putExtra(RenameRecordFragment.KEY_ID, entity.getId());
        intent.putExtra(RenameRecordFragment.KEY_PARENT, entity.getParent());
        intent.putExtra(RenameRecordFragment.KEY_HINT, "重命名");
        intent.putExtra(RenameRecordFragment.KEY_NAME, entity.getName());

        this.startActivityForResult(intent, REQUEST_RENAME);
    }

    final void requestDelete(RecordEntity entity) {
        entity = getCallback().onDelete(entity);
        if (entity != null) {
            sortedList.remove(entity);
        }
    }

    final void requestTag(RecordEntity entity) {
        Intent intent = new Intent(getActivity(), PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, ShowTagFragment.class.getName());

        intent.putExtra(ShowTagFragment.KEY_ID, entity.getId());

        this.startActivityForResult(intent, REQUEST_TAG);
    }

    final void requestCompose(@NonNull RecordEntity entity) {
        Intent intent = new Intent(getActivity(), PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, ComposeArticleFragment.class.getName());

        intent.putExtra(ComposeArticleFragment.KEY_ID, entity.getId());

        this.startActivityForResult(intent, REQUEST_COMPOSE);
    }

    final void requestFavorite(@NonNull RecordEntity entity) {
        if (entity.isTrash() || !entity.isDirectory()) {
            return;
        }

        String id = entity.getId();
        FavoriteEntity result = FavoriteFactory.obtain(getActivity()).add(id);

        String text = (result != null)? "已收藏": "不能收藏";
        Snackbar.make(recyclerView, text, Snackbar.LENGTH_SHORT).show();

    }

    final void requestMove(@NonNull RecordEntity entity) {
        if (entity.isTrash()) {
            return;
        }

        Intent intent = new Intent(getActivity(), PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, TargetFolderFragment.class.getName());

        intent.putExtra(TargetFolderFragment.KEY_ID, entity.getId());

        this.startActivityForResult(intent, REQUEST_MOVE);
    }

    final RecordEntity getEntity(String id) {
        for (int i = 0, size = sortedList.size(); i < size; i++) {
            if (sortedList.get(i).getId().equals(id)) {
                return sortedList.get(i);
            }
        }

        return null;
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
    private class FolderMenuItemListener implements Toolbar.OnMenuItemClickListener {

        public FolderMenuItemListener() {

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            swipeActionHelper.clear();

            int id = item.getItemId();
            switch (id) {
                case R.id.menu_create_note: {
                    requestCreateNote();

                    break;
                }
            }

            return false;
        }
    }

    /**
     *
     */
    class RecordItemCallback implements RecordViewHolder.Callback {

        RecordListFragment parent;
        RecordMenuItemListener listener;

        RecordItemCallback(RecordListFragment f) {
            this.parent = f;

            this.listener = new RecordMenuItemListener(this);
        }

        @Override
        public SwipeActionHelper getSwipeActionHelper() {
            return parent.swipeActionHelper;
        }

        @Override
        public boolean onLongClick(RecordViewHolder viewHolder, RecordEntity item) {
            popupMenu(viewHolder, item);

            return true;
        }

        @Override
        public void onRequestCompose(RecordEntity item) {
            parent.requestCompose(item);
        }

        @Override
        public void onRequestFolder(RecordEntity item) {
            ShowRecordActivity.start(parent.getActivity(), item.getId());
        }

        @Override
        public void onRequestDelete(RecordEntity item) {
            parent.requestDelete(item);
        }

        public void onRequestTag(RecordEntity item) {
            parent.requestTag(item);
        }

        public void onRequestFavorite(RecordEntity item) {
            parent.requestFavorite(item);
        }

        public void onRequestMove(RecordEntity item) {
            parent.requestMove(item);
        }

        public void onRequestRename(RecordEntity item) {
            parent.requestRename(item);
        }

        void popupMenu(RecordViewHolder viewHolder, RecordEntity item) {
            int resId = R.menu.menu_record_note_list_item;
            if (item.isTrash()) {
                resId = R.menu.menu_record_trash_list_item;
            } else if (item.isDirectory()) {
                resId = R.menu.menu_record_folder_list_item;
            }

            PopupMenu popup = new PopupMenu(parent.getActivity(), viewHolder.itemView);
            popup.inflate(resId);
            popup.setOnMenuItemClickListener(listener.setItem(item));

            popup.show();
        }
    }

    class RecordMenuItemListener implements PopupMenu.OnMenuItemClickListener {

        RecordEntity entity;

        RecordItemCallback callback;

        RecordMenuItemListener(RecordItemCallback callback) {
            this.callback = callback;
        }

        RecordMenuItemListener setItem(RecordEntity entity) {
            this.entity = entity;

            return this;
        }

        RecordEntity getItem() {
            return this.entity;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            int id = item.getItemId();
            switch (id) {
                case R.id.menu_rename: {
                    callback.onRequestRename(getItem());
                    break;
                }
                case R.id.menu_tag: {
                    callback.onRequestTag(getItem());
                    break;
                }
                case R.id.menu_favorite: {
                    callback.onRequestFavorite(getItem());
                    break;
                }
                case R.id.menu_move: {
                    callback.onRequestMove(getItem());
                    break;
                }
            }

            return false;
        }

    }

    /**
     *
     */
    static class FolderViewHolder extends RecordViewHolder.Folder {

        public FolderViewHolder(RecordItemCallback callback, View itemView) {
            super(callback, itemView);
        }
    }

    /**
     *
     */
    static class NoteViewHolder extends RecordViewHolder.Note {

        public NoteViewHolder(RecordItemCallback callback, View itemView) {
            super(callback, itemView);
        }
    }

    /**
     *
     */
    abstract class Callback {

        abstract RecordEntity onCreate(int type, String name);

        abstract RecordEntity onRename(RecordEntity entity, String name);

        abstract RecordEntity onDelete(RecordEntity entity);

        abstract RecordEntity onMove(RecordEntity entity);

        abstract void onSort();

        protected SortEntity getSort() {
            return null;
        }

        protected void applySort() {
            if (sortEntity == null) {
                sortEntity = SortFactory.create(getActivity(), null);
            }

            {
                String text = String.format("已按%1$s排序", sortEntity.getName());
                sortBtn.setText(text);
            }
        }

        final RecordEntity onMove(RecordEntity entity, String target) {
            if (entity == null || entity.getParent().equals(target)) {
                return null;
            }

            entity.moveTo(target);

            return onMove(entity);
        }

        final boolean onSort(SortEntity entity) {
            if (sortEntity.getId().equals(entity.getId())) {
                sortEntity.toggle();
            } else {
                sortEntity = entity;
            }

            {
                String text = String.format("已按%1$s排序", sortEntity.getName());
                sortBtn.setText(text);
            }

            this.onSort();
            return true;
        }
    }
}

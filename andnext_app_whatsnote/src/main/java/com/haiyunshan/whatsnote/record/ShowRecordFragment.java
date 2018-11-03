package com.haiyunshan.whatsnote.record;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.recyclerview.bridge.*;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.recyclerview.swipe.SwipeHolder;
import club.andnext.recyclerview.swipe.SwipeViewHolder;
import club.andnext.recyclerview.swipe.runner.RightActionRunner;
import club.andnext.utils.PrettyTimeUtils;
import com.haiyunshan.article.Document;
import com.haiyunshan.extract.ExtractProvider;
import com.haiyunshan.record.RecordEntity;
import com.haiyunshan.whatsnote.ShowRecordActivity;
import com.haiyunshan.whatsnote.PackActivity;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.article.ComposeArticleFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowRecordFragment extends Fragment {

    public static final String KEY_PARENT = "parent";

    static final int REQUEST_CREATE_FOLDER  = 1001;
    static final int REQUEST_CREATE_NOTE    = 1002;
    static final int REQUEST_RENAME         = 2001;
    static final int REQUEST_TAG            = 3001;
    static final int REQUEST_COMPOSE        = 4001;

    Toolbar toolbar;

    RecyclerView recyclerView;
    BridgeAdapter adapter;
    MarginDividerDecoration dividerDecoration;
    SwipeActionHelper swipeActionHelper;

    RecordEntity recordEntity;

    public ShowRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_folder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.toolbar = view.findViewById(R.id.toolbar);
            toolbar.inflateMenu(R.menu.menu_record);
            toolbar.setOnMenuItemClickListener(new FolderMenuItemListener(this));
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }

        {
            view.findViewById(R.id.btn_create_folder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeActionHelper.clear();
                    requestCreateFolder();
                }
            });

            view.findViewById(R.id.btn_create_note).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeActionHelper.clear();
                    requestCreateNote();
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            String parent = getArguments().getString(KEY_PARENT, RecordEntity.ROOT_NOTE);
            this.recordEntity = RecordEntity.obtain(parent);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new FileProvider());

            adapter.bind(RecordEntity.class,
                    new BridgeBuilder(RecordViewHolder.class, RecordViewHolder.LAYOUT_RES_ID, this));
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

        if (recordEntity.isTrash() || recordEntity.isExtract()) {
            getView().findViewById(R.id.btn_create_folder).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_create_note).setVisibility(View.GONE);

            toolbar.getMenu().findItem(R.id.menu_create_note).setVisible(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CREATE_FOLDER: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String name = data.getStringExtra(CreateRecordFragment.KEY_NAME);
                    if (!TextUtils.isEmpty(name)) {
                        create(RecordEntity.TYPE_FOLDER, name);
                    }
                }

                break;
            }
            case REQUEST_CREATE_NOTE: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String name = data.getStringExtra(CreateRecordFragment.KEY_NAME);
                    if (!TextUtils.isEmpty(name)) {
                        create(RecordEntity.TYPE_NOTE, name);
                    }
                }

                break;
            }
            case REQUEST_RENAME: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String id = data.getStringExtra(RenameRecordFragment.KEY_ID);
                    String name = data.getStringExtra(RenameRecordFragment.KEY_NAME);
                    if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(name)) {
                        rename(id, name);
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
            entity = recordEntity.add(RecordEntity.TYPE_NOTE, "新的摘抄");
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

    void requestCreateFolder() {

        Intent intent = new Intent(getActivity(), PackActivity.class);
        intent.putExtra(PackActivity.KEY_FRAGMENT, CreateRecordFragment.class.getName());
        intent.putExtra(CreateRecordFragment.KEY_HINT, "新建文件夹");
        intent.putExtra(CreateRecordFragment.KEY_NAME, "新建文件夹");

        this.startActivityForResult(intent, REQUEST_CREATE_FOLDER);
    }

    void create(int type, String name) {
        RecordEntity entity = recordEntity.add(type, name);
        int position = recordEntity.indexOf(entity);
        if (position >= 0) {
            adapter.notifyItemInserted(position);
        }
    }

    void requestCreateNote() {
        Intent intent = new Intent(getActivity(), PackActivity.class);
        intent.putExtra(PackActivity.KEY_FRAGMENT, CreateRecordFragment.class.getName());
        intent.putExtra(CreateRecordFragment.KEY_HINT, "新建笔记");
        intent.putExtra(CreateRecordFragment.KEY_NAME, "空白笔记");

        this.startActivityForResult(intent, REQUEST_CREATE_NOTE);
    }

    void requestRename(RecordEntity entity) {
        Intent intent = new Intent(getActivity(), PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, RenameRecordFragment.class.getName());

        intent.putExtra(RenameRecordFragment.KEY_ID, entity.getId());
        intent.putExtra(RenameRecordFragment.KEY_PARENT, entity.getParent());
        intent.putExtra(RenameRecordFragment.KEY_HINT, "重命名");
        intent.putExtra(RenameRecordFragment.KEY_NAME, entity.getName());

        this.startActivityForResult(intent, REQUEST_RENAME);
    }

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

    void requestDelete(RecordEntity entity) {

        int index = recordEntity.remove(entity);
        if (index >= 0) {
            adapter.notifyItemRemoved(index);
        }
    }

    void requestTag(RecordEntity entity) {
        Intent intent = new Intent(getActivity(), PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, ShowTagFragment.class.getName());

        intent.putExtra(ShowTagFragment.KEY_ID, entity.getId());

        this.startActivityForResult(intent, REQUEST_TAG);
    }

    void requestCompose(RecordEntity entity) {
        Intent intent = new Intent(getActivity(), PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, ComposeArticleFragment.class.getName());

        intent.putExtra(ComposeArticleFragment.KEY_ID, entity.getId());

        this.startActivityForResult(intent, REQUEST_COMPOSE);
    }

    boolean isTrash() {
        boolean result = recordEntity.isTrash();

        return result;
    }

    /**
     *
     */
    private class FileProvider extends BridgeAdapterProvider {

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
    public static class RecordViewHolder extends SwipeViewHolder<RecordEntity> implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {

        static final int LAYOUT_RES_ID = R.layout.layout_note_file_list_item;

        View contentLayout;
        ImageView iconView;
        TextView nameView;
        TextView infoView;

        View renameBtn;
        View deleteBtn;

        ShowRecordFragment parent;

        public RecordViewHolder(ShowRecordFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
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
                this.renameBtn = view.findViewById(R.id.btn_rename);
                renameBtn.setOnClickListener(this);

                this.deleteBtn = view.findViewById(R.id.btn_delete);
                deleteBtn.setOnClickListener(this);

                if (parent.isTrash()) {
                    renameBtn.setVisibility(View.GONE);
                }
            }

            {
                SwipeHolder holder = new SwipeHolder(parent.swipeActionHelper, view, contentLayout);

                {
                    RightActionRunner r = new RightActionRunner(renameBtn, deleteBtn);
                    holder.add(r);
                }

                this.setSwipeHolder(holder);
            }

        }

        @Override
        public void onBind(RecordEntity item, int position) {

            iconView.setImageResource(item.isDirectory()? R.drawable.ic_folder_white_24dp: R.drawable.ic_note_white_24dp);

            nameView.setText(item.getName());

            infoView.setText(PrettyTimeUtils.format(item.getCreated()));
        }

        @Override
        public void onClick(View v) {
            parent.swipeActionHelper.clear();

            if (v == contentLayout) {
                RecordEntity entity = getEntity();
                if (entity.isDirectory()) {
                    ShowRecordActivity.start(parent.getActivity(), getEntity().getId());
                } else {
                    parent.requestCompose(entity);
                }
            } else if (v == renameBtn) {
                parent.requestRename(getEntity());
            } else if (v == deleteBtn) {
                parent.requestDelete(getEntity());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (v == contentLayout) {
                popupMenu();
                return true;
            }

            return false;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.menu_tag: {
                    parent.requestTag(getEntity());
                    break;
                }
            }

            return false;
        }

        void popupMenu() {
            PopupMenu popup = new PopupMenu(parent.getActivity(), itemView);
            popup.inflate(R.menu.menu_folder);
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        RecordEntity getEntity() {
            int position = this.getAdapterPosition();
            RecordEntity en = parent.recordEntity.get(position);

            return en;
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

package com.haiyunshan.whatsnote.record;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import com.google.android.material.snackbar.Snackbar;
import com.haiyunshan.record.FavoriteEntity;
import com.haiyunshan.record.RecordEntity;
import com.haiyunshan.whatsnote.PackActivity;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.article.ComposeArticleFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseRecordFragment extends Fragment implements View.OnClickListener {

    static final int REQUEST_CREATE_FOLDER  = 1001;
    static final int REQUEST_CREATE_NOTE    = 1002;
    static final int REQUEST_MOVE           = 1003;
    static final int REQUEST_RENAME         = 2001;
    static final int REQUEST_TAG            = 3001;
    static final int REQUEST_COMPOSE        = 4001;

    protected Toolbar toolbar;

    protected View createNoteBtn;
    protected View createFolderBtn;

    protected RecyclerView recyclerView;
    protected BridgeAdapter adapter;
    protected MarginDividerDecoration dividerDecoration;
    protected SwipeActionHelper swipeActionHelper;

    public BaseRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_record, container, false);
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.toolbar = view.findViewById(R.id.toolbar);
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }

        {
            this.createFolderBtn = view.findViewById(R.id.btn_create_folder);
            createFolderBtn.setOnClickListener(this);

            this.createNoteBtn = view.findViewById(R.id.btn_create_note);
            createNoteBtn.setOnClickListener(this);
        }
    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        {
            this.dividerDecoration = new MarginDividerDecoration(getActivity());
            dividerDecoration.setMargin(48);
            recyclerView.addItemDecoration(dividerDecoration);
        }

        {
            this.swipeActionHelper = new SwipeActionHelper();
            swipeActionHelper.attach(recyclerView);
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
            case REQUEST_MOVE: {
                if (resultCode == Activity.RESULT_OK && data != null) {

                    String id = data.getStringExtra(TargetFolderFragment.KEY_ID);
                    String target = data.getStringExtra(TargetFolderFragment.KEY_TARGET);
                    if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(target)) {
                        move(id, target);
                    }
                }

                break;
            }

        }
    }

    @Override
    public void onClick(View v) {

    }

    void requestCreateFolder() {

        Intent intent = new Intent(getActivity(), PackActivity.class);
        intent.putExtra(PackActivity.KEY_FRAGMENT, CreateRecordFragment.class.getName());
        intent.putExtra(CreateRecordFragment.KEY_HINT, "新建文件夹");
        intent.putExtra(CreateRecordFragment.KEY_NAME, "新建文件夹");

        this.startActivityForResult(intent, REQUEST_CREATE_FOLDER);
    }

    void create(int type, String name) {

    }

    void requestCreateNote() {

        {
            this.create(RecordEntity.TYPE_NOTE, "空白笔记");
        }

        if (false) {
            Intent intent = new Intent(getActivity(), PackActivity.class);
            intent.putExtra(PackActivity.KEY_FRAGMENT, CreateRecordFragment.class.getName());
            intent.putExtra(CreateRecordFragment.KEY_HINT, "新建笔记");
            intent.putExtra(CreateRecordFragment.KEY_NAME, "空白笔记");

            this.startActivityForResult(intent, REQUEST_CREATE_NOTE);
        }
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

    }

    void requestDelete(RecordEntity entity) {

    }

    void requestTag(RecordEntity entity) {
        Intent intent = new Intent(getActivity(), PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, ShowTagFragment.class.getName());

        intent.putExtra(ShowTagFragment.KEY_ID, entity.getId());

        this.startActivityForResult(intent, REQUEST_TAG);
    }

    void requestCompose(@NonNull RecordEntity entity) {
        Intent intent = new Intent(getActivity(), PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, ComposeArticleFragment.class.getName());

        intent.putExtra(ComposeArticleFragment.KEY_ID, entity.getId());

        this.startActivityForResult(intent, REQUEST_COMPOSE);
    }

    void requestFavorite(@NonNull RecordEntity entity) {
        if (entity.isTrash() || !entity.isDirectory()) {
            return;
        }

        String id = entity.getId();
        FavoriteEntity result = FavoriteEntity.obtain().add(id);

        String text = (result != null)? "已收藏": "不能收藏";
        Snackbar.make(recyclerView, text, Snackbar.LENGTH_SHORT).show();

    }

    void requestMove(@NonNull RecordEntity entity) {
        if (entity.isTrash()) {
            return;
        }

        Intent intent = new Intent(getActivity(), PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, TargetFolderFragment.class.getName());

        intent.putExtra(TargetFolderFragment.KEY_ID, entity.getId());

        this.startActivityForResult(intent, REQUEST_MOVE);
    }

    @CallSuper
    boolean move(String id, String target) {
        RecordEntity entity = this.getEntity(id);
        if (entity == null || entity.getParent().equals(target)) {
            return false;
        }

        entity.moveTo(target);
        return true;
    }

    abstract RecordEntity getEntity(String id);
}

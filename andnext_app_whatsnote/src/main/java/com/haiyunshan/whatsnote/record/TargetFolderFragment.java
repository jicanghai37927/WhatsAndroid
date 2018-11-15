package com.haiyunshan.whatsnote.record;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.recyclerview.adapter.ClazzAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeHolder;
import club.andnext.recyclerview.tree.TreeList;
import club.andnext.recyclerview.tree.TreeListAdapterCallback;
import club.andnext.utils.AlertDialogUtils;
import com.haiyunshan.whatsnote.record.entity.RecordEntity;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.record.entity.RecordFactory;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class TargetFolderFragment extends Fragment implements View.OnClickListener {

    public static final String KEY_ID       = "record.id";
    public static final String KEY_TARGET   = "record.target";

    View cancelBtn;
    View okBtn;

    RecyclerView recyclerView;
    BridgeAdapter adapter;
    TreeList treeList;

    RecordEntity entity;
    RecordEntity checkEntity;
    HashMap<String, RecordEntity> recordMap;

    public TargetFolderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_target_folder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.cancelBtn = view.findViewById(R.id.btn_cancel);
            cancelBtn.setOnClickListener(this);

            this.okBtn = view.findViewById(R.id.btn_ok);
            okBtn.setOnClickListener(this);
        }

        {
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            String id = this.getArguments().getString(KEY_ID, RecordEntity.ROOT_NOTE);
            this.entity = RecordFactory.create(getActivity(), id, RecordEntity.TYPE_EMPTY);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new FolderProvider());

            adapter.bind(RecordEntity.class,
                    new BridgeBuilder(FolderViewHolder.class, FolderViewHolder.LAYOUT_RES_ID, this));
        }

        {
            this.treeList = new TreeList(new TreeListAdapterCallback(adapter));
            treeList.setKeepExpand(true);
            RecordEntity root = getEntity(RecordEntity.ROOT_NOTE);
            root.setName("我的笔记");

            treeList.add(null, root);
        }

        {
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == cancelBtn) {
            getActivity().onBackPressed();
        } else {
            if (checkEntity != null) {

                if (checkEntity.isDescendantOf(entity.getId())) {

                    AlertDialogUtils.showMessage(getActivity(), "不能完成此操作。");

                } else {
                    Intent intent = new Intent();
                    intent.putExtras(getArguments());
                    intent.putExtra(KEY_TARGET, checkEntity.getId());

                    Activity context = getActivity();
                    context.setResult(Activity.RESULT_OK, intent);

                    getActivity().onBackPressed();
                }
            }


        }
    }

    RecordEntity getEntity(String id) {
        if (recordMap == null) {
            recordMap = new HashMap<>();
        }

        RecordEntity entity = recordMap.get(id);
        if (entity != null) {
            return entity;
        }

        entity = RecordFactory.create(getActivity(), id, RecordEntity.TYPE_FOLDER);
        recordMap.put(id, entity);
        return entity;
    }

    void setChecked(RecordEntity entity) {
        if (checkEntity == entity) {
            return;
        }

        int oldPosition = -1;
        if (checkEntity != null) {
            oldPosition = treeList.indexOf(checkEntity);
        }

        int position = -1;
        this.checkEntity = entity;
        if (checkEntity != null) {
            position = treeList.indexOf(checkEntity);
        }

        if (oldPosition >= 0) {
            adapter.notifyItemChanged(oldPosition);
        }

        if (position >= 0) {
            adapter.notifyItemChanged(position);
        }
    }

    /**
     *
     */
    private class FolderProvider implements ClazzAdapterProvider<RecordEntity> {

        @Override
        public RecordEntity get(int position) {
            return (RecordEntity)treeList.get(position);
        }

        @Override
        public int size() {
            return treeList.size();
        }
    }

    /**
     *
     */
    private static class FolderViewHolder extends BridgeHolder<RecordEntity> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = R.layout.layout_folder_tree_list_item;
        static final int PADDING = 48;

        View contentLayout;
        View levelView;
        TextView nameView;
        ImageView chevronView;

        RecordEntity entity;
        TargetFolderFragment parent;

        public FolderViewHolder(TargetFolderFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {

            this.contentLayout = view.findViewById(R.id.content_layout);
            contentLayout.setOnClickListener(this);

            this.levelView = view.findViewById(R.id.tv_level);
            this.nameView = view.findViewById(R.id.tv_name);
            this.chevronView = view.findViewById(R.id.iv_right);
        }

        @Override
        public void onBind(RecordEntity item, int position) {
            this.entity = item;

            {
                contentLayout.setActivated((item == parent.checkEntity));
            }

            {
                int level = parent.treeList.getLevel(item);
                level = (level < 0)? 0: level;
                int padding = level * PADDING;

                int left = padding;
                int top = 0;
                int right = 0;
                int bottom = 0;

                levelView.setPadding(left, top, right, bottom);
            }

            {
                nameView.setText(item.getName());
            }

            {
                chevronView.setRotation(parent.treeList.isExpand(item) ? 90 : 0);
                chevronView.setVisibility((item.size() == 0) ? View.INVISIBLE : View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == contentLayout) {
                click();
            }
        }

        void click() {

            parent.setChecked(entity);

            if (entity.size() == 0) {
                return;
            }

            boolean isExpand = (parent.treeList.isExpand(entity));
            if (!isExpand) {
                for (int i = 0, size = entity.size(); i < size; i++) {
                    RecordEntity child = entity.get(i);

                    parent.treeList.add(entity, parent.getEntity(child.getId()));
                }
            }

            parent.treeList.setExpand(entity, !isExpand);

            chevronView.animate().rotation(parent.treeList.isExpand(entity)? 90: 0);
        }
    }
}

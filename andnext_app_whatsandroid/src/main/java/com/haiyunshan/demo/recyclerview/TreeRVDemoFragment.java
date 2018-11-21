package com.haiyunshan.demo.recyclerview;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.overscroll.OverScrollHelper;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeViewHolder;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.recyclerview.swipe.SwipeHolder;
import club.andnext.recyclerview.swipe.SwipeViewHolder;
import club.andnext.recyclerview.swipe.runner.RightActionRunner;
import club.andnext.recyclerview.tree.TreeList;
import club.andnext.recyclerview.tree.TreeListAdapterCallback;
import club.andnext.utils.GsonUtils;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.dataset.AreaDataset;
import com.haiyunshan.whatsandroid.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TreeRVDemoFragment extends Fragment {

    protected RecyclerView recyclerView;
    BridgeAdapter adapter;

    SwipeActionHelper swipeActionHelper;
    MarginDividerDecoration dividerDecoration;

    protected View actionLayout;
    protected TextView editBtn;

    TreeList treeList;

    public TreeRVDemoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tree_recycler_view_demo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.actionLayout = view.findViewById(R.id.action_layout);
            this.editBtn = view.findViewById(R.id.btn_edit);
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
            this.dividerDecoration = new MarginDividerDecoration(getActivity());
            dividerDecoration.setMargin(36);
            recyclerView.addItemDecoration(dividerDecoration);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new BridgeAdapterProvider() {
                @Override
                public Object get(int position) {
                    return treeList.get(position);
                }

                @Override
                public int size() {
                    return treeList.size();
                }
            });

            adapter.bind(AreaDataset.AreaEntry.class,
                    new BridgeBuilder(TreeDemoViewHolder.class, TreeDemoViewHolder.LAYOUT_RES_ID, this));
            adapter.bind(FolderHeader.class,
                    new BridgeBuilder(FolderViewHolder.class, FolderViewHolder.LAYOUT_RES_ID, this));
        }

        {
            OverScrollHelper helper = OverScrollHelper.attach(recyclerView);

            this.swipeActionHelper = new SwipeActionHelper(helper);
            swipeActionHelper.attach(recyclerView);
        }

        {
            this.treeList = new TreeList(new TreeListAdapterCallback(adapter) {
                @Override
                public void onInserted(int position, int count) {
                    super.onInserted(position, count);

                    recyclerView.smoothScrollToPosition(position);
                }
            });

            treeList.add(null, new FolderHeader());

            {
                AreaDataset dataset = GsonUtils.fromJson(getActivity(), "dataset/area_ds.json", AreaDataset.class);
                this.buildTree(treeList, dataset, null);
            }
        }

        {
            recyclerView.setAdapter(adapter);
        }

    }

    void buildTree(TreeList tree, AreaDataset ds, AreaDataset.AreaEntry parent) {
        List<AreaDataset.AreaEntry> list = ds.getChildren(parent == null? "": parent.getId(), null);

        for (AreaDataset.AreaEntry e : list) {
            tree.add(parent, e);
        }

        for (AreaDataset.AreaEntry e : list) {
            this.buildTree(tree, ds, e);
        }
    }

    private static class FolderViewHolder extends BridgeViewHolder<FolderHeader> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        TreeRVDemoFragment parent;

        public FolderViewHolder(TreeRVDemoFragment parent, View itemView) {
            super(itemView);

            this.parent = parent;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            TextView nameView = ((TextView)view);
            nameView.setText("新建文件夹");
            nameView.setTextColor(ColorStateList.valueOf(Color.MAGENTA));

            view.setOnClickListener(this);
        }

        @Override
        public void onBind(FolderHeader item, int position) {

        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                this.folder();
            }
        }

        void folder() {
            int count = parent.treeList.getChildCount(null);

            String id = UUIDUtils.next();
            String parent = "";
            String name = "新建文件夹 " + (count + 1);

            this.parent.treeList.add(null, new AreaDataset.AreaEntry(id, parent, name));
        }
    }

    /**
     *
     */
    private static class TreeDemoViewHolder extends SwipeViewHolder<AreaDataset.AreaEntry> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = R.layout.layout_area_tree_list_item;

        static final int PADDING = 48;

        View contentLayout;
        TextView nameView;
        ImageView arrowView;

        View folderBtn;
        View deleteBtn;

        TreeRVDemoFragment parent;

        public TreeDemoViewHolder(TreeRVDemoFragment parent, View itemView) {
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
                this.contentLayout = view.findViewById(R.id.content_layout);
                this.nameView = view.findViewById(R.id.tv_name);
                this.arrowView = view.findViewById(R.id.iv_right);

                contentLayout.setOnClickListener(this);
            }

            {
                this.deleteBtn = view.findViewById(R.id.action_delete);
                deleteBtn.setOnClickListener(this);

                this.folderBtn = view.findViewById(R.id.action_folder);
                folderBtn.setOnClickListener(this);
            }

            {
                SwipeHolder holder = new SwipeHolder(parent.swipeActionHelper, view, contentLayout);

                {
                    RightActionRunner r = new RightActionRunner();
                    r.add(folderBtn);
                    r.add(deleteBtn);

                    holder.add(r);
                }

                this.setSwipeHolder(holder);
            }
        }

        @Override
        public void onBind(AreaDataset.AreaEntry item, int position) {
            {
                nameView.setText(item.getName());

                int level = parent.treeList.getLevel(item);
                level = (level < 0)? 0: level;
                int padding = level * PADDING;

                int left = padding;
                int top = nameView.getPaddingTop();
                int right = nameView.getPaddingRight();
                int bottom = nameView.getPaddingBottom();
                nameView.setPadding(left, top, right, bottom);
            }

            {
                boolean isLeaf = parent.treeList.isLeaf(item);
                arrowView.setVisibility(isLeaf ? View.INVISIBLE : View.VISIBLE);

                if (arrowView.getVisibility() == View.VISIBLE) {
                    float degree = (parent.treeList.isExpand(item)) ? 90 : 0;
                    arrowView.setRotation(degree);
                }
            }
        }

        @Override
        public void onActionBegin(SwipeActionHelper helper, int action) {

        }

        @Override
        public void onActionEnd(SwipeActionHelper helper, int action) {

        }

        @Override
        public void onClick(View v) {
            if (v == contentLayout) {
                this.click();
            } else if (v == deleteBtn) {
                this.delete();
            } else if (v == folderBtn) {
                this.folder();
            }
        }

        void click() {
            AreaDataset.AreaEntry item = this.getEntity();
            if (parent.treeList.isLeaf(item)) {

            } else {
                boolean expand = parent.treeList.isExpand(item);
                expand = parent.treeList.setExpand(item, !expand);
                if (expand) {
                    arrowView.animate().rotation(90);
                } else {
                    arrowView.animate().rotation(0);
                }
            }
        }

        void delete() {
            AreaDataset.AreaEntry entity = getEntity();
            parent.treeList.remove(entity);
        }

        void folder() {
            AreaDataset.AreaEntry entity = getEntity();

            String name = entity.getName() + " " + (parent.treeList.getChildCount(entity) + 1);
            String parent = entity.getId();
            String id = UUIDUtils.next();

            AreaDataset.AreaEntry child = new AreaDataset.AreaEntry(id, parent, name);
            this.parent.treeList.add(entity, child);

            this.parent.treeList.setExpand(entity, true);
        }

        AreaDataset.AreaEntry getEntity() {
            int position = this.getAdapterPosition();
            if (position < 0) {
                return null;
            }

            Object obj = parent.adapter.get(position);
            AreaDataset.AreaEntry item = (AreaDataset.AreaEntry)(obj);

            return item;
        }

        @Override
        public float getTranslation(MarginDividerDecoration decoration) {
            AreaDataset.AreaEntry item = this.getEntity();

            int level = parent.treeList.getLevel(item);
            level = (level < 0)? 0: level;
            int padding = level * PADDING;

            padding += super.getTranslation(decoration);
            return padding;
        }
    }

    /**
     *
     */
    private static class FolderHeader {

    }
}

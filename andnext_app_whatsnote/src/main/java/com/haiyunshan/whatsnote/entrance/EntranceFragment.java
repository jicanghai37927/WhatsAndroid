package com.haiyunshan.whatsnote.entrance;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeHolder;
import club.andnext.recyclerview.section.SectionList;
import club.andnext.recyclerview.section.SectionListAdapterCallback;
import com.haiyunshan.whatsnote.entrance.entity.EntranceEntity;
import com.haiyunshan.whatsnote.entrance.entity.EntranceUtils;
import com.haiyunshan.whatsnote.record.entity.*;
import com.haiyunshan.whatsnote.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EntranceFragment extends Fragment {

    RecyclerView recyclerView;
    BridgeAdapter adapter;
    SectionList sectionList;

    EntranceSection entranceSection;
    FavoriteSection favoriteSection;
    TagSection tagSection;

    public EntranceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
            this.entranceSection = new EntranceSection(this, "位置", OptionEntity.SECTION_ENTRANCE);
            this.getLifecycle().addObserver(entranceSection);

            this.favoriteSection = new FavoriteSection(this, "个人收藏", OptionEntity.SECTION_FAVORITE);
            this.getLifecycle().addObserver(favoriteSection);

            this.tagSection = new TagSection(this, "标签", OptionEntity.SECTION_TAG);
            this.getLifecycle().addObserver(tagSection);
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new SectionListProvider());

            {
                adapter.bind(EntranceSection.class,
                        new BridgeBuilder(SectionViewHolder.class, SectionViewHolder.LAYOUT_RES_ID, this));
                adapter.bind(EntranceEntity.class,
                        new BridgeBuilder(EntranceViewHolder.class, EntranceViewHolder.LAYOUT_RES_ID, this));
            }

            {
                adapter.bind(FavoriteSection.class,
                        new BridgeBuilder(SectionViewHolder.class, SectionViewHolder.LAYOUT_RES_ID, this));
                adapter.bind(FavoriteEntity.class,
                        new BridgeBuilder(FavoriteViewHolder.class, FavoriteViewHolder.LAYOUT_RES_ID, this));
            }

            {
                adapter.bind(TagSection.class,
                        new BridgeBuilder(SectionViewHolder.class, SectionViewHolder.LAYOUT_RES_ID, this));
                adapter.bind(TagEntity.class,
                        new BridgeBuilder(TagViewHolder.class, TagViewHolder.LAYOUT_RES_ID, this));
            }

        }

        {
            this.sectionList = new SectionList(new SectionListAdapterCallback(adapter));
            {
                String key = entranceSection.getKey();
                boolean expand = OptionEntity.obtain().isSectionExpand(key);
                sectionList.add(entranceSection, expand, entranceSection);
            }
            {
                String key = favoriteSection.getKey();
                boolean expand = OptionEntity.obtain().isSectionExpand(key);
                sectionList.add(favoriteSection, expand, favoriteSection);
            }
            {
                String key = tagSection.getKey();
                boolean expand = OptionEntity.obtain().isSectionExpand(key);
                sectionList.add(tagSection, expand, tagSection);
            }
        }

        {
            recyclerView.setAdapter(adapter);
        }

    }

    /**
     *
     */
    private class SectionListProvider implements BridgeAdapterProvider {

        @Override
        public Object get(int position) {
            return sectionList.get(position);
        }

        @Override
        public int size() {
            return sectionList.size();
        }
    }

    /**
     *
     */
    private static class SectionViewHolder extends BridgeHolder<BaseSection> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = R.layout.layout_section_list_item;

        TextView nameView;
        ImageView chevronView;

        BaseSection entity;
        EntranceFragment parent;

        public SectionViewHolder(EntranceFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            this.nameView = view.findViewById(R.id.tv_name);
            this.chevronView = view.findViewById(R.id.iv_chevron);

            view.setOnClickListener(this);
        }

        @Override
        public void onBind(BaseSection item, int position) {
            this.entity = item;

            nameView.setText(item.getName());

            if (parent.sectionList.isExpand(item)) {
                chevronView.setRotation(90);
            } else {
                chevronView.setRotation(0);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                boolean expand = parent.sectionList.isExpand(entity);
                parent.sectionList.setExpand(entity, !expand);
                expand = parent.sectionList.isExpand(entity);
                if (expand) {
                    chevronView.animate().rotation(90);
                } else {
                    chevronView.animate().rotation(0);
                }

                {
                    OptionEntity.obtain().setSectionExpand(entity.getKey(), expand);
                }
            }
        }
    }

    /**
     *
     */
    private static class EntranceViewHolder extends BridgeHolder<EntranceEntity> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        EntranceEntity entity;

        EntranceFragment parent;

        public EntranceViewHolder(EntranceFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this);
        }

        @Override
        public void onBind(EntranceEntity item, int position) {
            this.entity = item;

            ((TextView)itemView).setText(item.getName());
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                EntranceUtils.enter(parent, entity);
            }
        }
    }

    /**
     *
     */
    private static class FavoriteViewHolder extends BridgeHolder<FavoriteEntity> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        FavoriteEntity entity;

        EntranceFragment parent;

        public FavoriteViewHolder(EntranceFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this);
        }

        @Override
        public void onBind(FavoriteEntity item, int position) {
            this.entity = item;

            ((TextView)itemView).setText(item.getName());
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                EntranceUtils.enter(parent, entity);
            }
        }
    }

    /**
     *
     */
    private static class TagViewHolder extends BridgeHolder<TagEntity> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        TagEntity entity;

        EntranceFragment parent;

        public TagViewHolder(EntranceFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {
            view.setOnClickListener(this);
        }

        @Override
        public void onBind(TagEntity item, int position) {
            this.entity = item;

            ((TextView)itemView).setText(item.getName());
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                EntranceUtils.enter(parent, entity);
            }
        }
    }

    /**
     *
     */
    private static class EntranceSection extends BaseSection implements BridgeAdapterProvider<EntranceEntity> {

        ArrayList<EntranceEntity> data;
        ArrayList<EntranceEntity> oldData;

        EntranceEntity entranceEntity;

        EntranceSection(EntranceFragment f, String name, String key) {
            super(f, name, key);

            this.entranceEntity = EntranceEntity.obtain(f.getActivity());

            this.data = new ArrayList<>(entranceEntity.size());
            for (int i = 0, size = entranceEntity.size(); i < size; i++) {
                EntranceEntity en = entranceEntity.get(i);
                if (en.isVisible()) {
                    data.add(en);
                }
            }
        }

        @Override
        public EntranceEntity get(int position) {
            return data.get(position);
        }

        @Override
        public int size() {
            return data.size();
        }

    }

    /**
     *
     */
    private static class FavoriteSection extends BaseSection implements BridgeAdapterProvider<FavoriteEntity> {

        FavoriteEntity data;
        FavoriteEntity oldData;

        FavoriteSection(EntranceFragment f, String name, String key) {
            super(f, name, key);

            this.data = FavoriteEntity.obtain();
            this.oldData = null;
        }

        @Override
        public FavoriteEntity get(int position) {
            return data.get(position);
        }

        @Override
        public int size() {
            return data.size();
        }

        @Override
        void onStart() {
            if (parent.sectionList.isExpand(this)) {
                if (data != null && oldData != null) {
                    DiffUtil.calculateDiff(new DiffCallback(data, oldData))
                            .dispatchUpdatesTo(new DiffListUpdateCallback(parent.sectionList, this));
                }

                oldData = null;
            }
        }

        @Override
        void onStop() {
            if (parent.sectionList.isExpand(this)) {
                oldData = FavoriteEntity.copy();
            } else {
                oldData = null;
            }

        }

    }

    /**
     *
     */
    private static class TagSection extends BaseSection implements BridgeAdapterProvider<TagEntity> {

        TagEntity data;
        TagEntity oldData;

        TagSection(EntranceFragment f, String name, String key) {
            super(f, name, key);

            this.data = TagEntity.obtain();
            this.oldData = null;
        }

        @Override
        public TagEntity get(int position) {
            return data.get(position);
        }

        @Override
        public int size() {
            return data.size();
        }

        @Override
        void onStart() {
            if (parent.sectionList.isExpand(this)) {
                if (data != null && oldData != null) {
                    DiffUtil.calculateDiff(new DiffCallback(data, oldData))
                            .dispatchUpdatesTo(new DiffListUpdateCallback(parent.sectionList, this));
                }

                oldData = null;
            }
        }

        @Override
        void onStop() {
            if (parent.sectionList.isExpand(this)) {
                oldData = TagEntity.copy();
            } else {
                oldData = null;
            }
        }
    }

    /**
     *
     */
    private static class BaseSection implements LifecycleObserver {

        String name;
        String key;

        EntranceFragment parent;

        BaseSection(EntranceFragment f, String name, String key) {
            this.parent = f;
            this.name = name;
            this.key = key;
        }

        String getName() {
            return name;
        }

        String getKey() { return key; }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        void onStart() {

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        void onStop() {

        }
    }

    /**
     *
     */
    private static class DiffListUpdateCallback implements ListUpdateCallback {

        SectionList sectionList;
        BaseSection section;

        DiffListUpdateCallback(SectionList sectionList, BaseSection section) {
            this.sectionList = sectionList;
            this.section = section;
        }

        @Override
        public void onInserted(int position, int count) {
            sectionList.notifyInserted(section, position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            sectionList.notifyRemoved(section, position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            sectionList.notifyMoved(section, fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count, @Nullable Object payload) {
            sectionList.notifyChanged(section, position, count, payload);
        }
    }

    /**
     *
     */
    private static class DiffCallback extends DiffUtil.Callback {

        BaseEntitySet data;
        BaseEntitySet oldData;

        DiffCallback(BaseEntitySet data, BaseEntitySet oldData) {
            this.data = data;
            this.oldData = oldData;
        }

        @Override
        public int getOldListSize() {
            return oldData.size();
        }

        @Override
        public int getNewListSize() {
            return data.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            BaseEntity oldItem = oldData.get(oldItemPosition);
            BaseEntity newItem = data.get(newItemPosition);

            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            BaseEntity oldItem = oldData.get(oldItemPosition);
            BaseEntity newItem = data.get(newItemPosition);

            return oldItem.equals(newItem);
        }
    }
}

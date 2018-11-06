package com.haiyunshan.whatsnote.record;


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
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeHolder;
import club.andnext.recyclerview.section.SectionList;
import club.andnext.recyclerview.section.SectionListAdapterCallback;
import com.haiyunshan.entrance.EntranceEntity;
import com.haiyunshan.entrance.EntranceUtils;
import com.haiyunshan.record.FavoriteEntity;
import com.haiyunshan.record.TagEntity;
import com.haiyunshan.whatsnote.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordMainFragment extends Fragment {

    RecyclerView recyclerView;
    BridgeAdapter adapter;
    SectionList sectionList;

    EntranceSection entranceSection;
    FavoriteSection favoriteSection;
    TagSection tagSection;

    public RecordMainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_main, container, false);
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
            this.entranceSection = new EntranceSection(this, "位置");
            this.favoriteSection = new FavoriteSection(this, "个人收藏");
            this.tagSection = new TagSection(this, "标签");


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
            sectionList.add(entranceSection, true, entranceSection);
            sectionList.add(favoriteSection, true, favoriteSection);
            sectionList.add(tagSection, true, tagSection);
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
        RecordMainFragment parent;

        public SectionViewHolder(RecordMainFragment f, View itemView) {
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
            }
        }
    }

    /**
     *
     */
    private static class EntranceViewHolder extends BridgeHolder<EntranceEntity> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        EntranceEntity entity;

        RecordMainFragment parent;

        public EntranceViewHolder(RecordMainFragment f, View itemView) {
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
    private static class FavoriteViewHolder extends BridgeHolder<FavoriteEntity> {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        RecordMainFragment parent;

        public FavoriteViewHolder(RecordMainFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {

        }

        @Override
        public void onBind(FavoriteEntity item, int position) {
            ((TextView)itemView).setText(item.getName());
        }
    }

    /**
     *
     */
    private static class TagViewHolder extends BridgeHolder<TagEntity> {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        RecordMainFragment parent;

        public TagViewHolder(RecordMainFragment f, View itemView) {
            super(itemView);

            this.parent = f;
        }

        @Override
        public int getLayoutResourceId() {
            return LAYOUT_RES_ID;
        }

        @Override
        public void onViewCreated(@NonNull View view) {

        }

        @Override
        public void onBind(TagEntity item, int position) {
            ((TextView)itemView).setText(item.getName());
        }
    }

    /**
     *
     */
    private static class EntranceSection extends BaseSection implements BridgeAdapterProvider<EntranceEntity> {

        ArrayList<EntranceEntity> data;
        ArrayList<EntranceEntity> oldData;

        EntranceEntity entranceEntity;

        EntranceSection(RecordMainFragment f, String name) {
            super(f, name);

            this.entranceEntity = EntranceEntity.obtain();

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

        FavoriteSection(RecordMainFragment f, String name) {
            super(f, name);

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

    }

    /**
     *
     */
    private static class TagSection extends BaseSection implements BridgeAdapterProvider<TagEntity> {

        TagEntity data;
        TagEntity oldData;

        TagSection(RecordMainFragment f, String name) {
            super(f, name);

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
    }

    /**
     *
     */
    private static class BaseSection {

        String name;

        RecordMainFragment parent;

        BaseSection(RecordMainFragment f, String name) {
            this.parent = f;
            this.name = name;
        }

        String getName() {
            return name;
        }
    }
}

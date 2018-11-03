package com.haiyunshan.whatsnote.record;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;
import club.andnext.recyclerview.bridge.*;
import club.andnext.utils.ColorUtils;
import com.haiyunshan.record.RecordEntity;
import com.haiyunshan.record.TagEntity;
import com.haiyunshan.whatsnote.PackActivity;
import com.haiyunshan.whatsnote.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTagFragment extends Fragment {

    public static final String KEY_ID = "record.id";

    static final int REQUEST_CREATE_TAG = 1001;

    RecyclerView recyclerView;
    BridgeAdapter adapter;
    SortedList<TagEntity> sortedList;

    TagEntity tagEntity;
    RecordEntity recordEntity;

    public ShowTagFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_tag, container, false);
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
            String id = getArguments().getString(KEY_ID);

            this.recordEntity = RecordEntity.obtain(id, RecordEntity.TYPE_EMPTY);
            this.tagEntity = TagEntity.obtain();
        }

        {
            this.adapter = new BridgeAdapter(getActivity(), new TagProvider());

            adapter.bind(TagEntity.class,
                    new BridgeBuilder(CreateViewHolder.class, CreateViewHolder.LAYOUT_RES_ID, this),
                    new BridgeBuilder(TagViewHolder.class, TagViewHolder.LAYOUT_RES_ID, this));
            adapter.bind(TagEntity.class, new BridgeFilter() {
                @Override
                public Class<? extends BridgeHolder> getHolder(Object obj) {
                    if (obj == tagEntity) {
                        return CreateViewHolder.class;
                    }

                    return TagViewHolder.class;
                }
            });

        }

        {
            this.sortedList = new SortedList<>(TagEntity.class, new SortedCallback(adapter));
            sortedList.add(tagEntity);
            if (tagEntity.getList() != null) {
                sortedList.addAll(tagEntity.getList());
            }
        }

        {
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CREATE_TAG: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String name = data.getStringExtra(CreateTagFragment.KEY_NAME);
                    String color = data.getStringExtra(CreateTagFragment.KEY_COLOR);
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(color)) {
                        createTag(name, color);
                    }

                }
            }
        }
    }

    void requestCreate() {
        Intent intent = new Intent(getActivity(), PackActivity.class);

        intent.putExtra(PackActivity.KEY_FRAGMENT, CreateTagFragment.class.getName());

        this.startActivityForResult(intent, REQUEST_CREATE_TAG);
    }

    void createTag(String name, String color) {
        int c = ColorUtils.parse(color);

        TagEntity entity = tagEntity.add(name, c);
        if (entity != null) {
            sortedList.add(entity);
        }
    }

    /**
     *
     */
    private class TagProvider extends BridgeAdapterProvider {

        @Override
        public Object get(int position) {
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
    private class SortedCallback extends SortedListAdapterCallback<TagEntity> {

        public SortedCallback(RecyclerView.Adapter adapter) {
            super(adapter);
        }

        @Override
        public int compare(TagEntity o1, TagEntity o2) {
            if (o1 == tagEntity) {
                return -1;
            }
            if (o2 == tagEntity) {
                return 1;
            }

            int a = o1.getOrder();
            int b = o2.getOrder();

            return (a - b);
        }

        @Override
        public boolean areContentsTheSame(TagEntity oldItem, TagEntity newItem) {
            boolean a = oldItem.getName().equals(newItem.getName());
            boolean b = oldItem.getColor() == newItem.getColor();

            return (a && b);
        }

        @Override
        public boolean areItemsTheSame(TagEntity item1, TagEntity item2) {
            return item1.getId().equals(item2.getId());
        }
    }

    /**
     *
     */
    private static class CreateViewHolder extends BridgeHolder<TagEntity> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        ShowTagFragment parent;

        public CreateViewHolder(ShowTagFragment f, View itemView) {
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
            ((TextView)itemView).setText("新建标签");
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                parent.requestCreate();
            }
        }
    }

    /**
     *
     */
    private static class TagViewHolder extends BridgeHolder<TagEntity> {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        public TagViewHolder(ShowTagFragment f, View itemView) {
            super(itemView);
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
            ((TextView)itemView).setTextColor(item.getColor());
        }
    }

}

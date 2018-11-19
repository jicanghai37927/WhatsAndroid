package com.haiyunshan.whatsnote.record;


import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.record.entity.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentRecordFragment extends RecordListFragment {

    public static final String KEY_TAG = "recent.tag"; 
    
    RecentRecordSet recentRecordSet;

    RadioButton nameSortBtn;
    RadioButton modifiedSortBtn;

    public RecentRecordFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            FrameLayout sortLayout = view.findViewById(R.id.sort_layout);
            sortLayout.findViewById(R.id.tv_sort).setVisibility(View.GONE);

            int resource = R.layout.layout_recent_sort;
            View v = getLayoutInflater().inflate(resource, sortLayout, false);

            sortLayout.addView(v);

            nameSortBtn = v.findViewById(R.id.rb_name);
            nameSortBtn.setOnClickListener(this);

            modifiedSortBtn = v.findViewById(R.id.rb_modified);
            modifiedSortBtn.setOnClickListener(this);

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            SortEntity sort = this.getSort();
            if (sort.getId().equals(SortEntity.ID_NAME)) {
                nameSortBtn.setChecked(true);
            }
            if (sort.getId().equals(SortEntity.ID_MODIFIED)) {
                modifiedSortBtn.setChecked(true);
            }
        }

        {
            getView().findViewById(R.id.btn_create_folder).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_create_note).setVisibility(View.GONE);

            toolbar.getMenu().findItem(R.id.menu_create_note).setVisible(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        boolean isFirstStart = (recentRecordSet == null);

        {
            String tag = getArguments().getString(KEY_TAG, "");
            this.recentRecordSet = RecentRecordSet.create(tag);
        }

        {
            this.replaceAll(recentRecordSet.getCollection());
        }

        if (!isFirstStart) {
            SortEntity sort = this.getSort();
            if (sort.getId().equals(SortEntity.ID_MODIFIED)) {
                int position = 0;
                if (!sort.isReverse()) {
                    position = recyclerView.getAdapter().getItemCount() - 1;
                    position = (position < 0)? 0: position;
                }

                recyclerView.scrollToPosition(position);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        {
            recentRecordSet.save();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v == nameSortBtn) {
            SortEntity sort = this.getSort();
            if (!sort.getId().equals(SortEntity.ID_NAME)) {
                sort = SortEntity.create(SortEntity.ID_NAME);
                this.setSort(sort);
            } else {
                sort.toggle();
            }

            {
                this.replaceAll(recentRecordSet.getCollection());
                recyclerView.scrollToPosition(0);
            }

            {
                OptionEntity.obtain().setRecentSort(sort);
            }

        } else if (v == modifiedSortBtn) {
            SortEntity sort = this.getSort();
            if (!sort.getId().equals(SortEntity.ID_MODIFIED)) {
                sort = SortEntity.create(SortEntity.ID_MODIFIED);
                sort.setReverse(true);

                this.setSort(sort);

            } else {
                sort.toggle();
            }

            {
                this.replaceAll(recentRecordSet.getCollection());
                recyclerView.scrollToPosition(0);
            }

            {
                OptionEntity.obtain().setRecentSort(sort);
            }

        }
    }

    @Override
    protected Callback createCallback() {
        RecordListCallback callback = new RecordListCallback();
        callback.setTimeType(TIME_MODIFIED);

        return callback;
    }

    /**
     *
     */
    private class RecordListCallback extends RecordListFragment.Callback {

        @Override
        protected SortEntity getSort() {

            // change default sort for recent records
            return OptionEntity.obtain().getRecentSort();
        }

        @Override
        protected void applySort() {
            // we do not need apply sort when onStart(), so let it empty
        }

        @Override
        public RecordEntity onCreate(int type, String name) {
            return null;
        }

        @Override
        public RecordEntity onRename(RecordEntity entity, String name) {

            boolean equals = entity.getName().equals(name);
            if (equals) {
                return null;
            }

            entity.setName(name);

            return entity;
        }

        @Override
        RecordEntity onDelete(RecordEntity entity) {

            int index = recentRecordSet.remove(entity);
            if (index < 0) {
                return null;
            }

            return entity;
        }

        @Override
        public RecordEntity onMove(RecordEntity entity) {
            return null;
        }

        @Override
        public void onSort() {
            replaceAll(recentRecordSet.getCollection());
        }
    }


}

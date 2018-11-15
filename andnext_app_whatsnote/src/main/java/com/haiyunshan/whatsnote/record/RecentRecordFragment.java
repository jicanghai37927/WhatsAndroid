package com.haiyunshan.whatsnote.record;


import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.record.entity.RecentFactory;
import com.haiyunshan.whatsnote.record.entity.RecentRecordSet;
import com.haiyunshan.whatsnote.record.entity.RecordEntity;
import com.haiyunshan.whatsnote.record.entity.SortEntity;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentRecordFragment extends RecordListFragment {

    public static final String KEY_TAG = "recent.tag"; 
    
    RecentRecordSet recentRecordSet;

    public RecentRecordFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            String tag = getArguments().getString(KEY_TAG, "");
            this.recentRecordSet = RecentFactory.createRecordSet(getActivity(), tag);

            addAll(recentRecordSet.getCollection());
        }

        {
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
    }

    @Override
    protected Callback createCallback() {
        return new RecordListCallback();
    }

    /**
     *
     */
    private class RecordListCallback extends RecordListFragment.Callback {

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

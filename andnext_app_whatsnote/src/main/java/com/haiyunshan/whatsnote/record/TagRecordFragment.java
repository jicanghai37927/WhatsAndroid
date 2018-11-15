package com.haiyunshan.whatsnote.record;


import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.record.entity.RecordEntity;
import com.haiyunshan.whatsnote.record.entity.SortEntity;
import com.haiyunshan.whatsnote.record.entity.TagFactory;
import com.haiyunshan.whatsnote.record.entity.TagRecordSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class TagRecordFragment extends RecordListFragment {

    public static final String KEY_TAG = "recent.tag";

    TagRecordSet tagRecordSet;

    public TagRecordFragment() {

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
            this.tagRecordSet = TagFactory.createRecordSet(getActivity(), tag);

            this.addAll(tagRecordSet.getCollection());
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

            int index = tagRecordSet.remove(entity);
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
            replaceAll(tagRecordSet.getCollection());
        }
    }


}

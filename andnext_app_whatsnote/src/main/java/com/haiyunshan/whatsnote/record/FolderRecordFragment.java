package com.haiyunshan.whatsnote.record;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.extract.entity.ExtractFactory;
import com.haiyunshan.whatsnote.record.entity.RecordEntity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderRecordFragment extends RecordListFragment {

    public static final String KEY_FOLDER = "record.folder";

    RecordEntity folderEntity;      // record data

    public FolderRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        {
            String parent = getArguments().getString(KEY_FOLDER, RecordEntity.ROOT_NOTE);
            this.folderEntity = RecordEntity.create(parent);

            if (folderEntity.isExtract()) {
                ExtractFactory.check(getActivity(), folderEntity);
            }
        }

        {
            this.replaceAll(folderEntity.getCollection());
        }

        if (folderEntity.isTrash() || folderEntity.isExtract()) {
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

        {
            folderEntity.save();
        }
    }

    @Override
    RecordListFragment.Callback createCallback() {
        return new RecordListCallback();
    }

    /**
     *
     */
    private class RecordListCallback extends RecordListFragment.Callback {

        @Override
        public RecordEntity onCreate(int type, String name) {
            RecordEntity entity = folderEntity.add(type, name);
            int position = folderEntity.indexOf(entity);
            if (position >= 0 && entity.isDirectory()) {
                return entity;
            }

            if (type == RecordEntity.TYPE_NOTE) {
                requestCompose(entity);
            }

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

            int index = folderEntity.remove(entity.getId(), true);
            if (index < 0) {
                return null;
            }

            return entity;
        }

        @Override
        public RecordEntity onMove(RecordEntity entity) {

            int index = folderEntity.remove(entity.getId(), false);
            if (index < 0) {
                return null;
            }

            return entity;
        }

        @Override
        public void onSort() {
            replaceAll(folderEntity.getCollection());
        }
    }

}

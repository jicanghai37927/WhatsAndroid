package com.haiyunshan.whatsnote.record;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import club.andnext.recyclerview.helper.SoftInputTouchHelper;
import club.andnext.utils.SoftInputUtils;
import com.haiyunshan.whatsnote.R;
import com.haiyunshan.whatsnote.record.entity.RecordEntity;
import com.haiyunshan.whatsnote.record.entity.SearchRecordSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRecordFragment extends RecordListFragment {

    EditText editKeyword;
    View cancelBtn;

    SearchRecordSet recordSet;      // record data

    public SearchRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            FrameLayout layout = view.findViewById(R.id.toolbar_layout);

            int resource = R.layout.layout_search_toolbar;
            View v = getLayoutInflater().inflate(resource, layout, false);
            layout.addView(v);

            this.editKeyword = v.findViewById(R.id.edit_keyword);
            editKeyword.addTextChangedListener(new SearchTextWatcher());
            editKeyword.setOnClickListener(this);

            this.cancelBtn = v.findViewById(R.id.btn_cancel);
            cancelBtn.setOnClickListener(this);

        }

        {
            toolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            getView().findViewById(R.id.btn_create_folder).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_create_note).setVisibility(View.GONE);

            toolbar.getMenu().findItem(R.id.menu_create_note).setVisible(false);
        }

        {
            SoftInputTouchHelper helper = new SoftInputTouchHelper();
            helper.setFlags(SoftInputTouchHelper.FLAG_SCROLL);
            helper.attach(recyclerView);
        }

        {
            editKeyword.post(new Runnable() {
                @Override
                public void run() {
                    SoftInputUtils.show(getActivity(), editKeyword);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        {
            this.recordSet = null;
        }

        {
            String keyword = editKeyword.getText().toString().trim();
            this.search(keyword);
        }

        {
            this.replaceAll(recordSet.getCollection());
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
    public void onClick(View v) {
        super.onClick(v);

        if (v == cancelBtn) {
            getActivity().onBackPressed();
        } else if (v == editKeyword) {
            appBarLayout.setExpanded(false, true);
        }
    }

    @Override
    RecordListFragment.Callback createCallback() {
        return new SearchRecordFragment.RecordListCallback();
    }

    void search(String keyword) {
        if (recordSet == null) {

            recordSet = SearchRecordSet.create(keyword);
            replaceAll(recordSet.getCollection());

        } else {
            if (!recordSet.getKeyword().equals(keyword)) {
                recordSet = SearchRecordSet.create(keyword);
                replaceAll(recordSet.getCollection());
            }
        }
    }

    /**
     *
     */
    private class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            search(s.toString().trim());
        }
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
            if (name.indexOf(recordSet.getKeyword()) < 0) {
                int index = recordSet.remove(entity);
                if (index >= 0) {
                    remove(entity);
                }
            }

            return null;
        }

        @Override
        RecordEntity onDelete(RecordEntity entity) {
            return null;
        }

        @Override
        public RecordEntity onMove(RecordEntity entity) {
            return null;
        }

        @Override
        public void onSort() {
            replaceAll(recordSet.getCollection());
        }
    }

}

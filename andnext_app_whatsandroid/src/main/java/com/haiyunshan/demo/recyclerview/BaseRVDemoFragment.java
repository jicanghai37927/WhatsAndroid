package com.haiyunshan.demo.recyclerview;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.haiyunshan.whatsandroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseRVDemoFragment extends Fragment {

    protected RecyclerView recyclerView;

    protected View actionLayout;
    protected TextView editBtn;

    public BaseRVDemoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_recycler_view_demo, container, false);
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
}

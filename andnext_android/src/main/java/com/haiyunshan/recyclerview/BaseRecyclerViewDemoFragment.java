package com.haiyunshan.recyclerview;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.haiyunshan.whatsandroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseRecyclerViewDemoFragment extends Fragment {

    RecyclerView recyclerView;

    View actionLayout;
    TextView editBtn;

    public BaseRecyclerViewDemoFragment() {

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
            this.recyclerView = view.findViewById(R.id.recycler_list_view);
            this.actionLayout = view.findViewById(R.id.action_layout);
            this.editBtn = view.findViewById(R.id.btn_edit);
        }
    }
}

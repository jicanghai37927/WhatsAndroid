package com.haiyunshan.demo.recyclerview;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import club.andnext.overscroll.OverScrollHelper;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;
import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.utils.GsonUtils;
import com.haiyunshan.dataset.PiliDataset;

public abstract class BaseSwipeRVDemoFragment extends BaseRVDemoFragment {

    protected PiliDataset dataset;
    protected BridgeAdapter adapter;

    protected SwipeActionHelper swipeActionhelper;
    protected OverScrollHelper overScrollHelper;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            this.dataset = GsonUtils.fromJson(getActivity(), "dataset/pili_ds.json", PiliDataset.class);
        }

        {
            MarginDividerDecoration decor = new MarginDividerDecoration(getActivity());
            decor.setMargin(42);
            recyclerView.addItemDecoration(decor);
        }

        {
            this.overScrollHelper = OverScrollHelper.attach(recyclerView);
        }

        {
            this.swipeActionhelper = new SwipeActionHelper(overScrollHelper);
            swipeActionhelper.attach(recyclerView);
        }

        {
            this.adapter = this.createAdapter();
            recyclerView.setAdapter(adapter);
        }
    }

    protected abstract BridgeAdapter createAdapter();

}

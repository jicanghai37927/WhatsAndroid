package com.haiyunshan.test.widget;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.overscroll.OverScrollHelper;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeHolder;
import com.haiyunshan.whatsandroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestNestedScrollView1Fragment extends Fragment {

    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;

    public TestNestedScrollView1Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_nested_scroll_view1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.nestedScrollView = view.findViewById(R.id.sv_nested);
            OverScrollHelper.attach(nestedScrollView);
        }

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
            BridgeAdapter adapter = new BridgeAdapter(getActivity(), new BridgeAdapterProvider() {
                @Override
                public Object get(int position) {
                    return String.valueOf(position + 1);
                }

                @Override
                public int size() {
                    return 100;
                }
            });

            adapter.bind(String.class,
                    new BridgeBuilder(DemoViewHolder.class, DemoViewHolder.LAYOUT_RES_ID, this));

            recyclerView.setAdapter(adapter);
        }

    }

    private static class DemoViewHolder extends BridgeHolder<String> {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        public DemoViewHolder(TestNestedScrollView1Fragment parent, View itemView) {
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
        public void onBind(String item, int position) {
            ((TextView)itemView).setText(item);

            Log.w("AA", "onBind = " + item);
        }
    }
}

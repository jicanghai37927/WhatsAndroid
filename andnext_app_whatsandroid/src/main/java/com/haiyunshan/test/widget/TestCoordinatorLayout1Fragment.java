package com.haiyunshan.test.widget;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.andnext.overscroll.OverScrollHelper;
import club.andnext.recyclerview.bridge.BridgeAdapter;
import club.andnext.recyclerview.bridge.BridgeAdapterProvider;
import club.andnext.recyclerview.bridge.BridgeBuilder;
import club.andnext.recyclerview.bridge.BridgeHolder;
import com.google.android.material.appbar.AppBarLayout;
import com.haiyunshan.whatsandroid.R;
import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;
import me.everything.android.ui.overscroll.adapters.StaticOverScrollDecorAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class TestCoordinatorLayout1Fragment extends Fragment {

    CoordinatorLayout coordinatorLayout;

    AppBarLayout appBarLayout;
    View headerView;

    RecyclerView recyclerView;

    public TestCoordinatorLayout1Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_test_coordinator_layout1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.coordinatorLayout = (CoordinatorLayout)view;
        coordinatorLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.w("AA", "event y = " + event.getY());

                return false;
            }
        });

        {
            this.appBarLayout = view.findViewById(R.id.app_bar);
            this.headerView = view.findViewById(R.id.header);
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


        coordinatorLayout.setStatusBarBackgroundColor(Color.RED);
        {
            VerticalOverScrollBounceEffectDecorator ov = new VerticalOverScrollBounceEffectDecorator(new IOverScrollDecoratorAdapter() {

                @Override
                public View getView() {
                    return coordinatorLayout;
                }

                @Override
                public boolean isInAbsoluteStart() {
                    return recyclerView.getTop() == coordinatorLayout.getChildAt(0).getHeight();
                }

                @Override
                public boolean isInAbsoluteEnd() {
                    return false;
                }
            });
            ov.attach();
        }

        {
            BridgeAdapter adapter = new BridgeAdapter(getActivity(), new BridgeAdapterProvider() {
                @Override
                public Object get(int position) {
                    return String.valueOf(position + 1);
                }

                @Override
                public int size() {
                    return 20;
                }
            });

            adapter.bind(String.class,
                    new BridgeBuilder(DemoViewHolder.class, DemoViewHolder.LAYOUT_RES_ID, this));

            recyclerView.setAdapter(adapter);
        }

        {
            coordinatorLayout.requestChildFocus(recyclerView, recyclerView);
        }

        {
            printBehavior("onActivityCreated");
        }

        {
            OverScrollRunner.attach(coordinatorLayout, recyclerView);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        printBehavior("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        printBehavior("onResume");
    }

    void printBehavior(String when) {

        {
            CoordinatorLayout.Behavior b = getBehavior(appBarLayout);
            Log.w("AA", when + " app bar behavior = " + b);
        }

        {
            CoordinatorLayout.Behavior b = getBehavior(recyclerView);
            Log.w("AA", when + " recycler view behavior = " + b);
        }
    }

    CoordinatorLayout.Behavior getBehavior(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            return ((CoordinatorLayout.LayoutParams)params).getBehavior();
        }

        return null;
    }

    /**
     *
     */
    private static class DemoViewHolder extends BridgeHolder<String> implements View.OnClickListener {

        static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

        TestCoordinatorLayout1Fragment parent;

        public DemoViewHolder(TestCoordinatorLayout1Fragment parent, View itemView) {
            super(itemView);

            this.parent = parent;
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
        public void onBind(String item, int position) {
            ((TextView)itemView).setText(item);

            Log.w("AA", "onBind = " + item);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                parent.printBehavior("onClick");

            }
        }
    }

    private static class OverScrollRunner extends OverScrollHelper implements IOverScrollUpdateListener {

        CoordinatorLayout layout;

        public static OverScrollRunner attach(CoordinatorLayout layout, RecyclerView recyclerView) {
            OverScrollDecorAdapter adapter = new OverScrollDecorAdapter(layout, recyclerView);

            OverScrollRunner r =  new OverScrollRunner(layout, adapter);
            r.attach();

            return r;
        }

        public OverScrollRunner(CoordinatorLayout layout, IOverScrollDecoratorAdapter viewAdapter) {
            super(viewAdapter);

            this.layout = layout;

            this.addOverScrollUpdateListener(this);
        }

        @Override
        protected void translateView(View view, float offset) {
//            layout.setTranslationY(offset);

            super.translateView(view, offset);

            layout.getChildAt(0).setTranslationY(offset);
        }

        @Override
        protected void translateViewAndEvent(View view, float offset, MotionEvent event) {
//            layout.setTranslationY(offset);
//            event.offsetLocation(offset - event.getY(0), 0f);

            super.translateViewAndEvent(view, offset, event);

            layout.getChildAt(0).setTranslationY(offset);
        }

        @Override
        public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {
            layout.getChildAt(0).setTranslationY(offset);
        }
    }

    private static class OverScrollDecorAdapter implements IOverScrollDecoratorAdapter {

        CoordinatorLayout layout;
        protected final RecyclerView mView;

        public OverScrollDecorAdapter(CoordinatorLayout layout, RecyclerView view) {
            this.layout = layout;
            mView = view;
        }

        @Override
        public View getView() {
            return mView;
        }

        @Override
        public boolean isInAbsoluteStart() {
            int index = layout.indexOfChild(mView);
            if (index != 0) {
                if (layout.getHeight() + layout.getChildAt(0).getHeight() != mView.getBottom()) {
                    return false;
                }
            }

            return !mView.canScrollVertically(-1);
        }

        @Override
        public boolean isInAbsoluteEnd() {
            int index = layout.indexOfChild(mView);
            if (index != 0) {
                if (layout.getHeight() != mView.getBottom()) {
                    return false;
                }
            }

            return !mView.canScrollVertically(1);
        }

    }
}

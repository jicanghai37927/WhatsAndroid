package me.everything.android.ui.overscroll.adapters;

import android.view.View;
import androidx.core.widget.NestedScrollView;

public class NestedScrollViewOverScrollDecorAdapter implements IOverScrollDecoratorAdapter {

    protected final NestedScrollView mView;

    public NestedScrollViewOverScrollDecorAdapter(NestedScrollView view) {
        mView = view;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public boolean isInAbsoluteStart() {
        return !mView.canScrollVertically(-1);
    }

    @Override
    public boolean isInAbsoluteEnd() {
        return !mView.canScrollVertically(1);
    }

}

package club.andnext.recyclerview.overscroll;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import club.andnext.recyclerview.swipe.SwipeActionHelper;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter;

/**
 *
 */
public class OverScrollHelper extends VerticalOverScrollBounceEffectDecorator {

    SwipeActionHelper helper;

    public static final OverScrollHelper attach(RecyclerView view) {

        RecyclerViewOverScrollDecorAdapter adapter = new RecyclerViewOverScrollDecorAdapter(view);
        OverScrollHelper decor = new OverScrollHelper(adapter, null);

        return decor;
    }

    public static final OverScrollHelper attach(RecyclerView view, SwipeActionHelper helper) {

        RecyclerViewOverScrollDecorAdapter adapter = new RecyclerViewOverScrollDecorAdapter(view);
        OverScrollHelper decor = new OverScrollHelper(adapter, helper);

        return decor;
    }

    public static final OverScrollHelper attach(RecyclerView view, ItemTouchHelper.Callback callback) {

        RecyclerViewOverScrollDecorAdapter adapter = new RecyclerViewOverScrollDecorAdapter(view, callback);
        OverScrollHelper decor = new OverScrollHelper(adapter, null);

        return decor;
    }


    OverScrollHelper(IOverScrollDecoratorAdapter viewAdapter, SwipeActionHelper helper) {
        super(viewAdapter);

        this.helper = helper;
    }

    @Override
    public void attach() {
        if (helper != null) {

            helper.setOnTouchListener(this);
            getView().setOverScrollMode(View.OVER_SCROLL_NEVER);

        } else {

            super.attach();

        }
    }

    @Override
    public void detach() {
        if (helper != null) {

            if (mCurrentState != mIdleState) {
                Log.w(TAG, "Decorator detached while over-scroll is in effect. You might want to add a precondition of that getCurrentState()==STATE_IDLE, first.");
            }
            helper.setOnTouchListener(null);
            getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        } else {

            super.detach();

        }
    }

}

package club.andnext.recyclerview.swipe;

import android.graphics.Canvas;
import androidx.annotation.NonNull;
import android.view.View;

import club.andnext.recyclerview.bridge.BridgeHolder;
import club.andnext.recyclerview.decoration.MarginDividerDecoration;

public abstract class SwipeViewHolder<T> extends BridgeHolder<T> implements SwipeActionHelper.Adapter, MarginDividerDecoration.Adapter {

    SwipeHolder swipeHolder;

    public SwipeViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public abstract int getLayoutResourceId();

    @Override
    public abstract void onViewCreated(@NonNull View view);

    @Override
    public void onBind(T item, int position) {
        if (swipeHolder != null) {
            swipeHolder.reset();
        }
    }

    @Override
    public void onActionBegin(SwipeActionHelper helper, int action) {

    }

    @Override
    public void onActionEnd(SwipeActionHelper helper, int action) {

    }

    protected void setSwipeHolder(SwipeHolder holder) {
        this.swipeHolder = holder;
    }

    protected SwipeHolder getSwipeHolder() {
        return this.swipeHolder;
    }

    @Override
    public boolean isSwiped(SwipeActionHelper helper) {
        if (swipeHolder != null) {
            return swipeHolder.isSwiped();
        }

        return false;
    }

    @Override
    public SwipeActionHelper.Adapter getActive(SwipeActionHelper helper) {
        if (swipeHolder != null) {
            if (swipeHolder.getActive() != null) {
                return this;
            }
        }

        return null;
    }

    @Override
    public int getDirection(SwipeActionHelper helper) {
        if (swipeHolder != null) {
            return swipeHolder.getDirection();
        }

        return 0;
    }

    @Override
    public View getSwipeView(SwipeActionHelper helper) {
        if (swipeHolder != null) {
            return swipeHolder.getSwipeView();
        }

        return null;
    }

    @Override
    public void clear(SwipeActionHelper helper) {
        if (swipeHolder != null) {
            swipeHolder.clear();
        }
    }

    @Override
    public void onBegin(SwipeActionHelper helper) {
        if (swipeHolder != null) {
            swipeHolder.onBegin();
        }
    }

    @Override
    public void onMove(SwipeActionHelper helper, float deltaX) {
        if (swipeHolder != null) {
            swipeHolder.onMove(deltaX);
        }
    }

    @Override
    public void onEnd(SwipeActionHelper helper, float velocityX) {
        if (swipeHolder != null) {
            swipeHolder.onEnd(velocityX);
        }
    }

    @Override
    public void onDraw(SwipeActionHelper helper, Canvas canvas) {
        if (swipeHolder != null) {
            swipeHolder.onDraw(canvas);
        }
    }

    @Override
    public void onDrawOver(SwipeActionHelper helper, Canvas canvas) {
        if (swipeHolder != null) {
            swipeHolder.onDrawOver(canvas);
        }
    }

    @Override
    public void onClear(SwipeActionHelper helper, int direction) {

    }

    @Override
    public float getTranslation(MarginDividerDecoration decoration) {
        if (swipeHolder != null) {
            return swipeHolder.getTranslationX();
        }

        return 0;
    }
}

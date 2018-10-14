package club.andnext.recyclerview.swipe;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewParent;

import java.util.ArrayList;

public class SwipeHolder {

    View container;
    View swipeView;

    boolean isDrag;

    SwipeRunner activeRunner;
    ArrayList<SwipeRunner> list;

    Rect bounds = new Rect();
    SwipeActionHelper helper;

    public SwipeHolder(SwipeActionHelper helper, View container, View swipeView) {
        this.container = container;
        this.swipeView = swipeView;

        this.activeRunner = null;
        this.list = new ArrayList<>();

        this.helper = helper;
    }

    public void add(SwipeRunner runner) {
        list.add(runner);

        runner.set(this, swipeView);
    }

    public float getTranslationX() {
        return swipeView.getTranslationX();
    }

    public View getSwipeView() {
        return this.swipeView;
    }

    public boolean isSwiped() {
        for (SwipeRunner r : list) {
            if (r.isSwiped()) {
                return true;
            }
        }

        return (swipeView.getTranslationX() != 0);
    }

    public SwipeHolder getActive() {
        if (activeRunner != null) {
            return this;
        }

        return null;
    }

    public int getDirection() {
        int value = 0;

        for (SwipeRunner r : list) {
            value = value | r.direction;
        }

        return value;
    }

    public void reset() {
        for (SwipeRunner r : list) {
            r.reset();
        }

        swipeView.setTranslationX(0);

        this.invalidate();
    }

    public void clear() {
        if (activeRunner != null) {
            activeRunner.clear();
        }

        this.activeRunner = null;
    }

    public void onBegin() {
        if (activeRunner != null) {
            activeRunner.onBegin();
        }

        this.isDrag = false;
    }

    public void onMove(float deltaX) {

        if ((activeRunner == null) && !isDrag && (deltaX != 0)) {

            for (SwipeRunner r : list) {
                activeRunner = r.accept(deltaX);
                if (activeRunner != null) {
                    break;
                }
            }

            if (activeRunner != null) {
                activeRunner.onBegin();

                for (SwipeRunner r : list) {
                    if (r != activeRunner) {
                        r.reset();
                    }
                }
            }
        }

        if (activeRunner != null) {
            activeRunner.onMove(deltaX);
        }

        if (!isDrag) {
            this.isDrag = (deltaX != 0);
        }

    }

    public void onEnd(float velocityX) {
        if (activeRunner != null) {
            activeRunner.onEnd(velocityX);
        }

        this.isDrag = false;
    }

    public void onDraw(Canvas canvas) {

        if (activeRunner != null) {
            activeRunner.onDraw(canvas);
            return;
        }

        for (SwipeRunner r : list) {
            if (r.isSwiped()) {
                r.onDraw(canvas);
            }
        }

    }

    public void onDrawOver(Canvas canvas) {

        if (activeRunner != null) {
            activeRunner.onDrawOver(canvas);
            return;
        }

        for (SwipeRunner r : list) {
            if (r.isSwiped()) {
                r.onDrawOver(canvas);
            }
        }

    }

    void clear(SwipeRunner runner) {
        if (activeRunner == runner) {
            activeRunner = null;
        }

        if (!this.isSwiped()) {
            helper.clear(this.getViewHolder(), runner.direction);
        }
    }

    public void invalidate() {
        RecyclerView recyclerView = this.getRecyclerView();
        if (recyclerView == null) {
            return;
        }

        View child = recyclerView.findContainingItemView(container);
        if (child != null) {
            recyclerView.getDecoratedBoundsWithMargins(child, bounds);
            recyclerView.invalidate(bounds);
        }

    }

    public RecyclerView.ViewHolder getViewHolder() {
        RecyclerView recyclerView = this.getRecyclerView();
        if (recyclerView == null) {
            return null;
        }

        return recyclerView.findContainingViewHolder(container);
    }

    public RecyclerView getRecyclerView() {
        ViewParent parent = container.getParent();

        while (parent != null) {
            if (parent instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView)parent;
                return recyclerView;
            }

            parent = parent.getParent();
        }

        return null;
    }

    public void notifyActionBegin(SwipeRunner runner, int action) {
        if (runner != this.activeRunner) {
            return;
        }

        RecyclerView.ViewHolder viewHolder = this.getViewHolder();

        if (viewHolder != null) {
            helper.notifyActionBegin(viewHolder, action);
        }

    }

    public void notifyActionEnd(SwipeRunner runner, int action) {
        if (runner != this.activeRunner) {
            return;
        }

        RecyclerView.ViewHolder viewHolder = this.getViewHolder();

        if (viewHolder != null) {
            helper.notifyActionEnd(viewHolder, action);
        }

    }
}

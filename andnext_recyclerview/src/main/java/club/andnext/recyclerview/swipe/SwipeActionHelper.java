package club.andnext.recyclerview.swipe;

import android.graphics.Canvas;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import club.andnext.overscroll.OverScrollHelper;
import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollState;
import me.everything.android.ui.overscroll.IOverScrollStateListener;

public class SwipeActionHelper extends RecyclerView.ItemDecoration {

    public static final int DIRECTION_LTR = ItemTouchHelper.RIGHT;
    public static final int DIRECTION_RTL = ItemTouchHelper.LEFT;

    public static final int ACTION_LEFT = ItemTouchHelper.RIGHT;
    public static final int ACTION_RIGHT = ItemTouchHelper.LEFT;

    boolean enable;

    RecyclerView recyclerView;

    ItemTouchHelper itemTouchHelper;
    SwipeTouchCallback itemTouchCallback;

    ForbidTouchListener forbidTouchListener;
    SwipeItemTouchListener onItemTouchListener;
    SwipeScrollListener scrollListener;

    OnSwipeActionListener onSwipeActionListener;
    View.OnTouchListener onTouchListener;

    OverScrollHelper overScrollHelper;

    boolean isSwiped;
    SwipeActionDelegate swipeActionDelegate;

    public SwipeActionHelper() {
        this(null);
    }

    public SwipeActionHelper(OverScrollHelper overScrollHelper) {
        this.overScrollHelper = overScrollHelper;
        this.enable = true;

        this.itemTouchCallback = new SwipeTouchCallback();
        this.itemTouchHelper = new ItemTouchHelper(itemTouchCallback);

        this.forbidTouchListener = new ForbidTouchListener();
        this.onItemTouchListener = new SwipeItemTouchListener();
        this.scrollListener = new SwipeScrollListener();

        this.isSwiped = false;
        this.swipeActionDelegate = new SwipeActionDelegate(this);

        if (overScrollHelper != null) {
            overScrollHelper.setOverScrollStateListener(new OverScrollStateListener());
        }
    }

    public ItemTouchHelper.Callback getCallback() {
        return itemTouchCallback;
    }

    public void attach(@Nullable RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        // be first please
        {
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        {
            recyclerView.addItemDecoration(this);
            recyclerView.addOnItemTouchListener(forbidTouchListener);
            recyclerView.addOnItemTouchListener(onItemTouchListener);
            recyclerView.addOnScrollListener(scrollListener);
        }
    }

    public boolean isActive() {
        RecyclerView.ViewHolder viewHolder = this.swipeActionDelegate.getActive(recyclerView);
        if (viewHolder != null) {
            return true;
        }

        return false;
    }

    public boolean isSwiped() {
        RecyclerView.ViewHolder viewHolder = this.swipeActionDelegate.getActive(recyclerView);
        if (viewHolder != null) {
            return true;
        }

        return swipeActionDelegate.isSwiped(recyclerView);
    }

    public void clear() {
        swipeActionDelegate.clear(recyclerView);

        itemTouchCallback.selected = null;
        onItemTouchListener.active = null;

    }

    public void invalidate() {
        recyclerView.invalidate();
    }

    public void setForbidden(boolean value) {
        forbidTouchListener.setForbidden(value);
    }

    public void setForbidden(boolean value, long milliseconds) {
        forbidTouchListener.setForbidden(value, milliseconds);
    }

    public void setOnSwipeActionListener(OnSwipeActionListener listener) {
        this.onSwipeActionListener = listener;
    }

    public void setOnTouchListener(View.OnTouchListener listener) {
        this.onTouchListener = listener;
    }

    public void setEnable(boolean value) {
        this.enable = value;
    }

    void clear(RecyclerView.ViewHolder viewHolder, int direction) {

        if (itemTouchCallback.selected == viewHolder) {
            itemTouchCallback.selected = null;
        }

        if (onItemTouchListener.active == viewHolder) {
            onItemTouchListener.active = null;
        }

        {
            swipeActionDelegate.onClear(viewHolder, direction);
        }

        this.notifySwipeChanged();
    }

    void notifyActionBegin(RecyclerView.ViewHolder viewHolder, int action) {

        swipeActionDelegate.onActionBegin(viewHolder, action);

    }

    void notifyActionEnd(RecyclerView.ViewHolder viewHolder, int action) {

        swipeActionDelegate.onActionEnd(viewHolder, action);

    }

    void notifySwipeChanged() {

        boolean newValue = this.isSwiped();
        if (newValue ^ isSwiped) {
            boolean oldValue = isSwiped;
            isSwiped = newValue;

            if (onSwipeActionListener != null) {
                onSwipeActionListener.onSwipeChanged(this, oldValue, newValue);
            }
        }

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.ViewHolder holder = parent.findContainingViewHolder(child);

            swipeActionDelegate.onDraw(holder, c);
        }

        super.onDraw(c, parent, state);

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.ViewHolder holder = parent.findContainingViewHolder(child);

            swipeActionDelegate.onDrawOver(holder, c);
        }

        super.onDrawOver(c, parent, state);

    }

    /**
     *
     */
    private class SwipeTouchCallback extends ItemTouchHelper.Callback {

        RecyclerView.ViewHolder selected = null;

        @Override
        public boolean isItemViewSwipeEnabled() {
            return enable;
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return Float.MAX_VALUE;
        }

        @Override
        public float getSwipeEscapeVelocity(float defaultValue) {
            return Float.MAX_VALUE;
        }

        @Override
        public float getSwipeVelocityThreshold(float defaultValue) {
            return Float.MAX_VALUE;
        }

        @Override
        public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
//            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
            return 0;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

            // we don't need it
            {
//                super.clearView(recyclerView, viewHolder);
            }
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = 0;
            if (enable) {
                if (swipeActionDelegate.getActive(recyclerView) == viewHolder) {
                    swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                } else {
                    swipeFlags = swipeActionDelegate.getDirection(viewHolder);
                }
            }

            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

            if (overScrollHelper != null) {
                if (viewHolder == null) {
                    overScrollHelper.attach();
                } else {
                    overScrollHelper.detach();
                }
            }

            // we don't need it
            {
//                super.onSelectedChanged(viewHolder, actionState);
            }

            if (this.selected != null && viewHolder != null && selected != viewHolder) {
                swipeActionDelegate.clear(recyclerView);
            }

            {
                if (viewHolder != null) {
                    swipeActionDelegate.onBegin(viewHolder);
                } else {
                    if (selected != null) {
                        float velocityX = itemTouchHelper.mVelocityX;

                        swipeActionDelegate.onEnd(selected, velocityX);
                    }
                }
            }

            {
                notifySwipeChanged();
            }

            this.selected = viewHolder;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {

            // forbidden super.onChildDraw to avoid ViewHolder translation
            {
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            if (isCurrentlyActive) {

                {
                    swipeActionDelegate.onMove(viewHolder, dX);
                }

                {
                    notifySwipeChanged();
                }

            }

        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

            // like onChildDraw, we don't need it
            {
//            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }

        @Override
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

    }

    /**
     *
     */
    private class SwipeItemTouchListener implements RecyclerView.OnItemTouchListener {

        RecyclerView.ViewHolder active;

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            if (!enable) {
                return false;
            }

            final int action = e.getActionMasked();

            if (action == MotionEvent.ACTION_DOWN) {

                this.active = null;

                if (swipeActionDelegate.isSwiped(rv)) {
                    boolean result = true;

                    rv.removeOnItemTouchListener(this);

                    View view = rv.findChildViewUnder(e.getX(), e.getY());
                    RecyclerView.ViewHolder activeHolder = swipeActionDelegate.getActive(rv);

                    if (activeHolder != null) {

                        if (view == null) {

                            swipeActionDelegate.clear(recyclerView);

                        } else {

                            RecyclerView.ViewHolder underHolder = rv.findContainingViewHolder(view);
                            if (activeHolder != underHolder) {

                                swipeActionDelegate.clear(recyclerView);

                            } else {

                                view = activeHolder.itemView;
                                View swipeView = swipeActionDelegate.getSwipeView(activeHolder);
                                if (swipeView == null) {
                                    result = false;
                                } else {
                                    result = hitTest(view, e.getX(), e.getY(),
                                            view.getLeft() + swipeView.getTranslationX(),
                                            view.getTop() + swipeView.getTranslationY());
                                }

                                this.active = activeHolder;
                            }

                        }
                    }

                    rv.addOnItemTouchListener(this);
                    return result;
                }

            } else if (action == MotionEvent.ACTION_MOVE) {

            } else if (action == MotionEvent.ACTION_UP) {
                if (active != null) {
                    swipeActionDelegate.clear(recyclerView);
                }

                active = null;
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    /**
     *
     */
    private class SwipeScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                if (onItemTouchListener.active != null) {

                    swipeActionDelegate.clear(recyclerView);

                    onItemTouchListener.active = null;
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    /**
     *
     */
    private class OverScrollStateListener implements IOverScrollStateListener {

        @Override
        public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
            if (oldState == IOverScrollState.STATE_IDLE && newState != IOverScrollState.STATE_IDLE) {
                clear();
            }
        }
    }

    /**
     *
     */
    private static class ForbidTouchListener implements RecyclerView.OnItemTouchListener {

        boolean forbidden;
        long expire;

        ForbidTouchListener() {
            this.forbidden = false;
            this.expire = -1;
        }

        void setForbidden(boolean value) {
            this.forbidden = value;
            this.expire = -1;
        }

        void setForbidden(boolean value, long milliseconds) {
            this.forbidden = value;
            if (milliseconds <= 0) {
                this.expire = -1;
            } else {
                this.expire = System.currentTimeMillis() + milliseconds;
            }
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            if (forbidden) {

                if (expire <= 0) { // 永不过期
                    return true;
                }

                if (System.currentTimeMillis() < expire) { // 有效时间内，禁止操作
                    return true;
                }
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    /**
     *
     */
    private static class SwipeActionDelegate {

        private static final String TAG = SwipeActionDelegate.class.getSimpleName();

        SwipeActionHelper helper;

        SwipeActionDelegate(SwipeActionHelper helper) {
            this.helper = helper;
        }

        public void clear(RecyclerView recyclerView) {
            for (int i = 0, count = recyclerView.getChildCount(); i < count; i++) {
                View view = recyclerView.getChildAt(i);
                RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(view);
                Adapter adapter = this.getAdapter(holder);
                if (adapter != null) {
                    adapter.clear(helper);
                }
            }


        }

        public boolean isSwiped(RecyclerView recyclerView) {

            for (int i = 0, count = recyclerView.getChildCount(); i < count; i++) {
                View view = recyclerView.getChildAt(i);
                RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(view);
                Adapter adapter = this.getAdapter(holder);
                if (adapter != null && adapter.isSwiped(helper)) {
                    return true;
                }
            }

            return false;
        }

        public RecyclerView.ViewHolder getActive(RecyclerView recyclerView) {

            for (int i = 0, count = recyclerView.getChildCount(); i < count; i++) {
                View view = recyclerView.getChildAt(i);
                RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(view);
                Adapter adapter = this.getAdapter(holder);
                if (adapter != null && adapter.getActive(helper) != null) {
                    return holder;
                }
            }

            return null;
        }

        public int getDirection(RecyclerView.ViewHolder viewHolder) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                return adapter.getDirection(helper);
            }

            return 0;
        }

        public View getSwipeView(RecyclerView.ViewHolder viewHolder) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                return adapter.getSwipeView(helper);
            }

            return null;
        }

        public void clear(RecyclerView.ViewHolder viewHolder) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                adapter.clear(helper);
            }
        }

        public void onBegin(RecyclerView.ViewHolder viewHolder) {
            Log.v(TAG, "onBegin ViewHolder = " + viewHolder);

            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                adapter.onBegin(helper);
            }

        }

        public void onMove(RecyclerView.ViewHolder viewHolder, float deltaX) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                adapter.onMove(helper, deltaX);
            }

        }

        public void onEnd(RecyclerView.ViewHolder viewHolder, float velocityX) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                adapter.onEnd(helper, velocityX);
            }

        }

        public void onDraw(RecyclerView.ViewHolder viewHolder, Canvas canvas) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                adapter.onDraw(helper, canvas);
            }
        }

        public void onDrawOver(RecyclerView.ViewHolder viewHolder, Canvas canvas) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                adapter.onDrawOver(helper, canvas);
            }
        }

        void onActionBegin(RecyclerView.ViewHolder viewHolder, int action) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                adapter.onActionBegin(helper, action);
            }
        }

        void onActionEnd(RecyclerView.ViewHolder viewHolder, int action) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                adapter.onActionEnd(helper, action);
            }
        }

        void onClear(RecyclerView.ViewHolder viewHolder, int direction) {
            Adapter adapter = this.getAdapter(viewHolder);
            if (adapter != null) {
                adapter.onClear(helper, direction);
            }
        }

        Adapter getAdapter(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof Adapter) {
                return (Adapter)viewHolder;
            }

            return null;
        }

    }

    private static boolean hitTest(View child, float x, float y, float left, float top) {
        return x >= left
                && x <= left + child.getWidth()
                && y >= top
                && y <= top + child.getHeight();
    }

    /**
     *
     */
    public interface OnSwipeActionListener {

        void onSwipeChanged(SwipeActionHelper helper, boolean oldValue, boolean newValue);

    }

    /**
     *
     */
    public interface Adapter {

        boolean isSwiped(SwipeActionHelper helper);

        Adapter getActive(SwipeActionHelper helper);

        int getDirection(SwipeActionHelper helper);

        View getSwipeView(SwipeActionHelper helper);

        void clear(SwipeActionHelper helper);

        void onBegin(SwipeActionHelper helper);

        void onMove(SwipeActionHelper helper, float deltaX);

        void onEnd(SwipeActionHelper helper, float velocityX);

        void onDraw(SwipeActionHelper helper, Canvas canvas);

        void onDrawOver(SwipeActionHelper helper, Canvas canvas);

        void onActionBegin(SwipeActionHelper helper, int action);

        void onActionEnd(SwipeActionHelper helper, int action);

        void onClear(SwipeActionHelper helper, int direction);
    }

}

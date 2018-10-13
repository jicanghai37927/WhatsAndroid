package club.andnext.recyclerview.swipe;

import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class SwipeActionHelper extends RecyclerView.ItemDecoration implements View.OnTouchListener {

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

    VelocityHelper velocityHelper;

    boolean isSwiped;
    SwipeActionDelegate swipeActionDelegate;

    public SwipeActionHelper() {
        this.enable = true;

        this.itemTouchCallback = new SwipeTouchCallback();
        this.itemTouchHelper = new ItemTouchHelper(itemTouchCallback);

        this.forbidTouchListener = new ForbidTouchListener();
        this.onItemTouchListener = new SwipeItemTouchListener();
        this.scrollListener = new SwipeScrollListener();

        this.velocityHelper = new VelocityHelper();

        this.isSwiped = false;
        this.swipeActionDelegate = new SwipeActionDelegate(this);
    }

    public ItemTouchHelper.Callback getCallback() {
        return itemTouchCallback;
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        {
            recyclerView.setOnTouchListener(this);
        }

        {
            recyclerView.addItemDecoration(this);
            recyclerView.addOnItemTouchListener(forbidTouchListener);
            recyclerView.addOnItemTouchListener(onItemTouchListener);
            recyclerView.addOnScrollListener(scrollListener);
        }

        {
            itemTouchHelper.attachToRecyclerView(recyclerView);
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
        RecyclerView.ViewHolder viewHolder = swipeActionDelegate.getActive(recyclerView);
        if (viewHolder != null) {
            swipeActionDelegate.clear(viewHolder);

            itemTouchCallback.selected = null;
            onItemTouchListener.active = null;
        }
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

    void clear(RecyclerView.ViewHolder viewHolder) {

        if (itemTouchCallback.selected == viewHolder) {
            itemTouchCallback.selected = null;
        }

        if (onItemTouchListener.active == viewHolder) {
            onItemTouchListener.active = null;
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        velocityHelper.onTouch(v, event);

        if (onTouchListener == null) {
            return false;
        }

        return onTouchListener.onTouch(v, event);

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
            return 1000000.f;
        }

        @Override
        public float getSwipeEscapeVelocity(float defaultValue) {
            return 1000000.f;
        }

        @Override
        public float getSwipeVelocityThreshold(float defaultValue) {
            return 1000000.f;
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
                swipeFlags = swipeActionDelegate.getDirection(viewHolder);
            }

            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

            // we don't need it
            {
//                super.onSelectedChanged(viewHolder, actionState);
            }

            {
                if (viewHolder != null) {
                    swipeActionDelegate.onBegin(viewHolder);
                } else {
                    if (selected != null) {
                        swipeActionDelegate.onEnd(selected, velocityHelper.getXVelocity());
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
                    RecyclerView.ViewHolder viewHolder = swipeActionDelegate.getActive(rv);

                    if (viewHolder != null) {
                        if (view == null) {
                            swipeActionDelegate.clear(viewHolder);
                        } else {
                            RecyclerView.ViewHolder underHolder = rv.findContainingViewHolder(view);
                            if (viewHolder != underHolder) {

                                swipeActionDelegate.clear(viewHolder);

                            } else {

                                view = viewHolder.itemView;
                                View swipeView = swipeActionDelegate.getSwipeView(viewHolder);
                                if (swipeView == null) {
                                    result = false;
                                } else {
                                    result = hitTest(view, e.getX(), e.getY(),
                                            view.getLeft() + swipeView.getTranslationX(),
                                            view.getTop() + swipeView.getTranslationY());
                                }

                                this.active = viewHolder;
                            }
                        }
                    }

                    rv.addOnItemTouchListener(this);
                    return result;
                }

            } else if (action == MotionEvent.ACTION_MOVE) {

            } else if (action == MotionEvent.ACTION_UP) {
                if (active != null) {
                    RecyclerView.ViewHolder viewHolder = swipeActionDelegate.getActive(recyclerView);
                    if (viewHolder != null) {
                        swipeActionDelegate.clear(viewHolder);
                    }

                    active = null;
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
    private class SwipeScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                if (onItemTouchListener.active != null) {
                    RecyclerView.ViewHolder viewHolder = swipeActionDelegate.getActive(recyclerView);
                    if (viewHolder != null) {
                        swipeActionDelegate.clear(viewHolder);
                    }

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
    private static class ForbidTouchListener implements RecyclerView.OnItemTouchListener {

        boolean forbidden;
        long expire;

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
     * 检测移动速度
     *
     */
    private static class VelocityHelper implements View.OnTouchListener {

        private static final int INVALID_POINTER = -1;

        private int mScrollPointerId = INVALID_POINTER;

        private VelocityTracker mVelocityTracker;

        float xVelocity;
        float yVelocity;

        public VelocityHelper() {
            this.xVelocity = 0;
            this.yVelocity = 0;
        }

        public float getXVelocity() {
            return xVelocity;
        }

        public float getYVelocity() {
            return yVelocity;
        }

        @Override
        public boolean onTouch(View v, MotionEvent e) {

            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(e);

            int action = e.getActionMasked();
            int actionIndex = e.getActionIndex();

            switch (action) {

                case MotionEvent.ACTION_DOWN: {
                    mScrollPointerId = e.getPointerId(0);

                    break;
                }

                case MotionEvent.ACTION_UP: {
                    mVelocityTracker.addMovement(e);

                    mVelocityTracker.computeCurrentVelocity(1000, Float.MAX_VALUE);
                    this.xVelocity = mVelocityTracker.getXVelocity(mScrollPointerId);
                    this.yVelocity = mVelocityTracker.getYVelocity(mScrollPointerId);

                    mVelocityTracker.clear();

                    break;
                }

                case MotionEvent.ACTION_CANCEL: {

                    mVelocityTracker.clear();

                    break;
                }

                case MotionEvent.ACTION_POINTER_DOWN: {

                    mScrollPointerId = e.getPointerId(actionIndex);
                    break;

                }

                case MotionEvent.ACTION_POINTER_UP: {

                    actionIndex = e.getActionIndex();
                    if (e.getPointerId(actionIndex) == mScrollPointerId) {
                        // Pick a new pointer to pick up the slack.
                        final int newIndex = actionIndex == 0 ? 1 : 0;
                        mScrollPointerId = e.getPointerId(newIndex);
                    }

                    break;
                }
            }

            return false;
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
    }

}

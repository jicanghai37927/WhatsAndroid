package club.andnext.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.customview.widget.ViewDragHelper;

/**
 *
 */
public class NavigationLayout extends FrameLayout {

    ViewDragHelper viewDragHelper;

    ImageView previousView;
    View shadowView;
    FrameLayout targetView;

    String activityHash = "";
    boolean edgeTouchable = true;

    OnNavigationListener onNavigationListener;

    public NavigationLayout(@NonNull Context context) {
        this(context, null);
    }

    public NavigationLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NavigationLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.init(context);
    }

    void init(Context context) {

        {
            this.viewDragHelper = ViewDragHelper.create(this, 2.f, new DragCallback());
            viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        }

        {
            this.previousView = new AppCompatImageView(context);

            previousView.setVisibility(View.INVISIBLE);
            this.addView(previousView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        {
            int[] colors = new int[] { Color.TRANSPARENT, 0x10000000, 0x30000000};
            GradientDrawable d = new GradientDrawable();
            d.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            d.setColors(colors);

            this.shadowView = new View(context);
            shadowView.setBackground(d);

            int width = viewDragHelper.getEdgeSize();
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.LEFT;

            shadowView.setVisibility(View.INVISIBLE);
            this.addView(shadowView, layoutParams);

        }

        {
            this.targetView = new FrameLayout(context);

            {
                TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{
                        android.R.attr.windowBackground
                });
                int background = a.getResourceId(0, 0);
                a.recycle();

                targetView.setBackgroundResource(background);
            }

            this.addView(targetView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

    }

    public Activity getActivity() {
        return (Activity)(getContext());
    }

    public void setOnNavigationListener(OnNavigationListener listener) {
        this.onNavigationListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!this.isEnabled()) {
            return super.onInterceptTouchEvent(ev);
        }

        if (TextUtils.isEmpty(activityHash)) {
            return super.onInterceptTouchEvent(ev);
        }

        if (this.getChildCount() < 2) {
            return super.onInterceptTouchEvent(ev);
        }

        if (viewDragHelper == null) {
            return super.onInterceptTouchEvent(ev);
        }

        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);

        if (!result && !edgeTouchable) {
            int action = ev.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                if (ev.getX() < viewDragHelper.getEdgeSize()) {
                    result = true;
                }
            }
        }

        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isEnabled()) {
            return super.onTouchEvent(event);
        }

        if (TextUtils.isEmpty(this.activityHash)) {
            return super.onTouchEvent(event);
        }

        if (this.getChildCount() < 2) {
            return super.onTouchEvent(event);
        }

        if (viewDragHelper == null) {
            return super.onTouchEvent(event);
        }

        {
            viewDragHelper.processTouchEvent(event);
        }

        return true;
    }

    @Override
    public void computeScroll() {

        if (viewDragHelper != null && viewDragHelper.continueSettling(true)) {
            this.postInvalidateOnAnimation();
        }

    }

    public void setEdgeTouchable(boolean value) {
        this.edgeTouchable = value;
    }

    String getActivityHash() {
        return this.activityHash;
    }

    Drawable getPrevious() {
        Drawable d = previousView.getDrawable();

        return d;

    }

    void setPrevious(String activityHash, Bitmap bm) {
        this.activityHash = activityHash;

        previousView.setImageBitmap(bm);
    }

    void setPrevious(String activityHash, Drawable d) {
        this.activityHash = activityHash;

        previousView.setImageDrawable(d);
    }

    void setNext(View view) {
        this.targetView.removeAllViews();
        this.targetView.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    View getNext() {
        if (targetView.getChildCount() == 0) {
            return null;
        }

        View child = targetView.getChildAt(0);

        return child;
    }

    View getTargetView() {
        return this.targetView;
    }

    View getPreviousView() {
        return this.previousView;
    }

    View getShadowView() {
        return this.shadowView;
    }

    /**
     *
     */
    private class DragCallback extends ViewDragHelper.Callback {

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == ViewDragHelper.STATE_IDLE && getTargetView().getLeft() == 0) {
                getPreviousView().setVisibility(View.INVISIBLE);
                getShadowView().setVisibility(View.INVISIBLE);
            }

            if (state == ViewDragHelper.STATE_IDLE && getTargetView().getLeft() >= getWidth()) {
                if (onNavigationListener != null) {
                    onNavigationListener.onComplete(NavigationLayout.this);
                }
            }
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {

            {
                float factor = 2.f;

                float x = left;
                float width = changedView.getWidth();
                x = (x > width)? width: x;

                x = x / factor;
                width = width / factor;

                x = x - width;

                View prev = getPreviousView();
                prev.setTranslationX(x);

                if (prev.getVisibility() != View.VISIBLE) {
                    prev.setVisibility(View.VISIBLE);
                }
            }

            {
                View shadow = getShadowView();
                float x = left - shadow.getWidth();
                shadow.setTranslationX(x);

                if (shadow.getVisibility() != View.VISIBLE) {
                    shadow.setVisibility(View.VISIBLE);
                }
            }

        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {


        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (releasedChild.getLeft() == 0) {
                return;
            }

            boolean extend = true;

            boolean fling = (Math.abs(xvel) >= 1200);
            if (fling) {
                if (xvel < 0) {
                    extend = false;
                }
            } else {
                if (releasedChild.getLeft() < getWidth() / 2) {
                    extend = false;
                }
            }

            if (extend) {
                int finalLeft = getWidth() + getShadowView().getWidth();
                viewDragHelper.settleCapturedViewAt(finalLeft, releasedChild.getTop());
            } else {
                viewDragHelper.settleCapturedViewAt(0, releasedChild.getTop());
            }

            invalidate();
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {

        }

        @Override
        public boolean onEdgeLock(int edgeFlags) {
            return super.onEdgeLock(edgeFlags);

        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            View child = getTargetView();
            viewDragHelper.captureChildView(child, pointerId);
        }

        @Override
        public int getOrderedChildIndex(int index) {
            return super.getOrderedChildIndex(index);
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return child.getWidth();
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return 0; // disable vertical drag
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            left = (left < 0)? 0: left;
            return left;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return child.getTop(); // never changed
        }

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return false;
        }
    }

    public interface OnNavigationListener {

        void onComplete(NavigationLayout view);

    }
}

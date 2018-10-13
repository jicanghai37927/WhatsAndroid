package club.andnext.recyclerview.swipe.runner;

import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.recyclerview.swipe.SwipeRunner;

/**
 * Action保持在左侧，超过max，自动触发Action
 *
 */
public class SnapLeftRunner extends SwipeRunner {

    View actionView;
    View slideView;

    int snapState;

    public SnapLeftRunner() {
        super(DIRECTION_LTR);
    }

    @Override
    public SwipeRunner accept(float distance) {
        if (actionView == null || slideView == null) {
            return null;
        }

        return super.accept(distance);
    }

    @Override
    public void add(View view) {
        this.actionView = view;
        this.slideView = null;

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;
            if (group.getChildCount() > 0) {
                View child = group.getChildAt(0);
                this.slideView = child;
            }
        }
    }

    @Override
    public void reset() {
        super.reset();

        this.snapState = SNAP_LEFT;

        {
            this.actionView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void onBegin() {
        super.onBegin();

        this.snapState = SNAP_LEFT;

        {
            this.actionView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMove(float deltaX) {
        super.onMove(deltaX);

        float tx = this.getTranslationX();
        float max = this.getMaxDistance();

        // child
        {
            int state = (tx >= max) ? SNAP_RIGHT : SNAP_LEFT;
            if (snapState != state) {

                this.removeAnimation(ANIM_SLIDE);

                float x = this.getFinalX();
                float startX = this.getSlideX(x, snapState);
                float targetX = this.getSlideX(x, state);
                if (startX != targetX) {
                    AnimationHelper anim = new AnimationHelper(ANIM_SLIDE, startX, targetX);
                    this.addAnimation(anim);
                }
            }

            this.snapState = state;
        }

    }

    @Override
    public void onEnd(float velocityX) {

        float x = this.getFinalX();
        float tx = this.getTranslationX();
        float max = this.getMaxDistance();

        boolean showAction = false;
        int slideWidth = slideView.getWidth();

        {
            this.clearAnimation();
        }

        while (true) {
            if (x == 0) {
                break;
            }

            if (x < 0) {
                float start = x;
                float end = 0;

                AnimationHelper anim = new AnimationHelper(ANIM_RECOVER, start, end);
                this.addAnimation(anim);

                break;
            }

            if (tx >= max) {
                this.snapAction = SwipeActionHelper.ACTION_LEFT;

                float start = x;
                float end = 0;

                AnimationHelper anim = new AnimationHelper(ANIM_RECOVER, start, end);
                this.addAnimation(anim);

                break;
            }

            boolean fling = this.isFling(velocityX);
            if (fling) {
                showAction = true;

                if (velocityX < 0) {
                    showAction = false;
                }

            } else {

                if (tx > slideWidth / 2) {
                    showAction = true;
                }

            }

            {
                float start = x;
                float end = (showAction) ? (slideWidth) : 0;

                AnimationHelper anim = new AnimationHelper(ANIM_RECOVER, start, end);
                this.addAnimation(anim);

                break;
            }
        }

        {
            super.onEnd(velocityX);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float x = this.getFinalX();

        // swipe view
        {
            float tx = x;
            swipeView.setTranslationX(tx);
        }

        // parent
        {
            float tx = x;
            tx -= swipeView.getWidth();
            actionView.setTranslationX(tx);
        }

        // child
        {
            float tx = x;
            tx = getSlideX(tx, this.snapState);

            AnimationHelper anim = getAnimation(ANIM_SLIDE);
            if (anim != null) {
                float fx = tx;

                tx = anim.getValue();
                if (snapState == SNAP_LEFT) {
                    tx = (tx < fx) ? fx : tx;
                }
            }

            slideView.setTranslationX(tx);
        }
    }

    @Override
    public float getFinalX() {

        AnimationHelper anim;

        if ((anim = getAnimation(ANIM_RECOVER)) != null) {
            this.initialDistance = anim.getValue();
            return initialDistance;
        }

        return super.getFinalX();
    }

    @Override
    protected float getMaxDistance() {
        float f = 0.618f;
        float max = f * swipeView.getWidth();
        return max;
    }

    float getSlideX(float tx, int state) {

        if (state == SNAP_RIGHT) {
            tx = 0;
        } else {
            tx -= slideView.getWidth();
            tx = -tx;
            tx = (tx > 0)? 0: tx;
        }

        return tx;
    }

}

package club.andnext.recyclerview.swipe;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public abstract class SwipeRunner {

    protected static final int DIRECTION_LTR = SwipeActionHelper.DIRECTION_LTR;
    protected static final int DIRECTION_RTL = SwipeActionHelper.DIRECTION_RTL;

    protected static final int FLING_SPEED      = 1200;
    protected static final int ANIMATE_DURATION = 200;

    protected static final int SNAP_LEFT  = 0;
    protected static final int SNAP_RIGHT = 1;

    protected static final String ANIM_SLIDE    = "slide";
    protected static final String ANIM_SCROLL   = "scroll";
    protected static final String ANIM_RECOVER  = "recover";

    protected int direction;
    protected float initialDistance;
    protected float touchOffset;
    protected float scrollOffset;

    protected Integer snapAction;

    protected View swipeView;
    protected SwipeHolder parent;

    ArrayList<AnimationHelper> animationList;

    protected SwipeRunner(int direction) {
        this.direction = direction;
        this.snapAction = null;
        this.animationList = new ArrayList<>();
    }

    boolean isSwiped() {
        boolean result = (animationList.size() > 0);

        return result;
    }

    protected void set(SwipeHolder parent, View swipeView) {
        this.parent = parent;
        this.swipeView = swipeView;
    }

    public SwipeRunner accept(float distance) {
        int dir = (distance > 0)? DIRECTION_LTR: DIRECTION_RTL;
        if (dir == direction) {
            return this;
        }

        return null;
    }

    public abstract void add(View view);

    public void reset() {
        this.initialDistance = 0;
        this.touchOffset = 0;
        this.scrollOffset = 0;

        this.snapAction = null;

        this.clearAnimation();
    }

    public void clear() {
        this.initialDistance = 0;
        this.touchOffset = 0;
        this.scrollOffset = 0;

        this.snapAction = null;

        this.clearAnimation();

        if (swipeView.getTranslationX() != 0) {

            float start = swipeView.getTranslationX();
            float end = 0;

            AnimationHelper anim = new AnimationHelper(ANIM_RECOVER, start, end);
            this.addAnimation(anim);
        }
    }

    public void onBegin() {
        this.initialDistance = swipeView.getTranslationX();
        this.touchOffset = 0;
        this.scrollOffset = 0;

        this.snapAction = null;

        this.clearAnimation();
    }

    public void onMove(float deltaX) {
        this.touchOffset = deltaX;
    }

    public void onEnd(float velocityX) {
        this.initialDistance = 0;
        this.touchOffset = 0;
        this.scrollOffset = 0;

        if (snapAction != null) {
            parent.notifyActionBegin(this, snapAction);
        }
    }

    public void onDraw(Canvas canvas) {

    }

    public void onDrawOver(Canvas canvas) {
        int size = animationList.size();
        if (size > 0) {
            this.getHelper().invalidate();

            for (int i = size - 1; i >= 0; i--) {
                AnimationHelper anim = animationList.get(i);
                if (anim.isEnded) {
                    animationList.remove(i);
                }
            }

            size = animationList.size();
            if (size == 0 && swipeView.getTranslationX() == 0) {

                if (snapAction != null) {
                    int action = snapAction;
                    snapAction = null;

                    parent.notifyActionEnd(this, action);
                }

                parent.clear(this);

            }
        }
    }

    /**
     *
     * @return
     */
    float getTouchX() {
        float value = this.initialDistance;
        value += this.touchOffset;

        return value;
    }

    /**
     *
     * @return
     */
    protected float getTranslationX() {
        float value = this.getTouchX();

        float max = this.getMaxDistance();
        if (this.direction == DIRECTION_LTR) {

            if (value < 0) {
                value = value / getFriction(value, max);
            } else if (value > 0) {
                if (value > max) {
                    float offset = (value - max);
                    value = max + offset / getFriction(offset, max);
                }
            }

        } else if (this.direction == DIRECTION_RTL) {
            if (value < 0) {
                if (value < -max) {
                    float offset = (value + max);
                    value = -max + offset / getFriction(offset, max);
                }
            } else if (value > 0) {
                value = value / getFriction(value, max);
            }
        }

        return value;
    }

    /**
     *
     * @return
     */
    protected float getFinalX() {
        float value = this.getTranslationX();

        if (this.direction == DIRECTION_LTR) {
            value += scrollOffset;
        } else if (this.direction == DIRECTION_RTL) {
            value -= scrollOffset;
        }

        float max = swipeView.getWidth();
        value = (value > max)? max: value;
        value = (value < -max)? -max: value;

        return value;
    }

    /**
     *
     * @return
     */
    protected float getMaxDistance() {
        float f = 0.618f;
        f = f * swipeView.getWidth();
        return f;
    }

    protected SwipeActionHelper getHelper() {
        return parent.helper;
    }

    protected boolean isFling(float velocity) {
        return Math.abs(velocity) >= FLING_SPEED;
    }

    protected float getFriction(float offset, float max) {
        float width = Math.abs(max);
        width = (width == 0)? swipeView.getWidth(): width;

        float f = Math.abs(offset) / width;
        f = 2 + f * 7;

        return f;
    }

    protected void removeAnimation(String tag) {
        AnimationHelper anim = this.getAnimation(tag);
        if (anim == null) {
            return;
        }

        anim.cancel();
        animationList.remove(anim);
    }

    protected AnimationHelper addAnimation(AnimationHelper anim) {
        this.getHelper().invalidate();

        animationList.add(anim);
        anim.start();

        return anim;
    }

    protected AnimationHelper getAnimation(String tag) {
        for (AnimationHelper anim : animationList) {
            if (anim.tag.equals(tag)) {
                return anim;
            }
        }

        return null;
    }

    protected void clearAnimation() {
        for (int i = animationList.size() - 1; i >= 0; i--) {
            AnimationHelper anim = animationList.get(i);
            anim.cancel();
            animationList.remove(i);
        }
    }

    /**
     *
     */
    protected static class AnimationHelper implements ValueAnimator.AnimatorListener, ValueAnimator.AnimatorUpdateListener{

        String tag;

        float startX;
        float targetX;

        float fraction;

        boolean isEnded = false;
        ValueAnimator animator;

        public AnimationHelper(String tag, float startX, float targetX) {
            this.tag = (tag == null)? "": tag;

            this.startX = startX;
            this.targetX = targetX;

            this.animator = ValueAnimator.ofFloat(0.f, 1.f);
            animator.setDuration(ANIMATE_DURATION);
            animator.addListener(this);
            animator.addUpdateListener(this);

            this.fraction = 0;
        }

        public float getValue() {
            return startX + fraction * (targetX - startX);
        }

        public float getFraction() {
            return this.fraction;
        }

        public float getStart() {
            return this.startX;
        }

        void start() {
            animator.start();
        }

        void cancel() {
            animator.cancel();
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            this.isEnded = true;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            this.isEnded = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            this.fraction = animation.getAnimatedFraction();
        }
    }
}

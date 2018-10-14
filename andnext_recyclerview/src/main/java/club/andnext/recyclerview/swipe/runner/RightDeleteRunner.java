package club.andnext.recyclerview.swipe.runner;

import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import club.andnext.recyclerview.swipe.SwipeActionHelper;
import club.andnext.recyclerview.swipe.SwipeRunner;

public class RightDeleteRunner extends SwipeRunner {

    ArrayList<ViewGroup> actionList;

    int snapState;

    public RightDeleteRunner() {
        super(DIRECTION_RTL);
    }

    @Override
    public SwipeRunner accept(float distance) {
        if (actionList == null || actionList.isEmpty()) {
            return null;
        }

        return super.accept(distance);
    }

    @Override
    public void add(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;
            if (group.getChildCount() > 0) {

                if (actionList == null) {
                    actionList = new ArrayList<>();
                }

                actionList.add(group);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();

        this.snapState = SNAP_RIGHT;

        for (View v : actionList) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void onBegin() {
        super.onBegin();

        {
            this.snapState = SNAP_RIGHT;
        }

        for (View v : actionList) {
            v.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMove(float deltaX) {
        super.onMove(deltaX);

        float tx = this.getTranslationX();
        float max = this.getMaxDistance();

        // scroll offset
        if (tx <= -max) {
            if (scrollOffset == 0 && getAnimation(ANIM_SCROLL) == null) {
                float start = scrollOffset;
                float target = swipeView.getWidth() * 0.956f - Math.abs(tx);
                if (target > start) {
                    AnimationHelper anim = new AnimationHelper(ANIM_SCROLL, start, target);
                    this.addAnimation(anim);
                }
            }
        }

        // child
        {
            int state = (tx <= -max) ? SNAP_LEFT : SNAP_RIGHT;
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

        Integer action = null;

        float x = this.getFinalX();
        float tx = this.getTranslationX();
        float max = this.getMaxDistance();

        boolean showAction = false;

        {
            this.clearAnimation();
        }

        while (true) {
            if (x == 0) {
                break;
            }

            if (x > 0) {
                float start = x;
                float end = 0;

                AnimationHelper anim = new AnimationHelper(ANIM_RECOVER, start, end);
                this.addAnimation(anim);

                break;
            }

            if (tx <= -max) {
                action = SwipeActionHelper.ACTION_RIGHT;

                float start = x;
                float end = 0;

                AnimationHelper anim = new AnimationHelper(ANIM_RECOVER, start, end);
                this.addAnimation(anim);

                break;
            }

            boolean fling = this.isFling(velocityX);
            if (fling) {
                showAction = true;

                if (velocityX > 0) {
                    showAction = false;
                }

            } else {

                if (tx < -getWidth() / 2) {
                    showAction = true;
                }

            }

            {
                float start = x;
                float end = (showAction) ? (-getWidth()) : 0;

                AnimationHelper anim = new AnimationHelper(ANIM_RECOVER, start, end);
                this.addAnimation(anim);

                break;
            }
        }

        {
            super.onEnd(velocityX);
        }

        if (action != null) {
            parent.notifyActionBegin(this, action);
            parent.notifyActionEnd(this, action);
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
            float width = this.getWidth();
            for (int i = 0, size = actionList.size() - 1; i < size; i++) {
                View actionView = actionList.get(i);

                float tx = x;
                tx += swipeView.getWidth();

                float offset = this.getOffset(i);
                offset = -x * (offset / width);
                tx += offset;

                actionView.setTranslationX(tx);
            }
        }

        // child
        {
            float tx = x;
            tx = getSlideX(tx, this.snapState);

            AnimationHelper anim = getAnimation(ANIM_SLIDE);
            if (anim != null) {
                float fraction = anim.getFraction();

                if (this.snapState == SNAP_RIGHT) {

                    tx = this.getSlideX(x, SNAP_RIGHT);
                    float offset = (tx - (x + swipeView.getWidth()));
                    offset = offset * fraction;

                    tx = x + swipeView.getWidth();
                    tx += offset;

                } else if (snapState == SNAP_LEFT) {

                    tx = getSlideX(x, SNAP_RIGHT);
                    float offset = (tx - (x + swipeView.getWidth()));
                    offset = offset * fraction;
                    tx -= offset;
                }
            }

            tx = (tx < 0)? 0: tx;
            View slideView = actionList.get(actionList.size() - 1);
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

        if ((anim = getAnimation(ANIM_SCROLL)) != null) {
            this.scrollOffset = anim.getValue();
        }

        return super.getFinalX();
    }

    @Override
    protected float getMaxDistance() {
        float f = 0.8f;
        float max = f * swipeView.getWidth();

        float width = this.getWidth();
        max = (max < width)? width * 1.2f: max;

        return max;
    }

    float getSlideX(float x, int state) {

        if (state == SNAP_LEFT) {

            x = (x + swipeView.getWidth());

        } else {
            float width = this.getWidth();

            float tx = x;
            tx += swipeView.getWidth();

            float offset = this.getOffset(actionList.size() - 1);
            offset = -x * (offset / width);

            tx += offset;

            x = tx;
        }

        return x;
    }

    float getWidth() {
        return getOffset(actionList.size());
    }

    float getOffset(int position) {
        int offset = 0;

        int size = position;
        size = (size > actionList.size())? actionList.size(): size;
        for (int i = 0; i < size; i++) {
            offset += actionList.get(i).getChildAt(0).getWidth();
        }

        return offset;
    }
}

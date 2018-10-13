package club.andnext.recyclerview.swipe.runner;

import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import club.andnext.recyclerview.swipe.SwipeRunner;

public class RightActionRunner extends SwipeRunner {

    ArrayList<ViewGroup> actionList;

    public RightActionRunner() {
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

        for (View v : actionList) {
            v.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMove(float deltaX) {
        super.onMove(deltaX);
    }

    @Override
    public void onEnd(float velocityX) {

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

            boolean fling = this.isFling(velocityX);
            if (fling) {
                showAction = true;

                if (velocityX > 0) {
                    showAction = false;
                }

            } else {

                if (tx < -max / 2) {
                    showAction = true;
                }

            }

            {
                float start = x;
                float end = (showAction) ? (-max) : 0;

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
            float max = this.getMaxDistance();
            for (int i = 0, size = actionList.size(); i < size; i++) {
                View actionView = actionList.get(i);

                float tx = x;
                tx += swipeView.getWidth();

                float offset = this.getOffset(i);
                offset = -x * (offset / max);
                tx += offset;

                actionView.setTranslationX(tx);
            }

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
        float max = getOffset(actionList.size());

        return max;
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

package club.andnext.overscroll;

import android.widget.HorizontalScrollView;
import me.everything.android.ui.overscroll.*;
import me.everything.android.ui.overscroll.adapters.HorizontalScrollViewOverScrollDecorAdapter;
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;

import java.util.ArrayList;

/**
 *
 */
public class HorizontalOverScrollHelper extends HorizontalOverScrollBounceEffectDecorator implements IOverScrollStateListener, IOverScrollUpdateListener {

    ArrayList<IOverScrollStateListener> stateListeners;
    ArrayList<IOverScrollUpdateListener> updateListeners;

    HorizontalOverScrollHelper(IOverScrollDecoratorAdapter viewAdapter) {
        super(viewAdapter);

        this.mStateListener = this;
        this.mUpdateListener = this;
    }

    @Override
    public void setOverScrollStateListener(IOverScrollStateListener listener) {
        this.addOverScrollStateListener(listener);
    }

    @Override
    public void setOverScrollUpdateListener(IOverScrollUpdateListener listener) {
        this.addOverScrollUpdateListener(listener);
    }

    public void addOverScrollStateListener(IOverScrollStateListener listener) {
        if (listener == null) {
            return;
        }

        if (stateListeners == null) {
            stateListeners = new ArrayList<>();
        }

        stateListeners.add(listener);
    }

    public void addOverScrollUpdateListener(IOverScrollUpdateListener listener) {
        if (listener == null) {
            return;
        }

        if (updateListeners == null) {
            updateListeners = new ArrayList<>();
        }

        updateListeners.add(listener);
    }

    @Override
    public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
        if (stateListeners != null) {
            for (IOverScrollStateListener listener: stateListeners) {
                listener.onOverScrollStateChange(decor, oldState, newState);
            }
        }
    }

    @Override
    public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {
        if (updateListeners != null) {
            for (IOverScrollUpdateListener listener: updateListeners) {
                listener.onOverScrollUpdate(decor, state, offset);
            }
        }
    }

    public static final HorizontalOverScrollHelper attach(HorizontalScrollView scrollView) {
        HorizontalScrollViewOverScrollDecorAdapter adapter = new HorizontalScrollViewOverScrollDecorAdapter(scrollView);
        HorizontalOverScrollHelper helper = new HorizontalOverScrollHelper(adapter);
        helper.attach();

        return helper;
    }
}

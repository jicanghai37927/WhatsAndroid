package club.andnext.recyclerview.overscroll;

import android.support.v7.widget.RecyclerView;
import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollStateListener;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter;

import java.util.ArrayList;

public class OverScrollHelper extends VerticalOverScrollBounceEffectDecorator implements IOverScrollStateListener, IOverScrollUpdateListener{

    ArrayList<IOverScrollStateListener> stateListeners;
    ArrayList<IOverScrollUpdateListener> updateListeners;

    public static OverScrollHelper attach(RecyclerView recyclerView) {
        RecyclerViewOverScrollDecorAdapter adapter = new RecyclerViewOverScrollDecorAdapter(recyclerView);
        OverScrollHelper helper = new OverScrollHelper(adapter);
        return helper;
    }

    public OverScrollHelper(IOverScrollDecoratorAdapter viewAdapter) {
        super(viewAdapter);

        super.setOverScrollStateListener(this);
        super.setOverScrollUpdateListener(this);
    }

    @Override
    public void attach() {
        super.attach();

    }

    @Override
    public void detach() {
        super.detach();
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
}

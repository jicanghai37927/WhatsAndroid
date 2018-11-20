package club.andnext.recyclerview.helper;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */
public class SoftInputTouchHelper implements RecyclerView.OnItemTouchListener {

    public static final int FLAG_SCROLL = 0x01;
    public static final int FLAG_BOTTOM = 0x01 << 2;

    boolean enable;

    int hideFlags = FLAG_BOTTOM;

    RecyclerView recyclerView;

    public SoftInputTouchHelper() {
        this.enable = true;
    }

    public void attach(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.addOnItemTouchListener(this);
    }

    public void detach() {
        recyclerView.removeOnItemTouchListener(this);
    }

    public boolean isEnable() {
        return enable;
    }

    public SoftInputTouchHelper setEnable(boolean value) {
        this.enable = value;

        return this;
    }

    public SoftInputTouchHelper setFlags(int hideFlags) {
        this.hideFlags = hideFlags;

        return this;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        if (!enable) {
            return false;
        }

        int action = e.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {

                break;
            }

            case MotionEvent.ACTION_MOVE: {

                int state = rv.getScrollState();
                boolean hide = false;
                while (true) {
                    if ((hideFlags & FLAG_SCROLL) != 0) {
                        hide = (state != RecyclerView.SCROLL_STATE_IDLE);
                        if (hide) {
                            break;
                        }
                    }

                    if ((hideFlags & FLAG_BOTTOM) != 0) {
                        hide = (e.getY() > rv.getBottom());
                        if (hide) {
                            break;
                        }
                    }

                    break;
                }

                if (hide) {
                    SoftInputTouchHelper.hideSoftInput(rv.getContext());
                }

                break;
            }
        }

        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        if (!enable) {
            return;
        }

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (!enable) {
            return;
        }
    }

    /**
     *
     * @param context
     */
    public static final void hideSoftInput(Context context) {
        if (!(context instanceof Activity)) {
            return;
        }

        View view = ((Activity)context).getCurrentFocus();

        if (view == null) {
            return;
        }

        InputMethodManager manager = (InputMethodManager)(context.getSystemService(Context.INPUT_METHOD_SERVICE));
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

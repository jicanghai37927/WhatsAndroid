package club.andnext.recyclerview.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */
public class EditTouchHelper implements RecyclerView.OnItemTouchListener {

    public static final int FLAG_SCROLL = 0x01;
    public static final int FLAG_BOTTOM = 0x01 << 2;

    boolean enable;

    TextView target;
    Rect rect;
    int hideFlags = FLAG_BOTTOM;

    RecyclerView recyclerView;

    public EditTouchHelper() {
        this.enable = true;

        this.rect = new Rect();
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

    public EditTouchHelper setEnable(boolean value) {
        this.enable = value;

        if (!enable) {
            target = null;
        }

        return this;
    }

    public EditTouchHelper setFlags(int hideFlags) {
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
                this.target = findTarget(rv, e);
                if (target != null) {
                    rect = getRect(rv, target);
                }

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

                if (target != null) {
                    if (hide || (state != RecyclerView.SCROLL_STATE_IDLE)) {
                        MotionEvent event = this.obtain(e);
                        event.setAction(MotionEvent.ACTION_CANCEL);

                        target.dispatchTouchEvent(event);
                        event.recycle();

                        target = null;
                    }
                }

                if (hide) {
                    EditTouchHelper.hideSoftInput(rv.getContext());
                }

                break;
            }
        }

        if (target != null) {
            MotionEvent event = this.obtain(e);
            target.dispatchTouchEvent(event);
            event.recycle();
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                target = null;
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

    TextView findTarget(RecyclerView rv, MotionEvent e) {
        float x = rv.getWidth() / 2;
        float y = e.getY();

        View view = rv.findChildViewUnder(x, y);
        if (view == null) {
            while (rv.getChildCount() != 0) {

                // try last one
                {
                    view = rv.getChildAt(rv.getChildCount() - 1);
                    rect = getRect(rv, view);
                    if (y > rect.bottom) {
                        break;
                    }
                }

                // try first one
                {
                    view = rv.getChildAt(0);
                    rect = getRect(rv, view);
                    if (y < rect.top) {
                        break;
                    }
                }

                // remain null
                {
                    view = null;
                    break;
                }
            }
        }

        if (view == null) {
            return null;
        }

        TextView child = findTextView(view);
        if (child != null) {
            rect.set(0, 0, child.getWidth(), child.getHeight());
            rv.offsetDescendantRectToMyCoords(child, rect);

            if (rect.contains((int)(e.getX()), (int)(e.getY()))) {
                child = null;
            }
        }

        return child;
    }

    protected TextView findTextView(View child) {
        if (child.getVisibility() != View.VISIBLE) {
            return null;
        }

        if (!child.isEnabled()) {
            return null;
        }

        if (child instanceof EditText) {
            return (EditText)child;
        }

        if (child instanceof TextView) {
            TextView view = (TextView)child;
            if (view.onCheckIsTextEditor()) {
                return view;
            }

            if (view.isTextSelectable()) {
                return view;
            }

            return null;
        }

        if (child instanceof ViewGroup) {
            ViewGroup layout = (ViewGroup)child;
            for (int i = 0, size = layout.getChildCount(); i < size; i++) {
                TextView view = findTextView(layout.getChildAt(i));
                if (view != null) {
                    return view;
                }
            }
        }

        return null;
    }

    MotionEvent obtain(MotionEvent e) {
        MotionEvent event = MotionEvent.obtain(e);
        float x = e.getX();
        if (x < rect.left) {
            x = 0;
        } else if (x > rect.right) {
            x = target.getWidth();
        } else {
            x = (x - rect.left);
        }

        float y = e.getY();
        if (y < rect.top) {
            y = 0;
        } else if (y > rect.bottom) {
            y = target.getHeight();
        } else {
            y = (y - rect.top);
        }

        event.setLocation(x, y);

        return event;
    }

    Rect getRect(RecyclerView rv, View view) {

        rect.set(0, 0, view.getWidth(), view.getHeight());
        rv.offsetDescendantRectToMyCoords(view, rect);

        return rect;
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


package club.andnext.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 软键盘工具类
 *
 */
public class SoftInputUtils {

    /**
     *
     * @param context
     */
    public static final void show(final Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            show(context, view);
        }
    }

    /**
     * 显示软键盘
     * 
     * @param context
     * @param view
     */
    public static final void show(final Context context, final EditText view) {
        if (view == null) {
            return;
        }

        view.requestFocus();
        
        view.post(new Runnable() {
			@Override
			public void run() {
				InputMethodManager manager = (InputMethodManager)(context.getSystemService(Context.INPUT_METHOD_SERVICE));
				manager.showSoftInput(view, 0);
			}
		});
    }

    /**
     *
     * @param context
     * @param view
     */
    public static final void show(final Context context, final View view) {
        if (view == null) {
            return;
        }

        view.requestFocus();

        view.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager)(context.getSystemService(Context.INPUT_METHOD_SERVICE));
                manager.showSoftInput(view, 0);
            }
        });
    }

    /**
     *
     * @param context
     */
    public static final void hide(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            hide(context, view);
        }
    }

    /**
     * 隐藏软键盘
     * 
     * @param context
     * @param view
     */
    public static final void hide(Context context, EditText view) {
        if (view == null) {
            return;
        }

        InputMethodManager manager = (InputMethodManager)(context.getSystemService(Context.INPUT_METHOD_SERVICE));
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     *
     * @param context
     * @param view
     */
    public static final void hide(Context context, View view) {
        if (view == null) {
            return;
        }

        if (view instanceof EditText) {
            hide(context, (EditText)view);
            return;
        }

        InputMethodManager manager = (InputMethodManager)(context.getSystemService(Context.INPUT_METHOD_SERVICE));
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

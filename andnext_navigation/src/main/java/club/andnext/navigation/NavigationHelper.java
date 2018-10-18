package club.andnext.navigation;

import android.app.Activity;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;

public class NavigationHelper {

    static NavigationCallbacks navigationCallbacks;

    public static final boolean setContentView(Activity context, int layoutResID) {

        context.setContentView(layoutResID);

        return attach(context);
    }

    public static final boolean setContentView(Activity context, View view) {

        context.setContentView(view);

        return attach(context);
    }

    public static final boolean setContentView(Activity context, View view, ViewGroup.LayoutParams params) {

        context.setContentView(view, params);

        return attach(context);
    }

    static final boolean attach(Activity context) {

        NavigationLayout layout = null;

        {
            View decorView = context.getWindow().getDecorView();
            if (decorView instanceof ViewGroup) {
                ViewGroup decorGroup = (ViewGroup) decorView;

                if (decorGroup.getChildCount() == 1) {
                    layout = new NavigationLayout(context);
                    layout.setOnNavigationListener(new NavigationListener(context));

                    {
                        View child = decorGroup.getChildAt(0);
                        decorGroup.removeAllViews();

                        ViewGroup target = layout.getTargetView();
                        target.addView(child,
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    }

                    decorGroup.addView(layout);
                }

            }
        }

        if (layout != null) {

            if (navigationCallbacks == null) {
                navigationCallbacks = new NavigationCallbacks();
                navigationCallbacks.onActivityCreated(context, null);

                context.getApplication().registerActivityLifecycleCallbacks(navigationCallbacks);
            }
        }

        return (layout != null);
    }

    /**
     *
     */
    private static class NavigationListener implements NavigationLayout.OnNavigationListener {

        Activity context;

        NavigationListener(Activity context) {
            this.context = context;
        }

        @Override
        public void onComplete(NavigationLayout view) {
            context.finish();
            context.overridePendingTransition(R.anim.anc_navigation_standby, R.anim.anc_navigation_standby);
        }
    }
}

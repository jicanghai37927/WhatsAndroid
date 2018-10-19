package club.andnext.navigation;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import java.util.ArrayList;

public class NavigationHelper implements Application.ActivityLifecycleCallbacks {

    private static NavigationHelper sInstance = null;

    ArrayList<Activity> createdList;

    ArrayList<String> activityList;
    ArrayList<String> excludeList;

    /**
     * Call from Activity#onCreate(), and before call super.onCreate().
     *
     * @param context
     */
    public static final void onCreate(@NonNull Activity context) {
        if (sInstance == null) {
            onCreate(context.getApplication());
        }
    }

    /**
     * Call from Application#onCreate().
     *
     * @param application
     */
    public static final void onCreate(@NonNull Application application) {
        if (sInstance == null) {

            sInstance = new NavigationHelper();
            application.registerActivityLifecycleCallbacks(sInstance);

        }
    }

    /**
     * Call from Activity#onCreate(), and before call super.onCreate().
     *
     * @param context
     */
    public static final void exclude(@NonNull Activity context) {
        if (sInstance != null) {
            String hash = getHash(context);
            sInstance.excludeList.add(hash);
        }
    }

    private NavigationHelper() {
        this.createdList = new ArrayList<>();
        this.activityList = new ArrayList<>();
        this.excludeList = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        String hash = getHash(activity);

        int index = excludeList.indexOf(hash);
        if (index < 0) {

            this.createdList.add(activity);

            if (!isAttached(activity)) {
                attach(activity);
            }
        }

        {
            this.excludeList.remove(hash);
        }

        {
            this.activityList.add(hash);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (this.createdList.isEmpty()) {
            return;
        }

        Activity createdActivity = createdList.remove(createdList.size() - 1);
        int stoppedIndex = activityList.indexOf(getHash(activity));
        int createdIndex = activityList.indexOf(getHash(createdActivity));

        if (stoppedIndex < createdIndex) {
            NavigationLayout layout = this.getNavigationLayout(createdActivity);

            if (layout != null && TextUtils.isEmpty(layout.getActivityHash())) {

                if (activity.isFinishing()) { // 即将被destroy

                    NavigationLayout prev = this.getNavigationLayout(activity);
                    if (prev != null) {
                        Drawable d = prev.getPrevious();
                        layout.setPrevious(prev.getActivityHash(), d);
                    }

                } else {

                    Bitmap bitmap = getDecorBitmap(activity);
                    layout.setPrevious(getHash(activity), bitmap);

                }
            }
        }

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        {
            int index = activityList.indexOf(getHash(activity));
            if (index >= 0) {
                activityList.remove(index);
            }

            if (activityList.isEmpty()) {
                activity.getApplication().unregisterActivityLifecycleCallbacks(NavigationHelper.sInstance);
                NavigationHelper.sInstance = null;
            }
        }

    }

    boolean isAttached(@NonNull Activity context) {

        NavigationLayout layout = this.getNavigationLayout(context);
        return (layout != null);

    }

    void detach(@NonNull Activity context) {

        NavigationLayout layout = this.getNavigationLayout(context);
        if (layout == null) {
            return;
        }

        View child = layout.getNext();
        if (child == null) {
            return;
        }

        {
            ViewGroup parent = (ViewGroup)(child.getParent());

            parent.removeAllViews();
        }

        {
            ViewGroup parent = (ViewGroup) (layout.getParent());

            int index = parent.indexOfChild(layout);
            parent.removeViewAt(index);
            parent.addView(child, index);
        }

    }

    boolean attach(@NonNull Activity context) {

        NavigationLayout layout = this.getNavigationLayout(context);
        if (layout != null) {
            return true;
        }

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

                        layout.setNext(child);
                    }

                    decorGroup.addView(layout);
                }

            }
        }

        return (layout != null);
    }


    Bitmap getDecorBitmap(Activity activity) {
        View view = activity.getWindow().getDecorView();

        int width = view.getWidth();
        int height = view.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    NavigationLayout getNavigationLayout(Activity activity) {

        View decorView = activity.getWindow().getDecorView();

        if (decorView instanceof ViewGroup) {
            ViewGroup decorGroup = (ViewGroup) decorView;

            int count = decorGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = ((ViewGroup) decorView).getChildAt(i);
                if (view instanceof NavigationLayout) {
                    return (NavigationLayout)view;
                }
            }
        }

        return null;
    }

    static String getHash(Activity context) {
        return String.valueOf(context.hashCode());
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

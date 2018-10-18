package club.andnext.navigation;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class NavigationCallbacks implements Application.ActivityLifecycleCallbacks {

    ArrayList<Activity> createdList;

    ArrayList<Integer> activityList;

    NavigationCallbacks() {
        this.createdList = new ArrayList<>();
        this.activityList = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        {
            this.createdList.add(activity);
        }

        {
            this.activityList.add(activity.hashCode());
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
        int stoppedIndex = activityList.indexOf(activity.hashCode());
        int createdIndex = activityList.indexOf(createdActivity.hashCode());

        if (stoppedIndex < createdIndex) {
            NavigationLayout layout = this.getNavigationLayout(createdActivity);

            if (layout != null && layout.getActivityHash() == 0) {

                if (activity.isFinishing()) { // 即将被destroy

                    NavigationLayout prev = this.getNavigationLayout(activity);
                    if (prev != null) {
                        Drawable d = prev.getBitmap();
                        layout.setBitmap(prev.getActivityHash(), d);
                    }

                } else {

                    Bitmap bitmap = getDecorBitmap(activity);
                    layout.setBitmap(activity.hashCode(), bitmap);

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
            int index = activityList.indexOf(activity.hashCode());
            if (index >= 0) {
                activityList.remove(index);
            }

            if (activityList.isEmpty()) {
                activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                NavigationHelper.navigationCallbacks = null;
            }
        }

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
}

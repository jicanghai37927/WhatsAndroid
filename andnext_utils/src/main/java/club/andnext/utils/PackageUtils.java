package club.andnext.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 *
 */
public class PackageUtils {

    public static final boolean open(Context context, Uri uri) {
        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(launchFlags);

        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {

        }

        return false;
    }
}

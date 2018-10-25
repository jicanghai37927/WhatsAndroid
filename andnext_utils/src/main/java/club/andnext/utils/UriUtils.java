package club.andnext.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import androidx.core.content.FileProvider;

import java.io.File;

public class UriUtils {

    private static String sAuthority = null;

    public static final Uri fromFile(Context context, File file) {
        Uri data;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = getAuthority(context);
            data = FileProvider.getUriForFile(context, authority, file);
        } else {
            data = Uri.fromFile(file);
        }

        return data;
    }

    public static final String getAuthority(Context context) {
        if (!TextUtils.isEmpty(sAuthority)) {
            return sAuthority;
        }

        String name = context.getPackageName();
        name += ".fileprovider";
        return name;
    }

    public static final void setAuthority(String authority) {
        sAuthority = authority;
    }
}

package club.andnext.utils;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.List;

/**
 *
 */
public class PackageUtils {

    public static final boolean openBrowser(Context context, Uri uri) {
        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, null);

        intent.setFlags(launchFlags);

        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {

        }

        return false;
    }


    public static final boolean checkSupport(Context context, ResolveInfo resolveInfo, Intent intent) {
        List<ResolveInfo> list = queryActivities(context, intent);
        if (list == null || list.isEmpty()) {
            return false;
        }

        for (ResolveInfo entry : list) {
            if (entry.activityInfo.packageName.equalsIgnoreCase(resolveInfo.activityInfo.packageName)
                    && entry.activityInfo.name.equalsIgnoreCase(resolveInfo.activityInfo.name)) {
                return true;
            }
        }

        return false;
    }

    public static final List<ResolveInfo> queryActivities(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);

        boolean print = false;
        if (print) {
            for (ResolveInfo entry : list) {
                Log.w("AA", entry.activityInfo.name);
            }
        }

        return list;
    }

    public static Drawable getIcon(@NonNull Context context, @NonNull String pkgName) {

        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
            if (info != null) {
                return info.loadIcon(pm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean exist(@NonNull Context context, @NonNull String pkgName) {
        PackageManager pm = context.getPackageManager();

        try {
            ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
            return (info != null);

        } catch (PackageManager.NameNotFoundException e) {

        }

        return false;
    }

    public static boolean launch(@NonNull Context context, @NonNull String pkgName) {

        try{
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
            if (intent == null) {
                return false;
            }

            context.startActivity(intent);
            return true;

        }catch(Exception e){

        }

        return false;
    }

    public static boolean start(@NonNull Context context, @NonNull String pkgName) {

        boolean result = false;

        try {
            PackageManager pm = context.getPackageManager();

            PackageInfo pi = pm.getPackageInfo(pkgName, 0);
            if (pi == null) {
                return result;
            }

            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);

            List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
            if (apps == null || apps.isEmpty()) {
                return result;
            }

            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                result = start(context, ri.activityInfo.packageName, ri.activityInfo.name);
            }

        } catch (Exception e) {

        }

        return result;
    }

    public static boolean start(@NonNull Context context, @NonNull String pkgName, @NonNull String className) {
        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(pkgName, className));
        intent.setFlags(launchFlags);

        try {
            context.startActivity(intent);

            return true;
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean canExecute(Context context, String action, Uri uri) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(action, uri);
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);

        return (list != null && !list.isEmpty());
    }

    public static boolean executeAction(Context context, String action, Uri uri) {
        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

        Intent intent = new Intent(action, uri);
        intent.setFlags(launchFlags);

        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean showDetailsSettings(Context context) {
        return showDetailsSettings(context, null);
    }

    public static boolean showDetailsSettings(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            pkgName = context.getPackageName();
        }

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", pkgName, null));

        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean canRead(Context context) {

        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        return (permission == PackageManager.PERMISSION_GRANTED);
    }

    public static ResolveInfo resolve(Context context, String packageName, String activityName) {

        Intent intent = new Intent();
        intent.setClassName(packageName, activityName);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }

        return null;
    }


    /**
     * 获取本地软件版本号名称
     */
    public static String getVersionName(Context ctx) {
        String localVersion = "";

        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);

            localVersion = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return localVersion;
    }

    /**
     *
     * @param ctx
     * @return
     */
    public static int getVersionCode(Context ctx) {
        int localVersion = 0;

        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);

            localVersion = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return localVersion;
    }

    public static boolean showMarket(Context context) {
        return showMarket(context, null, context.getPackageName());
    }

    public static boolean showMarket(Context context, String marketPkg) {
        return showMarket(context, marketPkg, context.getPackageName());
    }

    public static boolean showMarket(Context context, String marketPkg, String appPkg) {
        if (TextUtils.isEmpty(appPkg)) {
            return false;
        }

        try {
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static final boolean install(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        String mimeType = "application/vnd.android.package-archive";

        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {

            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = UriUtils.fromFile(context, file);

            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, mimeType);

        } else {
            intent.setDataAndType(Uri.fromFile(file), mimeType);
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static final void restart(Context context, Class<?> clz) {

        Intent intent = new Intent(context, clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

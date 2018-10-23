package club.andnext.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

/**
 *
 */
public class SimpleWebView extends WebView {

    private static final String TAG = SimpleWebView.class.getSimpleName();

    public SimpleWebView(Context context) {
        this(context, null);
    }

    public SimpleWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SimpleWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.init(context);
    }

    void init(Context context) {

        {
            this.setBackgroundColor(Color.TRANSPARENT);

            // be careful, we do not need internet access
            this.getSettings().setBlockNetworkLoads(false);

            //
            this.getSettings().setLoadWithOverviewMode(true);
            this.getSettings().setJavaScriptEnabled(true);
//            this.getSettings().setUseWideViewPort(true);
        }

        if (false) {
            // caching
            File dir = context.getCacheDir();
            if (!dir.exists()) {
                Log.d(TAG, "directory does not exist");
                boolean mkdirsStatus = dir.mkdirs();
                if (!mkdirsStatus) {
                    Log.e(TAG, "directory creation failed");
                }
            }

            getSettings().setAppCachePath(dir.getPath());
            getSettings().setAppCacheEnabled(true);
            getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        }

        {
            this.setWebViewClient(new SimpleWebViewClient());
        }
    }

    static final boolean open(Context context, Uri uri) {
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

    /**
     *
     */
    static class SimpleWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            String scheme = uri.getScheme();
            if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                open(view.getContext(), uri);

                return true;
            }

            return super.shouldOverrideUrlLoading(view, request);
        }

    }
}

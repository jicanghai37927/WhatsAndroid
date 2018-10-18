package club.andnext.marked;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class MarkedWebView extends WebView {

    private static final String TAG = MarkedWebView.class.getSimpleName();

    private static final String HTML_LOCATION = "file:///android_asset/marked/AndroidMarked.html";

    String mText;

    private boolean mWebViewLoaded = false;

    public MarkedWebView(Context context) {
        this(context, null);
    }

    public MarkedWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MarkedWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.init(context);
    }

    void init(Context context) {

        {
            this.setBackgroundColor(Color.TRANSPARENT);

            // be careful, we do not need internet access
            this.getSettings().setBlockNetworkLoads(true);

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
            this.mWebViewLoaded = false;
            this.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    if (mWebViewLoaded) {
                        // WebView was already finished
                        // do not load content again
                        return;
                    }

                    mWebViewLoaded = true;
                    if (!TextUtils.isEmpty(mText)) {
                        setText(mText);
                    }
                }
            });
        }

        {
            this.loadUrl(HTML_LOCATION);
        }
    }

    public void setText(String text) {
        this.mText = text;

        //wait for WebView to finish loading
        if (!mWebViewLoaded) {
            return;
        }

        String escapeText;
        if (text != null) {
            escapeText = escape(text);
        } else {
            escapeText = "";
        }

        String javascriptCommand = "javascript:setText(\'" + escapeText + "\')";
        this.loadUrl(javascriptCommand);
    }

    String escape(String s) {

        StringBuilder out = new StringBuilder(s.length() + 128);

        for (int i = 0, length = s.length(); i < length; i++) {
            char c = s.charAt(i);

            /*
             * From RFC 4627, "All Unicode characters may be placed within the
             * quotation marks except for the characters that must be escaped:
             * quotation mark, reverse solidus, and the control characters
             * (U+0000 through U+001F)."
             */
            switch (c) {
                case '"':
                case '\\':
                case '/':
                    out.append('\\').append(c);
                    break;

                case '\t':
                    out.append("\\t");
                    break;

                case '\b':
                    out.append("\\b");
                    break;

                case '\n':
                    out.append("\\n");
                    break;

                case '\r':
                    out.append("\\r");
                    break;

                case '\f':
                    out.append("\\f");
                    break;

                default:
                    if (c <= 0x1F) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                    break;
            }

        }

        return out.toString();
    }

}

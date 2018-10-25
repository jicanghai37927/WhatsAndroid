package club.andnext.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ClipboardUtils {

    public static final void setText(Context context, String text) {
        ClipboardManager myClipboard;
        myClipboard = (ClipboardManager)(context.getSystemService(CLIPBOARD_SERVICE));

        {
            ClipData myClip;
            myClip = ClipData.newPlainText("", text);
            myClipboard.setPrimaryClip(myClip);
        }
    }
}

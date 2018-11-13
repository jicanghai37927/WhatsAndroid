package club.andnext.utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class AlertDialogUtils {

    public static final AlertDialog showMessage(Context context, CharSequence msg) {
        AlertDialog dialog = showMessage(context, msg, null);
        return dialog;
    }

    public static final AlertDialog showMessage(Context context, CharSequence msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);

        builder.setPositiveButton(android.R.string.ok, listener);
        return builder.show();
    }
}

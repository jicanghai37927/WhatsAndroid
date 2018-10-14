package club.andnext.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class AlertDialogUtils {

    public static final AlertDialog showMessage(Context context, CharSequence msg) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        AlertDialog dialog = showMessage(context, msg, listener);
        return dialog;
    }

    public static final AlertDialog showMessage(Context context, CharSequence msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);

        builder.setPositiveButton(android.R.string.ok, listener);
        return builder.show();
    }
}

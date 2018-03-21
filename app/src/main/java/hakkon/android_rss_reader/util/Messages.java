package hakkon.android_rss_reader.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by hakkon on 21.03.18.
 */

public class Messages {
    public static void showError(Context context, String message, DialogInterface.OnDismissListener cb) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setOnDismissListener(cb);
        builder.create().show();
    }
}

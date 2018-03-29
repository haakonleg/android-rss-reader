package hakkon.android_rss_reader.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import hakkon.android_rss_reader.R;

/**
 * Created by hakkon on 21.03.18.
 */

public class Messages {
    public static void showError(Context context, String message, DialogInterface.OnDismissListener cb) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.dialog_error));
        builder.setMessage(message);
        builder.setOnDismissListener(cb);
        builder.create().show();
    }

    public static void showAreYouSure(Context context, String title, String message, DialogInterface.OnClickListener cb) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.dialog_yes), cb);
        builder.setNegativeButton(context.getString(R.string.dialog_cancel), cb);
        builder.create().show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

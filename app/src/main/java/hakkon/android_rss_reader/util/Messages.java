package hakkon.android_rss_reader.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

    public static void showInputDialog(Context context, String title, String message, InputDialogListener cb) {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edittext, null);

        TextView messageTxt = dialogView.findViewById(R.id.dialog_text);
        EditText inputTxt = dialogView.findViewById(R.id.dialog_edittext);

        messageTxt.setText(message);
        inputTxt.setText(title);
        inputTxt.setSelection(title.length());

        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setTitle(title);
        builder.setNegativeButton(context.getString(R.string.dialog_cancel), null);
        builder.setPositiveButton(context.getString(R.string.dialog_ok), (which, dialog) ->  {
            String text = inputTxt.getText().toString();
            if (!text.isEmpty())
                cb.onInputText(text);
        });
        builder.create().show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public interface InputDialogListener {
        void onInputText(String text);
    }
}

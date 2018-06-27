package com.projects.owner.camlocation.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static android.text.TextUtils.isEmpty;

public class Utils {

    private static Dialog mDialog = null;

    public static void showToast(Context mcontext, String msg)
    {
        Toast.makeText(mcontext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showDescDialog(Context context, String title, String desc) {
        try {
            if (mDialog != null && mDialog.isShowing()) {
                return;
            }
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(context);
            mAlertDialogBuilder.setTitle(title);
            mAlertDialogBuilder.setMessage(desc);
            mAlertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            mAlertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDialog.dismiss();
                    mDialog.cancel();
                    mDialog = null;
                }
            });
            mDialog = mAlertDialogBuilder.create();
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    private static final int TIME_D = -60000;
    public static String getTimeZoneOffset()
    {
        TimeZone timezone = TimeZone.getDefault();
        int seconds = timezone.getOffset(Calendar.ZONE_OFFSET)/1000;
        double minutes = seconds/60;
        double hours = minutes/60;

        return hours+"";
    }

    public static String getCurrentDate(String dateFormat) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void hideKeyboard(View view, Context context) {
        if (context != null) {
            InputMethodManager mgr = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}

package com.projects.owner.camlocation.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;

import com.projects.owner.camlocation.R;


/**
 * Created by Wajahat Ali on 12/31/2015.
 */
public class ServiceProgressDialog extends Dialog {
    private static ServiceProgressDialog mDialog;

    private static Activity mContext;
    private static ProgressDialog mCircleProgressView;

    private ServiceProgressDialog(Context context) {
        super(context);
    }

    private ServiceProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * show custom progress dialog.
     */
    public static void showDialog(Activity context) {

        if (context == null) {
            return;
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = null;
        mContext = context;
        mDialog = new ServiceProgressDialog(context);

        mDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        resetProgressView();
        mCircleProgressView=new ProgressDialog(context, R.style.custom_no_bg_progress_bar);
        mCircleProgressView.setMessage("Loading");
        mCircleProgressView.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mCircleProgressView.setIndeterminate(true);

        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        mDialog.getWindow().setAttributes(lp);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialog != null && !mDialog.isShowing()) {
//                    mDialog.show();
                    mCircleProgressView.show();
                }
            }
        });
    }

    /**
     *
     */
    private static void resetProgressView() {
        try {
            if (mCircleProgressView != null) {
                mCircleProgressView = null;
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    /**
     *
     * @param context
     */
    public static void showDialogNotCancelable(Activity context) {
        if (context == null) {
            return;
        }
        showDialog(context);
        if (mDialog != null) {
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
        }
    }


    public static void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.cancel();
            mDialog.dismiss();
            mDialog = null;
            resetProgressView();
        }

        if(mCircleProgressView!= null && mCircleProgressView.isShowing())
        {
            mCircleProgressView.cancel();
            mCircleProgressView.dismiss();
            mCircleProgressView = null;
        }

    }

    /**
     * On cancel listener.
     *
     * @param listener
     */
    public void setOnCancelListener(OnCancelListener listener) {
        mDialog.setOnCancelListener(listener);
    }

    /**
     * on dissmiss listener.
     *
     * @param listener
     */
    public void setOnDismissListener(OnDismissListener listener) {
        mDialog.setOnDismissListener(listener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mContext != null) {
            mContext.onBackPressed();
        }
    }
}

package me.shetj.base.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import androidx.annotation.Keep;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import me.shetj.base.R;


/**
 * I think QMUIDialog is better but maybe user
 * @author shetj
 */
@Keep
@SuppressLint("InflateParams")
public class LoadingDialog {

    private static Dialog mLoadingDialog;

    public static Dialog showLoading(Activity context,  boolean cancelable){
        if (null != mLoadingDialog){
            mLoadingDialog.cancel();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null);

        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mLoadingDialog.show();
        return  mLoadingDialog;
    }

    public static Dialog showLoading(Activity context) {
        if (null != mLoadingDialog){
            mLoadingDialog.cancel();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null);
        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mLoadingDialog.show();
        return  mLoadingDialog;
    }

    public static void hideLoading(){
        if (null != mLoadingDialog){
            mLoadingDialog.cancel();
        }
    }

}

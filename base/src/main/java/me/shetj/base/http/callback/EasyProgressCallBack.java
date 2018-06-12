package me.shetj.base.http.callback;

import android.support.annotation.Keep;

import com.zhouyou.http.callback.ProgressDialogCallBack;
import com.zhouyou.http.subsciber.IProgressDialog;

@Keep
public class EasyProgressCallBack<T> extends ProgressDialogCallBack<T> {

	public EasyProgressCallBack(IProgressDialog progressDialog) {
		super(progressDialog);
	}

	public EasyProgressCallBack(IProgressDialog progressDialog, boolean isShowProgress, boolean isCancel) {
		super(progressDialog, isShowProgress, isCancel);
	}

	@Override
	public void onSuccess(T t) {

	}
}

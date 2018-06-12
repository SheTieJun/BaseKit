package me.shetj.base.http.callback;

import android.support.annotation.Keep;

import com.zhouyou.http.callback.CallBack;
import com.zhouyou.http.exception.ApiException;

@Keep
public class EasyCallBack<Object> extends CallBack<Object> {
	@Override
	public void onStart() {

	}

	@Override
	public void onCompleted() {

	}

	@Override
	public void onError(ApiException e) {

	}

	@Override
	public void onSuccess(Object o) {

	}
}

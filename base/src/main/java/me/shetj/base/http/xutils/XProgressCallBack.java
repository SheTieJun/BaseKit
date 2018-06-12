package me.shetj.base.http.xutils;

import android.support.annotation.Keep;

import org.xutils.common.Callback;

/**
 * 类名称：XProgressCallBack<br>
 *
 * @author shetj<br>
 */
@Keep
public class XProgressCallBack<ResultType> implements Callback.ProgressCallback<ResultType> {
	@Override
	public void onWaiting() {

	}

	@Override
	public void onStarted() {

	}

	@Override
	public void onLoading(long total, long current, boolean isDownloading) {

	}

	@Override
	public void onSuccess(ResultType result) {

	}

	@Override
	public void onError(Throwable ex, boolean isOnCallback) {

	}

	@Override
	public void onCancelled(CancelledException cex) {

	}

	@Override
	public void onFinished() {

	}
}

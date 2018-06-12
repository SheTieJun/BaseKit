package me.shetj.base.http.xutils.download;

import android.support.annotation.Keep;
import android.view.View;

import org.xutils.common.Callback;

import java.io.File;

/**
 * 
 * 类名称：DefaultDownloadViewHolder.java <br>
 * @author shetj<br>
 */
@Keep
public class DefaultDownloadViewHolder extends DownloadViewHolder {

    public DefaultDownloadViewHolder(View view, DownloadInfo downloadInfo) {
        super(view, downloadInfo);
    }

    @Override
    public void onWaiting() {

    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onLoading(long total, long current) {

    }

    @Override
    public void onSuccess(File result) {
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
    }

    @Override
    public void onCancelled(Callback.CancelledException cex) {
    }
}

package me.shetj.base.base;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;

import org.simple.eventbus.EventBus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * ================================================
 * 基类 {@link IntentService}
 * @author shetj
 */
@Keep
public abstract class BaseIntentService extends IntentService {
    protected final String TAG = this.getClass().getSimpleName();
    protected CompositeDisposable mCompositeDisposable;

    public BaseIntentService(String name) {
        super(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unDispose();//解除订阅
        this.mCompositeDisposable = null;
    }

    protected void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        //将所有subscription放入,集中处理
        mCompositeDisposable.add(disposable);
    }

    protected void unDispose() {
        //保证activity结束时取消所有正在执行的订阅
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        init();
    }

    /**
     * 初始化
     */
    abstract public void init();
}
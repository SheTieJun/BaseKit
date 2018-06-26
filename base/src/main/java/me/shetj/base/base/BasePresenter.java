package me.shetj.base.base;


import android.content.Intent;
import android.support.annotation.Keep;

import org.simple.eventbus.EventBus;
import org.xutils.common.util.LogUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 *
 * 在model中获取数据，在view（Activity）中展示数据
 * @author shetj
 */
@Keep
public  class BasePresenter< T extends BaseModel> implements IPresenter {

    private CompositeDisposable mCompositeDisposable;
    protected IView view;
    protected T model;

    public BasePresenter(IView view) {
        LogUtil.i("onStart");
        onStart();
        this.view = view;
    }

    @Override
    public void onStart() {
        if (useEventBus())
        {
	        EventBus.getDefault().register(this);
        }
    }



    /**
     * //解除订阅
     *  Activity#onDestroy() 调用{@link IPresenter#onDestroy()}
     */
    @Override
    public void onDestroy() {
        LogUtil.i("onDestroy");
        if (useEventBus())
        {
	        EventBus.getDefault().unregister(this);
        }
        unDispose();
        this.mCompositeDisposable = null;
        if (model !=null){
            model.onDestroy();
            model = null;
        }
    }

    /**
     * 是否使用 {@link EventBus},默认为使用(true)，
     *
     * @return
     */
    public boolean useEventBus() {
        return true;
    }


    /**
     * 将 {@link Disposable} 添加到 {@link CompositeDisposable} 中统一管理
     * 可在 {onDestroy() 中使用 {@link #unDispose()} 停止正在执行的 RxJava 任务,避免内存泄漏
     *
     * @param disposable
     */
    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    /**
     * 停止集合中正在执行的 RxJava 任务
     */
    public void unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    public void startActivity(Intent intent){
        if (null != view){
            view.getRxContext().startActivity(intent);
        }
    }

    public BaseMessage getMessage(int code,Object msg){
        BaseMessage message = new BaseMessage();
        message.obj = msg;
        message.type = code;
        return message;
    }

}

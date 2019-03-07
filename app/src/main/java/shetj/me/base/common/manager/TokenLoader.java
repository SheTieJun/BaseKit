package shetj.me.base.common.manager;

import com.zhouyou.http.EasyHttp;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import me.shetj.base.s;
import me.shetj.base.tools.file.SPUtils;
import me.shetj.base.tools.json.EmptyUtils;
import me.shetj.base.tools.json.GsonKit;
import shetj.me.base.api.API;
import timber.log.Timber;

import static shetj.me.base.common.tag.SPKey.SAVE_TOKEN;


/**
 * @author shetj
 */
public class TokenLoader {


    private AtomicBoolean mRefreshing = new AtomicBoolean(false);
    private PublishSubject<String> mPublishSubject;
    private Observable<String> mTokenObservable;

    private TokenLoader() {
        mPublishSubject = PublishSubject.create();
        HashMap<String,String> map=new HashMap<>();
        mTokenObservable = EasyHttp.post(API.QINIU_GET_TOKEN)
                .upJson(GsonKit.objectToJson(map))
                .execute(String.class)
                .doOnNext(token -> {
                    Timber.i( "存储Token=" + token);
                    TokenManager.getInstance().setToken(token);
                    mRefreshing.set(false);
                }).doOnError(throwable -> mRefreshing.set(false))
                .subscribeOn(Schedulers.io());
    }

    public static TokenLoader getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final TokenLoader INSTANCE = new TokenLoader();
    }

    public String getCacheToken() {
        String token = (String) SPUtils.Companion.get(s.INSTANCE.getApp().getApplicationContext(), SAVE_TOKEN, "");
        if (EmptyUtils.isEmpty(token)){
            return "token_fail";
        }
        return token;
    }

    @Deprecated
    public Observable<String> getNetTokenLocked() {
        if (mRefreshing.compareAndSet(false, true)) {
            Timber.i("没有请求，发起一次新的Token请求");
            startTokenRequest();
        } else {
            Timber.i( "已经有请求，直接返回等待");
        }
        return mPublishSubject;
    }

    private void startTokenRequest() {
        mTokenObservable.subscribe(mPublishSubject);
    }

}

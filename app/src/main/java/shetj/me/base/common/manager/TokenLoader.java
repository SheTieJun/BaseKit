package shetj.me.base.common.manager;


import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import me.shetj.base.network.RxHttp;
import me.shetj.base.S;
import me.shetj.base.tools.file.SPUtils;
import me.shetj.base.tools.json.EmptyUtils;
import me.shetj.base.tools.json.GsonKit;
import shetj.me.base.utils.TimeUtil;
import timber.log.Timber;


/**
 * @author shetj
 */
public class TokenLoader {


    private final Observable<String> mTokenObservable;
    /**
     * 是否已经在去请求Token
     */
    private AtomicBoolean mRefreshing = new AtomicBoolean(false);
    private PublishSubject<String> mPublishSubject;

    private static class Holder {
        private static final TokenLoader INSTANCE = new TokenLoader();
    }


    private TokenLoader() {
        mTokenObservable = buildRequester();
    }

    public static TokenLoader getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 获取通过
     * 如果过期 或者token 为空就重新去获取
     */
    public Observable<String> getToken() {
        String cacheToken = getCacheToken();
        if (TextUtils.isEmpty(cacheToken)) {
            Timber.i("token过期了");
            return getNetTokenLocked();
        } else {
            Timber.i("使用本地缓存token = %s", cacheToken);
            return Observable.just(cacheToken);
        }
    }


    private Observable<String> buildRequester() {
        HashMap<String, String> map = new HashMap<>();
        String expire = TimeUtil.getTime() + "";
        map.put("expire", expire);
        return RxHttp.post("test/url")
                .upJson(GsonKit.objectToJson(map))
                .executeCus(String.class)
                .doOnNext(token -> {
                    Timber.i("存储Token=" + token);
                    TokenManager.getInstance().setToken(token);
                    mRefreshing.set(false);
                }).doOnError(throwable -> mRefreshing.set(false)).subscribeOn(Schedulers.io());
    }

    private String getCacheToken() {
        String token = (String) SPUtils.get(S.getApp().getApplicationContext(), "PRE_CUSTOM_TOKEN", "");
        if (EmptyUtils.Companion.isNotEmpty(token)) {
            long timeDiff = TimeUtil.getTimeDiff(getExpire(S.getApp().getApplicationContext()));
            if (timeDiff > 50000) {
                return token;
            } else {
                token = "";
            }
            return token;
        }
        return "";
    }

    private static String getExpire(Context c) {
        return (String) SPUtils.Companion.get(c, "PRE_CUSTOM_TOKEN_FAILURE_TIME", TimeUtil.getYMDHMSTime());
    }

    private Observable<String> getNetTokenLocked() {
        if (mRefreshing.compareAndSet(false, true)) {
            startTokenRequest();
        } else {
            Timber.i("已经去请求token了获取token");
        }
        return mPublishSubject;
    }

    private void startTokenRequest() {
        mPublishSubject = PublishSubject.create();
        mTokenObservable.subscribe(mPublishSubject);
    }


}

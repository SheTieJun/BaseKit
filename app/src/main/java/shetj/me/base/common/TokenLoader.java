package shetj.me.base.common;

import com.zhouyou.http.EasyHttp;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import me.shetj.base.tools.file.SPUtils;
import me.shetj.base.tools.json.EmptyUtils;
import me.shetj.base.tools.json.GsonKit;
import shetj.me.base.api.API;

import static shetj.me.base.configs.tag.SPKey.SAVE_TOKEN;


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
                    LogUtil.i( "存储Token=" + token);
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
        String token = (String) SPUtils.get(x.app().getApplicationContext(), SAVE_TOKEN, "");
        if (EmptyUtils.isEmpty(token)){
            return "token_fail";
        }
        return token;
    }

    @Deprecated
    public Observable<String> getNetTokenLocked() {
        if (mRefreshing.compareAndSet(false, true)) {
            LogUtil.i("没有请求，发起一次新的Token请求");
            startTokenRequest();
        } else {
            LogUtil.i( "已经有请求，直接返回等待");
        }
        return mPublishSubject;
    }

    private void startTokenRequest() {
        mTokenObservable.subscribe(mPublishSubject);
    }

}

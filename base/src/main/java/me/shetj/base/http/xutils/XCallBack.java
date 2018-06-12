package me.shetj.base.http.xutils;

import android.support.annotation.Keep;

import org.xutils.common.Callback;

/**
 * 请求回调
 * @param <ResultType>
 */
@Keep
public class XCallBack<ResultType> implements Callback.CommonCallback<ResultType>{


  /**
   *可以根据公司的需求进行统一的请求成功的逻辑处理
   *1:请求成功
   *0:请求失败
   *422:token 失效
   *500:服务器异常
   * @param result
   */
  @Override
  public void onSuccess(ResultType result) {

  }

  /**
   *  //可以根据公司的需求进行统一的请求网络失败的逻辑处理
   * @param ex
   * @param isOnCallback
   */
  @Override
  public void onError(Throwable ex, boolean isOnCallback){
  }

  @Override
  public void onCancelled(CancelledException cex) {
  }

  @Override
  public void onFinished() {
  }


}

package shetj.me.base.common.manager;

import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.model.CacheMode;

import me.shetj.base.http.callback.EasyCallBack;
import me.shetj.base.tools.app.ArmsUtils;
import shetj.me.base.api.API;

/**
 * Created by shetj
 * on 2017/9/28.
 * @author shetj
 */

public class HttpOssManager {

  private static HttpOssManager instance = null;
  private HttpOssManager() {
  }

  public static HttpOssManager getInstance() {
    if (instance == null) {
      synchronized (HttpOssManager.class) {
        if (instance == null) {
          instance = new HttpOssManager();
        }
      }
    }
    return instance;
  }

  public  void getOSSFromSever(final EasyCallBack<String> callBack){
    EasyHttp.get(API.QINIU_GET_TOKEN)
            .cacheKey(ArmsUtils.Companion.encodeToMD5(API.QINIU_GET_TOKEN))
            .cacheMode(CacheMode.FIRSTCACHE)
            .cacheTime(1000*60)
            .execute(callBack);
  }

}

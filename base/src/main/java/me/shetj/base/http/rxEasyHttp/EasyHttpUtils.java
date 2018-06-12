package me.shetj.base.http.rxEasyHttp;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.support.annotation.Keep;

import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.converter.SerializableDiskConverter;
import com.zhouyou.http.cache.model.CacheMode;
import com.zhouyou.http.model.HttpHeaders;
import com.zhouyou.http.model.HttpParams;
import com.zhouyou.http.subsciber.IProgressDialog;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import me.shetj.base.tools.app.ArmsUtils;
import me.shetj.base.tools.json.GsonKit;
import me.shetj.base.view.LoadingDialog;

/**
 *
 * @author shetj
 * @date 2017/10/16
 * 2017年10月19日10:46:14
 */
@Keep
public class EasyHttpUtils {

	public static  void  init(Application application,boolean isDebug,String baseUrl){
		EasyHttp.init(application);
		initConfig(isDebug,baseUrl);
	}

	private static void initConfig(boolean isDebug,String baseUrl) {
		//全局设置请求头
		HttpHeaders headers = new HttpHeaders();

		//全局设置请求参数
		HttpParams params = new HttpParams();


		//以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
		EasyHttp.getInstance()

						//可以全局统一设置全局URL
						.setBaseUrl(baseUrl)
						//设置全局URL
						// 打开该调试开关并设置TAG,不需要就不要加入该行
						// 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
						.debug("EasyHttp", isDebug)
//						.addCommonHeaders(new HttpHeaders("Authorization", "Bearer " + TokenManager.getInstance().getToken()))
						//如果使用默认的60秒,以下三行也不需要设置
						.setReadTimeOut(20 * 1000)
						.setWriteTimeOut(10 * 1000)
						.setConnectTimeout(10 * 1000)

						//可以全局统一设置超时重连次数,默认为3次,那么最差的情况会请求4次(一次原始请求,三次重连请求),
						//不需要可以设置为0
						.setRetryCount(2)
						//网络不好自动重试3次
						//可以全局统一设置超时重试间隔时间,默认为500ms,不需要可以设置为0
						.setRetryDelay(400)
						//每次延时500ms重试
						//可以全局统一设置超时重试间隔叠加时间,默认为0ms不叠加
						.setRetryIncreaseDelay(400)
						//每次延时叠加500ms
						//可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体请看CacheMode
						.setCacheMode(CacheMode.NO_CACHE)
						//可以全局统一设置缓存时间,默认永不过期
						//-1表示永久缓存,单位:秒 ，Okhttp和自定义RxCache缓存都起作用
						.setCacheTime(-1)
						//全局设置自定义缓存保存转换器，主要针对自定义RxCache缓存
						//默认缓存使用序列化转化
						.setCacheDiskConverter(new SerializableDiskConverter())
						//全局设置自定义缓存大小，默认50M
						//设置缓存大小为100M
						.setCacheMaxSize(100 * 1024 * 1024)
						//设置缓存版本，如果缓存有变化，修改版本后，缓存就不会被加载。特别是用于版本重大升级时缓存不能使用的情况
						//缓存版本为1
						.setCacheVersion(1)
						//.setHttpCache(new Cache())//设置Okhttp缓存，在缓存模式为DEFAULT才起作用
						//可以设置https的证书,以下几种方案根据需要自己设置
						.setCertificates()                                  //方法一：信任所有证书,不安全有风险
						//.setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
						//配置https的域名匹配规则，不需要就不要加入，使用不当会导致https握手失败
						//.setHostnameVerifier(new SafeHostnameVerifier())
						//.addConverterFactory(GsonConverterFactory.create(gson))//本框架没有采用Retrofit的Gson转化，所以不用配置
						//设置全局公共头
						.addCommonHeaders(headers)
						//设置全局公共参数
						.addCommonParams(params);
//						.addNetworkInterceptor(new NoCacheInterceptor())//设置网络拦截器
						//.setCallFactory()//局设置Retrofit对象Factory
						//.setCookieStore()//设置cookie
						//.setOkproxy()//设置全局代理
						//.setOkconnectionPool()//设置请求连接池
						//.setCallbackExecutor()//全局设置Retrofit callbackExecutor
						//可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
						//开启post数据进行gzip后发送给服务器
//						.addInterceptor(new GzipRequestInterceptor());
//						.addInterceptor(new CustomSignInterceptor());//添加参数签名拦截器
	}

	public static IProgressDialog getProgressDialog(final Activity context){
		return new IProgressDialog() {
			@Override
			public Dialog getDialog() {
				return LoadingDialog.showLoading(context,"正在加载...",true);
			}
		};
	}

	public static Observable<String> doPost(String url, Map<String, String> map){
		return	doPost(url,map,String.class);
	}

	public static Observable<String> doPostCache(String url, Map<String, String> map){
		return	doPostCache(url,map,String.class);
	}

	public static Observable<String> doGet(String url, Map<String, String> map){
		return	doGet(url, map,String.class);
	}
	public static Observable<String> doGetCache(String url, Map<String, String> map){

		return	doGetCache(url, map,String.class);
	}


	public static <T> Observable<T> doPost(String url, Map<String, String> map, Class<T> Class){
		return	EasyHttp.post(url)
						.params(getParamsFromMap(map))
						.execute(Class);
	}

	public static <T> Observable<T> doPostCache(String url, Map<String, String> map, Class<T> Class){
		return	EasyHttp.post(url)
						.params(getParamsFromMap(map))
						.cacheKey(ArmsUtils.encodeToMD5(url+ GsonKit.objectToJson(map)))
						.cacheMode(CacheMode.FIRSTCACHE)
						.execute(Class);
	}

	public static <T> Observable<T> doGet(String url, Map<String, String> map, Class<T> Class){
		return	EasyHttp.get(url)
						.params(getParamsFromMap(map))
						.execute(Class);
	}

	public static <T> Observable<T> doGetCache(String url, Map<String, String> map, Class<T> Class){
		return	EasyHttp.post(url)
						.params(getParamsFromMap(map))
						.cacheKey(ArmsUtils.encodeToMD5(url+ GsonKit.objectToJson(map)))
						.cacheMode(CacheMode.FIRSTREMOTE)
						.execute(Class);
	}


	@NonNull
	public static HttpParams getParamsFromMap(@NonNull Map<String,String > map ){
		HttpParams httpParams = new HttpParams();
		for (String key : map.keySet()) {
			httpParams.put(key,map.get(key));
		}
		return httpParams;
	}



}

package me.shetj.base.network.request

import android.text.TextUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.shetj.base.network.RxHttp
import me.shetj.base.network.api.ApiService
import me.shetj.base.network.callBack.NetCallBack
import me.shetj.base.network.callBack.NetCallBackProxy
import me.shetj.base.network.func.ApiResultFunc
import me.shetj.base.network.func.HandleFuc
import me.shetj.base.network.func.HttpResponseFunc
import me.shetj.base.network.func.RetryExceptionFunc
import me.shetj.base.network.model.ApiResult
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.network.model.HttpParams
import me.shetj.base.network.subscriber.CallBackSubscriber
import okhttp3.*
import retrofit2.CallAdapter
import retrofit2.Converter
import timber.log.Timber
import kotlin.collections.ArrayList

abstract class BaseRequest<R : BaseRequest<R>>() {
    var baseUrl: String? = null
    var readTimeOut: Long = 0 //读超时
    var writeTimeOut: Long = 0 //写超时
    var connectTimeout: Long = 0//链接超时
    val networkInterceptors: MutableList<Interceptor> = ArrayList() //添加网络拦截器
    val interceptors: MutableList<Interceptor> = ArrayList() //自定义拦截器，对数据处理
    var converterFactories: MutableList<Converter.Factory> = ArrayList()
    var adapterFactories: MutableList<CallAdapter.Factory> = ArrayList()
    var headers: HttpHeaders = HttpHeaders() //添加的header
    var isDefault = true //使用默认的ApiManager
    var sign = false //是否需要签名
    var timeStamp = false //是否需要追加时间戳
    var accessToken = false //是否需要追加token

    protected var httpUrl: HttpUrl? = null
    protected var url: String? = null //请求url
    protected var retryCount = 0//重试次数默认3次
    protected var retryDelay = 0L //延迟xxms重试
    protected var retryIncreaseDelay = 0L//叠加延迟
    protected var isSyncRequest = false //是否是同步请求
    protected var params: HttpParams = HttpParams() //添加的param
    protected var apiManager: ApiService? = null //通用的的api接口

    constructor(url: String, isDefault: Boolean) : this(url) {
        this.isDefault = isDefault
    }

    constructor(url: String) : this() {
        this.url = url
        initRequest()
    }

    //region  public method
    private fun build(): R {
        if (apiManager == null) {
            apiManager = RxHttp.getInstance().getApiManager(this)
        }
        return this as R
    }


    open fun readTimeOut(readTimeOut: Long): R {
        this.readTimeOut = readTimeOut
        return this as R
    }

    open fun writeTimeOut(writeTimeOut: Long): R {
        this.writeTimeOut = writeTimeOut
        return this as R
    }

    open fun connectTimeout(connectTimeout: Long): R {
        this.connectTimeout = connectTimeout
        return this as R
    }


    open fun retryCount(retryCount: Int): R {
        require(retryCount >= 0) { "retryCount must > 0" }
        this.retryCount = retryCount
        return this as R
    }

    open fun retryDelay(retryDelay: Int): R {
        require(retryDelay >= 0) { "retryDelay must > 0" }
        this.retryDelay = retryDelay.toLong()
        return this as R
    }

    open fun retryIncreaseDelay(retryIncreaseDelay: Int): R {
        require(retryIncreaseDelay >= 0) { "retryIncreaseDelay must > 0" }
        this.retryIncreaseDelay = retryIncreaseDelay.toLong()
        return this as R
    }

    open fun addInterceptor(interceptor: Interceptor?): R {
        interceptors.add(checkNotNull(interceptor, {"interceptor == null"}))
        return this as R
    }

    open fun addNetworkInterceptor(interceptor: Interceptor?): R {
        networkInterceptors.add(checkNotNull(interceptor, {"interceptor == null"}))
        return this as R
    }


    /**
     * 设置Converter.Factory,默认GsonConverterFactory.create()
     */
    open fun addConverterFactory(factory: Converter.Factory?): R {
        converterFactories.add(factory!!)
        return this as R
    }

    /**
     * 设置CallAdapter.Factory,默认RxJavaCallAdapterFactory.create()
     */
    open fun addCallAdapterFactory(factory: CallAdapter.Factory?): R {
        adapterFactories.add(factory!!)
        return this as R
    }

    /**
     * 添加头信息
     */
    open fun headers(headers: HttpHeaders?): R {
        this.headers.put(headers)
        return this as R
    }

    /**
     * 添加头信息
     */
    open fun headers(key: String?, value: String?): R {
        headers.put(key, value)
        return this as R
    }

    /**
     * 移除头信息
     */
    open fun removeHeader(key: String?): R {
        headers.remove(key!!)
        return this as R
    }

    /**
     * 移除所有头信息
     */
    open fun removeAllHeaders(): R {
        headers.clear()
        return this as R
    }

    /**
     * 设置参数
     */
    open fun params(params: HttpParams?): R {
        this.params.put(params)
        return this as R
    }

    open fun params(key: String?, value: String?): R {
        params.put(key!!, value!!)
        return this as R
    }

    open fun params(keyValues: Map<String, String>?): R {
        params.put(keyValues)
        return this as R
    }

    open fun removeParam(key: String?): R {
        params.remove(key!!)
        return this as R
    }

    open fun removeAllParams(): R {
        params.clear()
        return this as R
    }

    open fun sign(sign: Boolean): R {
        this.sign = sign
        return this as R
    }

    open fun timeStamp(timeStamp: Boolean): R {
        this.timeStamp = timeStamp
        return this as R
    }

    open fun accessToken(accessToken: Boolean): R {
        this.accessToken = accessToken
        return this as R
    }

    open fun syncRequest(syncRequest: Boolean): R {
        isSyncRequest = syncRequest
        return this as R
    }

    //endregion

    protected abstract fun generateRequest(): Observable<ResponseBody>?

    //region private method
    private fun initRequest() {
        val rxHttp = RxHttp.getInstance()
        initSetting(rxHttp)
    }

    //一些默认配置
    private fun initSetting(config: RxHttp) {
        baseUrl = config.getBaseUrl()
        if (!TextUtils.isEmpty(baseUrl)) {
            httpUrl = HttpUrl.parse(baseUrl)
        }
        if (baseUrl == null && url != null && (url!!.startsWith("http://") || url!!.startsWith("https://"))) {
            httpUrl = HttpUrl.parse(url)
            baseUrl = httpUrl!!.url().protocol + "://" + httpUrl!!.url().host + "/"
        }

        retryCount = config.getRetryCount() //超时重试次数
        retryDelay = config.getRetryDelay() //超时重试延时
        retryIncreaseDelay = config.getRetryIncreaseDelay() //超时重试叠加延时
        val acceptLanguage: String? = HttpHeaders.acceptLanguage   //默认添加 Accept-Language
        if (!TextUtils.isEmpty(acceptLanguage)) headers.put(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage)
        val userAgent: String? = HttpHeaders.userAgent //默认添加 User-Agent
        if (!TextUtils.isEmpty(userAgent)) headers.put(HttpHeaders.HEAD_KEY_USER_AGENT, userAgent)
        //添加公共请求参数
        if (config.getCommonParams() != null) params.put(config.getCommonParams())
        if (config.getCommonHeaders() != null) headers.put(config.getCommonHeaders())
    }
    //endregion

    ////region 请求执行

    /**
     * 执行，自定义数据类型
     */
    open fun <T> executeCus(type: Class<T>): Observable<T> {
        return build().generateRequest()!!
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ApiResultFunc<T>(type))
                .map(HandleFuc<T>())
                .doOnSubscribe { disposable: Disposable -> Timber.i("+++doOnSubscribe+++%s", disposable.isDisposed) }
                .doFinally { Timber.i("+++doFinally+++") }
                .onErrorResumeNext(HttpResponseFunc<T>())
                .retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
    }

    /**
     * 执行，自定义数据类型
     */
    open fun <T> executeCus(callback: NetCallBack<T>): Disposable {
        return build().generateRequest()!!
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ApiResultFunc<T>(callback.getType()))
                .map(HandleFuc<T>())
                .doOnSubscribe { disposable: Disposable -> Timber.i("+++doOnSubscribe+++%s", disposable.isDisposed) }
                .doFinally { Timber.i("+++doFinally+++") }
                .onErrorResumeNext(HttpResponseFunc<T>())
                .retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
                .subscribeWith(CallBackSubscriber<T>(callback))
    }

    /**
     * ApiResult<T> 类型的扩张类型
     */
    open fun <T> execute(callback: NetCallBack<T>): Disposable {
        //T -> ApiResult<T>
        val callBackProxy = object : NetCallBackProxy<ApiResult<T>, T>(callback) {

        }
        return build().generateRequest()!!
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ApiResultFunc<T>(callBackProxy.getType()))
                .map(HandleFuc<T>())
                .doOnSubscribe { disposable: Disposable -> Timber.i("+++doOnSubscribe+++%s", disposable.isDisposed) }
                .doFinally { Timber.i("+++doFinally+++") }
                .onErrorResumeNext(HttpResponseFunc<T>())
                .retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
                .subscribeWith(CallBackSubscriber<T>(callBackProxy.callBack))
    }

    //endregion
}
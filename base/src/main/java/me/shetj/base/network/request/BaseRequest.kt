package me.shetj.base.network.request

import android.text.TextUtils
import io.reactivex.Observable
import me.shetj.base.network.RxHttp
import me.shetj.base.network.api.ApiService
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.network.model.HttpParams
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.ResponseBody
import retrofit2.CallAdapter
import retrofit2.Converter
import java.util.*

abstract class BaseRequest<R : BaseRequest<R>>() {
    var baseUrl: String? = null
    protected var httpUrl: HttpUrl? = null
    protected var url: String? = null //请求url
    var readTimeOut: Long = 0 //读超时
    var writeTimeOut: Long = 0 //写超时
    var connectTimeout: Long = 0//链接超时
    protected var retryCount = 0//重试次数默认3次
    protected var retryDelay = 0L //延迟xxms重试
    protected var retryIncreaseDelay = 0L//叠加延迟
    protected var isSyncRequest = false //是否是同步请求
    private val sign = false //是否需要签名
    private val timeStamp = false //是否需要追加时间戳
    private val accessToken = false //是否需要追加token
    val networkInterceptors: List<Interceptor> = ArrayList() //添加网络拦截器
    val interceptors: List<Interceptor> = ArrayList() //自定义拦截器，对数据处理
    var converterFactories: MutableList<Converter.Factory> = ArrayList()
    var adapterFactories: MutableList<CallAdapter.Factory> = ArrayList()
    protected var apiManager: ApiService? = null //通用的的api接口
    var headers: HttpHeaders = HttpHeaders() //添加的header
    protected var params: HttpParams = HttpParams() //添加的param
    var isDefault = true //使用默认的ApiManager

    constructor(url: String, isDefault: Boolean) : this(url) {
        this.isDefault = isDefault
    }

    constructor(url: String) : this() {
        initRequest()
    }

    //region  public method
    /**
     * 设置Converter.Factory,默认GsonConverterFactory.create()
     */
    open fun addConverterFactory(factory: Converter.Factory): R {
        converterFactories.add(factory)
        return this as R
    }

    /**
     * 设置CallAdapter.Factory,默认RxJavaCallAdapterFactory.create()
     */
    open fun addCallAdapterFactory(factory: CallAdapter.Factory): R {
        adapterFactories.add(factory)
        return this as R
    }

    protected open fun build(): R {
        apiManager = RxHttp.getInstance().getApiManager(this)
        return this as R
    }

    //endregion

    protected abstract fun generateRequest(): Observable<ResponseBody>

    //region private method
    private fun initRequest() {
        val rxHttp = RxHttp.getInstance()
        initSetting(rxHttp)
    }

    //一些默认配置
    private fun initSetting(config: RxHttp) {
        baseUrl = config.getBaseUrl()
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


}
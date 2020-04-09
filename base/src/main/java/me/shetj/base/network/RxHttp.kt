package me.shetj.base.network

import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.network.model.HttpParams
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RxHttp private constructor() {
    // region 相关的参数
    val DEFAULT_MILLISECONDS = 30000 //默认的超时时间20秒

    private val DEFAULT_RETRY_COUNT = 3 //默认重试次数

    private val DEFAULT_RETRY_INCREASEDELAY = 0 //默认重试叠加时间

    private val DEFAULT_RETRY_DELAY = 500 //默认重试延时

    val DEFAULT_CACHE_NEVER_EXPIRE = -1 //缓存过期时间，默认永久缓存

    private var mRetryCount: Int = DEFAULT_RETRY_COUNT //重试次数默认3次

    private var mRetryDelay: Int = DEFAULT_RETRY_DELAY //延迟xxms重试

    private var mRetryIncreaseDelay: Int = DEFAULT_RETRY_INCREASEDELAY //叠加延迟

    private var mCommonHeaders: HttpHeaders? = null //全局公共请求头
    private var mCommonParams: HttpParams? = null //全局公共请求参数

    //endregion

    //region retrofit 相关
    private val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
    private val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    private var mBaseUrl: String? = null

    //region retrofit 相关
    init {
        initClient()
        initRetrofit()
    }

    private fun initRetrofit() {
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create())
    }

    private fun initClient() {
        okHttpClientBuilder.hostnameVerifier { _, _ -> true }
        okHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS.toLong(), TimeUnit.MILLISECONDS)
        okHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS.toLong(), TimeUnit.MILLISECONDS)
        okHttpClientBuilder.writeTimeout(DEFAULT_MILLISECONDS.toLong(), TimeUnit.MILLISECONDS)
    }

    companion object {
        private var rxHttp: RxHttp? = null

        fun getInstance(): RxHttp {
            return rxHttp ?: RxHttp().also {
                rxHttp = it
            }
        }


    }

    fun debug(isPrintException: Boolean): RxHttp {
        if (isPrintException) {
            okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Timber.i(it)
            }).apply { level = HttpLoggingInterceptor.Level.BODY })
        }
        return this
    }

    //region public方法

    //对外暴露 OkHttpClient,方便自定义
    fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        return getInstance().okHttpClientBuilder
    }

    //对外暴露 Retrofit,方便自定义
    fun getRetrofitBuilder(): Retrofit.Builder {
        return getInstance().retrofitBuilder
    }

    //endregion


    //region 全局设置BaseUrl
    fun setBaseUrl(mBaseUrl: String?): RxHttp {
        checkNotNull(mBaseUrl, { "baseUrl == null" })
        this.mBaseUrl = mBaseUrl
        return this
    }

    fun getBaseUrl(): String? {
        return getInstance().mBaseUrl
    }
    //endregion 全局设置BaseUrl

    //region Time 相关超时、和重试次数设置
    /**全局读取超时时间*/
    fun setReadTimeOut(readTimeOut: Long): RxHttp {
        okHttpClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
        return this
    }

    /**全局写入超时时间*/
    fun setWriteTimeOut(writeTimeout: Long): RxHttp {
        okHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
        return this
    }

    /**全局连接超时时间*/
    fun setConnectTimeout(connectTimeout: Long): RxHttp {
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
        return this
    }


    /**超时重试次数*/
    fun setRetryCount(retryCount: Int): RxHttp {
        require(retryCount >= 0) { "retryCount must > 0" }
        mRetryCount = retryCount
        return this
    }

    /**
     * 超时重试次数
     */
    fun getRetryCount(): Int {
        return getInstance().mRetryCount
    }


    /**
     * 添加全局公共请求参数
     */
    fun addCommonParams(commonParams: HttpParams?): RxHttp? {
        if (mCommonParams == null) mCommonParams = HttpParams()
        mCommonParams?.put(commonParams)
        return this
    }

    /**
     * 获取全局公共请求参数
     */
    fun getCommonParams(): HttpParams? {
        return mCommonParams
    }

    /**
     * 获取全局公共请求头
     */
    fun getCommonHeaders(): HttpHeaders? {
        return mCommonHeaders
    }

    /**
     * 添加全局公共请求参数
     */
    fun addCommonHeaders(commonHeaders: HttpHeaders?): RxHttp? {
        if (mCommonHeaders == null) mCommonHeaders = HttpHeaders()
        mCommonHeaders?.put(commonHeaders)
        return this
    }


    /**
     * 超时重试延迟时间
     */
    fun setRetryDelay(retryDelay: Int): RxHttp? {
        require(retryDelay >= 0) { "retryDelay must > 0" }
        mRetryDelay = retryDelay
        return this
    }

    /**
     * 超时重试延迟时间
     */
    fun getRetryDelay(): Int {
        return getInstance().mRetryDelay
    }

    /**
     * 超时重试延迟叠加时间
     */
    fun setRetryIncreaseDelay(retryIncreaseDelay: Int): RxHttp? {
        require(retryIncreaseDelay >= 0) { "retryIncreaseDelay must > 0" }
        mRetryIncreaseDelay = retryIncreaseDelay
        return this
    }

    /**
     * 超时重试延迟叠加时间
     */
    fun getRetryIncreaseDelay(): Int {
        return getInstance().mRetryIncreaseDelay
    }

    //endregion


    //region other
    /**
     * 添加全局拦截器
     */
    fun addInterceptor(interceptor: Interceptor?): RxHttp {
        okHttpClientBuilder.addInterceptor(checkNotNull(interceptor, { "interceptor == null" }))
        return this
    }

    /**
     * 全局设置请求的连接池
     */
    fun setOkconnectionPool(connectionPool: ConnectionPool?): RxHttp {
        okHttpClientBuilder.connectionPool(checkNotNull(connectionPool, { "setOkconnectionPool  not null" }))
        return this
    }

    /**
     * 全局为Retrofit设置自定义的OkHttpClient
     */
    fun setOkclient(client: OkHttpClient?): RxHttp {
        retrofitBuilder.client(checkNotNull(client, { "client == null" }))
        return this
    }

    //endregion

    //region 请求归类
    fun get(url: String) {

    }

    fun post(url: String) {

    }

    fun put(url: String) {

    }

    fun download(url: String) {

    }
    //endregion

}
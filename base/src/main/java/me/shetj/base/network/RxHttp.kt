package me.shetj.base.network

import me.shetj.base.network.api.ApiService
import me.shetj.base.network.interceptor.HeadersInterceptor
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.network.model.HttpParams
import me.shetj.base.network.request.BaseRequest
import me.shetj.base.network.request.GetRequest
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

open class RxHttp private constructor() {
    // region 相关的参数
    val DEFAULT_MILLISECONDS = 30000 //默认的超时时间20秒

    private val DEFAULT_RETRY_COUNT = 3 //默认重试次数

    private val DEFAULT_RETRY_INCREASEDELAY = 0L //默认重试叠加时间

    private val DEFAULT_RETRY_DELAY = 500L //默认重试延时

    val DEFAULT_CACHE_NEVER_EXPIRE = -1 //缓存过期时间，默认永久缓存

    private var mRetryCount: Int = DEFAULT_RETRY_COUNT //重试次数默认3次

    private var mRetryDelay: Long = DEFAULT_RETRY_DELAY //延迟xxms重试

    private var mRetryIncreaseDelay: Long = DEFAULT_RETRY_INCREASEDELAY //叠加延迟

    private var mCommonHeaders: HttpHeaders? = null //全局公共请求头
    private var mCommonParams: HttpParams? = null //全局公共请求参数

    //endregion

    //region retrofit 相关
    private val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
    private val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    private var mBaseUrl: String? = null
    private val apiManager: ApiService by lazy {
        getApiManagerDef()
    }


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
            return rxHttp ?: synchronized(RxHttp::class.java) {
                RxHttp().also {
                    rxHttp = it
                }
            }
        }

        fun get(url: String): GetRequest {
            return GetRequest(url)
        }

        fun post(url: String) {

        }

        fun put(url: String) {

        }

        fun download(url: String) {

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

    //region  ApiManager的获取

    fun getDeApiManager(): ApiService {
        return apiManager
    }

    fun getApiManager(baseRequest: BaseRequest<*>): ApiService {
        return if (baseRequest.isDefault) {
            getDeApiManager()
        } else {
            val okHttpClientBuilder: OkHttpClient.Builder = generateOkClient(baseRequest)
            val retrofitBuilder: Retrofit.Builder = generateRetrofit(baseRequest)
            return retrofitBuilder.client(okHttpClientBuilder.build()).build().create(ApiService::class.java)
        }
    }

    //对外暴露 OkHttpClient,方便自定义
    private fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        return getInstance().okHttpClientBuilder
    }

    //对外暴露 Retrofit,方便自定义
    private fun getRetrofitBuilder(): Retrofit.Builder {
        return getInstance().retrofitBuilder
    }

    private fun getOkHttpClient(): OkHttpClient {
        return getInstance().okHttpClientBuilder.build()
    }

    private fun getApiManagerDef(): ApiService {
        return getRetrofitBuilder().apply {
            client(getOkHttpClientBuilder()
                    .apply {
                        if (mCommonHeaders?.isEmpty != true) {
                            addInterceptor(HeadersInterceptor(mCommonHeaders!!))
                        }
                    }.build())
            mBaseUrl?.let { this.baseUrl(it) }
        }.build().create(ApiService::class.java)
    }

    //根据当前的请求参数，生成对应的OkClient
    private fun generateOkClient(baseRequest: BaseRequest<*>): OkHttpClient.Builder {
        return if (baseRequest.readTimeOut <= 0 && baseRequest.writeTimeOut <= 0
                && baseRequest.connectTimeout <= 0 && baseRequest.headers.isEmpty) {
            getOkHttpClientBuilder().apply {

            }
        } else {
            getOkHttpClient().newBuilder().apply {
                if (baseRequest.readTimeOut > 0) readTimeout(baseRequest.readTimeOut, TimeUnit.MILLISECONDS)
                if (baseRequest.writeTimeOut > 0) writeTimeout(baseRequest.writeTimeOut, TimeUnit.MILLISECONDS)
                //处理head
                if (!baseRequest.headers.isEmpty) {
                    addInterceptor(HeadersInterceptor(baseRequest.headers))
                }
                //处理拦截器
                baseRequest.interceptors.forEach {
                    addInterceptor(it)
                }
                //处理netInterceptor
                baseRequest.networkInterceptors.forEach {
                    addNetworkInterceptor(it)
                }
            }
        }.apply {
            //处理共同的
        }
    }


    private fun generateRetrofit(baseRequest: BaseRequest<*>): Retrofit.Builder {
        return if (baseRequest.converterFactories.isEmpty() && baseRequest.adapterFactories.isEmpty()) {
            getRetrofitBuilder()
        } else {
            Retrofit.Builder().apply {
                //添加转换器
                if (baseRequest.converterFactories.isEmpty()) {
                    getRetrofitBuilder().converterFactories()
                } else {
                    baseRequest.converterFactories
                }.forEach {
                    addConverterFactory(it)
                }
                //添加callAdapter
                if (baseRequest.adapterFactories.isEmpty()) {
                    getRetrofitBuilder().callAdapterFactories()
                } else {
                    baseRequest.adapterFactories
                }.forEach {
                    addCallAdapterFactory(it)
                }
            }
        }.apply {
            if (!baseRequest.baseUrl.isNullOrEmpty()) {
                this.baseUrl(baseRequest.baseUrl)
            }
        }
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
    fun setRetryDelay(retryDelay: Long): RxHttp? {
        require(retryDelay >= 0) { "retryDelay must > 0" }
        mRetryDelay = retryDelay
        return this
    }

    /**
     * 超时重试延迟时间
     */
    fun getRetryDelay(): Long {
        return getInstance().mRetryDelay
    }

    /**
     * 超时重试延迟叠加时间
     */
    fun setRetryIncreaseDelay(retryIncreaseDelay: Long): RxHttp? {
        require(retryIncreaseDelay >= 0) { "retryIncreaseDelay must > 0" }
        mRetryIncreaseDelay = retryIncreaseDelay
        return this
    }

    /**
     * 超时重试延迟叠加时间
     */
    fun getRetryIncreaseDelay(): Long {
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


}
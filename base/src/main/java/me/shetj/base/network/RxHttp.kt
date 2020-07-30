package me.shetj.base.network

import me.shetj.base.network.api.ApiService
import me.shetj.base.network.https.HttpsUtils
import me.shetj.base.network.interceptor.HeadersInterceptor
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.network.model.HttpParams
import me.shetj.base.network.request.*
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit

open class RxHttp private constructor() {
    // region 相关的参数
    private val DEFAULT_MILLISECONDS = 20000 //默认的超时时间20秒

    private val DEFAULT_RETRY_COUNT = 3 //默认重试次数

    private val DEFAULT_RETRY_INCREASEDELAY = 0L //默认重试叠加时间

    private val DEFAULT_RETRY_DELAY = 500L //默认重试延时

    private var mRetryCount: Int = DEFAULT_RETRY_COUNT //重试次数默认3次

    private var mRetryDelay: Long = DEFAULT_RETRY_DELAY //延迟xxms重试

    private var mRetryIncreaseDelay: Long = DEFAULT_RETRY_INCREASEDELAY //叠加延迟

    private var mCommonHeaders: HttpHeaders? = null //全局公共请求头
    private var mCommonParams: HttpParams? = null //全局公共请求参数

    private var isPrintException: Boolean = false

    //endregion

    //region retrofit 相关
    private val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
    private val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    private var mBaseUrl: String? = null
    private val apiManager: ApiService by lazy {
        getApiManager(ApiService::class.java)
    }

    private val apiMap = WeakHashMap<String, Any>()

    //region retrofit 相关
    init {
        initClientSetting()
        initRetrofitSetting()
    }

    private fun initRetrofitSetting() {
        retrofitBuilder.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create())
    }

    private fun initClientSetting() {
        okHttpClientBuilder.hostnameVerifier { _, _ -> true } //主机验证
        okHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS.toLong(), TimeUnit.MILLISECONDS)
        okHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS.toLong(), TimeUnit.MILLISECONDS)
        okHttpClientBuilder.writeTimeout(DEFAULT_MILLISECONDS.toLong(), TimeUnit.MILLISECONDS)
    }

    companion object {
        private var rxHttp: RxHttp? = null

        @JvmStatic
        fun getInstance(): RxHttp {
            return rxHttp ?: synchronized(RxHttp::class.java) {
                RxHttp().also {
                    rxHttp = it
                }
            }
        }

        @JvmStatic
        fun get(url: String): GetRequest {
            return GetRequest(url)
        }

        @JvmStatic
        fun post(url: String): PostRequest {
            return PostRequest(url)
        }

        @JvmStatic
        fun put(url: String): PutRequest {
            return PutRequest(url)
        }

        @JvmStatic
        fun delete(url: String): DeleteRequest {
            return DeleteRequest(url)
        }
    }

    fun debug(isPrintException: Boolean): RxHttp {
        this.isPrintException = isPrintException
        if (isPrintException) {
            okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Timber.i(it)
            }).apply { level = HttpLoggingInterceptor.Level.BASIC })
        }
        return this
    }

    //region  ApiManager的获取

    @Suppress("UNCHECKED_CAST")
    open fun <T> getApiManager(clazz: Class<T>): T {
        val apiManager = apiMap[clazz.simpleName]
        return if (apiManager == null) {
            val client = getOkHttpClientBuilder().apply {
                mCommonHeaders?.let {
                    addInterceptor(HeadersInterceptor(it))
                }
            }.build()
            getRetrofitBuilder().apply {
                client(client)
                mBaseUrl?.let { this.baseUrl(it) }
            }.build().create(clazz).also {
                apiMap[clazz.simpleName] = it
            }
        } else {
            apiManager as T
        }
    }

    fun getApiManager(baseRequest: BaseRequest<*>): ApiService {
        return if (baseRequest.isDefault) {
            getDeApiManager() //默认尽量是常用的
        } else {
            //直接生成新的
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

    private fun getDeApiManager(): ApiService {
        return apiManager
    }

    //根据当前的请求参数，生成对应的OkClient
    private fun generateOkClient(baseRequest: BaseRequest<*>): OkHttpClient.Builder {
        return getOkHttpClient().newBuilder().apply {
            if (baseRequest.readTimeOut > 0) readTimeout(baseRequest.readTimeOut, TimeUnit.MILLISECONDS)
            if (baseRequest.writeTimeOut > 0) writeTimeout(baseRequest.writeTimeOut, TimeUnit.MILLISECONDS)
            if (baseRequest.sslParams != null) {
                //SSL/TLS证书
                sslSocketFactory(baseRequest.sslParams!!.sSLSocketFactory,
                        baseRequest.sslParams!!.trustManager)
            }

            //处理拦截器
            baseRequest.interceptors.forEach {
                addInterceptor(it)
            }
            //处理netInterceptor
            baseRequest.networkInterceptors.forEach {
                addNetworkInterceptor(it)
            }
        }.apply {
            //处理共同的
            //处理header
            if (!baseRequest.headers.isEmpty) {
                addInterceptor(HeadersInterceptor(baseRequest.headers))
            }
            mCommonHeaders?.let {
                addInterceptor(HeadersInterceptor(it))
            }
            if (isPrintException) {
                addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                    Timber.i(it)
                }).apply { level = HttpLoggingInterceptor.Level.BODY })
            }
        }
    }


    private fun generateRetrofit(baseRequest: BaseRequest<*>): Retrofit.Builder {
        return Retrofit.Builder().apply {
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
        }.apply {
            if (!baseRequest.baseUrl.isNullOrEmpty()) {
                this.baseUrl(baseRequest.baseUrl!!)
            } else {
                mBaseUrl?.let {
                    this.baseUrl(it)
                }
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
    fun addCommonParams(commonParams: HttpParams?): RxHttp {
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
    fun addCommonHeaders(commonHeaders: HttpHeaders?): RxHttp {
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
    fun setRetryIncreaseDelay(retryIncreaseDelay: Long): RxHttp {
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
     * https的全局自签名证书
     */
    open fun setCertificates(vararg certificates: InputStream?): RxHttp? {
        val sslParams: HttpsUtils.SSLParams = HttpsUtils.getSslSocketFactory(null, null, certificates)
        okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
        return this
    }

    /**
     * https双向认证证书
     */
    open fun setCertificates(bksFile: InputStream?, password: String?, vararg certificates: InputStream?): RxHttp? {
        val sslParams: HttpsUtils.SSLParams = HttpsUtils.getSslSocketFactory(bksFile, password, certificates)
        okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
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
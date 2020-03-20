package me.shetj.base.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

class RxHttp  private constructor(){

    //region 私有自己
    private var rxHttp:RxHttp ?=null
    //end region
    // region 相关的参数
    val DEFAULT_MILLISECONDS = 30000 //默认的超时时间20秒

    private val DEFAULT_RETRY_COUNT = 3 //默认重试次数

    private val DEFAULT_RETRY_INCREASEDELAY = 0 //默认重试叠加时间

    private val DEFAULT_RETRY_DELAY = 500 //默认重试延时

    val DEFAULT_CACHE_NEVER_EXPIRE = -1 //缓存过期时间，默认永久缓存

    private var mRetryCount: Int = DEFAULT_RETRY_COUNT //重试次数默认3次

    private val mRetryDelay: Int =  DEFAULT_RETRY_DELAY //延迟xxms重试

    private val mRetryIncreaseDelay: Int = DEFAULT_RETRY_INCREASEDELAY //叠加延迟

    //endregion

    //region retrofit 相关
    private val retrofitBuilder:Retrofit.Builder =Retrofit.Builder()
    private val okHttpClientBuilder :OkHttpClient.Builder = OkHttpClient.Builder()
    private var mBaseUrl:String ?=null
    //region retrofit 相关
    init {

        okHttpClientBuilder.hostnameVerifier { _, _ -> true }
        okHttpClientBuilder.connectTimeout( DEFAULT_MILLISECONDS.toLong(), TimeUnit.MILLISECONDS)
        okHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS.toLong(), TimeUnit.MILLISECONDS)
        okHttpClientBuilder.writeTimeout(DEFAULT_MILLISECONDS.toLong(), TimeUnit.MILLISECONDS)
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }


    fun getInstance():RxHttp{
        return rxHttp?:RxHttp().also {
            rxHttp = it
        }
    }


    //region 全局设置BaseUrl
    fun setBaseUrl(mBaseUrl: String?): RxHttp {
        checkNotNull(mBaseUrl,{"baseUrl == null" })
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
    //endregion



}
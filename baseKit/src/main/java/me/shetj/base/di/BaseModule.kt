package me.shetj.base.di

import androidx.paging.Pager
import androidx.paging.PagingConfig
import me.shetj.base.S
import me.shetj.base.ktx.saverDB
import me.shetj.base.mvp.BaseBindingActivity
import me.shetj.base.mvp.BaseBindingFragment
import me.shetj.base.mvp.BaseFragment
import me.shetj.base.network.RxHttp.Companion.DEFAULT_MILLISECONDS
import me.shetj.base.network.interceptor.HeadersInterceptor
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.network.ohter.OkHttpDns
import me.shetj.base.network_coroutine.KCApiService
import me.shetj.base.saver.SaverDatabase
import me.shetj.base.tools.file.EnvironmentStorage
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

val dbModule = module() {

    single { SaverDatabase.getInstance(androidApplication()) }

    //try to override existing definition. 覆盖其他实例
    //(override = true) -> FIX:Please use override option or check for definition '[Single:'me.shetj.base.saver.SaverDao']'
    single(override = true) { get<SaverDatabase>().saverDao() }

    single<OkHttpClient> {
        get<OkHttpClient.Builder>().build()
    }

    single {
        OkHttpClient.Builder().apply {
            connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            addInterceptor(HeadersInterceptor(HttpHeaders().apply {
                put(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, HttpHeaders.acceptLanguage)
                put(HttpHeaders.HEAD_KEY_USER_AGENT, HttpHeaders.userAgent)
            }))
            cache(Cache(File(EnvironmentStorage.getPath(packagePath = "base")),1024*1024*12))
            dns(OkHttpDns.getInstance())
        }
    }

    single {
        Retrofit.Builder().apply {
            addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            addConverterFactory(GsonConverterFactory.create())
            client(get())
            baseUrl(S.baseUrl ?: "https://me.shetj.com")
            validateEagerly(S.isDebug) //在开始的时候直接开始检测所有的方法
        }
    }

    single<KCApiService> {
        get<Retrofit.Builder>().build().create(KCApiService::class.java)
    }


    single {
        Pager(PagingConfig(
                // 每页显示的数据的大小。对应 PagingSource 里 LoadParams.loadSize
                pageSize = 10,

                // 预刷新的距离，距离最后一个 item 多远时加载数据
                prefetchDistance = 3,

                // 初始化加载数量，默认为 pageSize * 3
                initialLoadSize = 30,
                // 一次应在内存中保存的最大数据
                maxSize = 200
        )
        ) {
            saverDB.searchSaver()
        }
    }
}
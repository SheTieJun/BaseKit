package me.shetj.base.di

import me.shetj.base.network.interceptor.HeadersInterceptor
import me.shetj.base.network.interceptor.HttpLoggingInterceptor
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.network_coroutine.KCApiService
import me.shetj.base.S
import me.shetj.base.saver.SaverDatabase
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val dbModule = module() {

    single { SaverDatabase.getInstance(androidApplication()) }

    //try to override existing definition. 覆盖其他实例
    //(override = true) -> FIX:Please use override option or check for definition '[Single:'me.shetj.base.saver.SaverDao']'
    single(override = true) { get<SaverDatabase>().saverDao() }

    single<OkHttpClient> {
        OkHttpClient.Builder().apply {
            connectTimeout(20000, TimeUnit.MILLISECONDS)
            readTimeout(20000, TimeUnit.MILLISECONDS)
            writeTimeout(20000, TimeUnit.MILLISECONDS)
            addInterceptor(HeadersInterceptor(HttpHeaders().apply {
                put(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, HttpHeaders.acceptLanguage)
                put(HttpHeaders.HEAD_KEY_USER_AGENT, HttpHeaders.userAgent)
            }))
            addInterceptor(HttpLoggingInterceptor("HTTP").apply { setLevel( HttpLoggingInterceptor.Level.BODY) })
        }.build()
    }

    single<Retrofit> {
        Retrofit.Builder().apply {
            addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            addConverterFactory(GsonConverterFactory.create())
            client(get())
            baseUrl(S.baseUrl?:"https://me.shetj.come")
        }.build()
    }

    single<KCApiService> {
        get<Retrofit>(Retrofit::class.java).create(KCApiService::class.java)
    }
}
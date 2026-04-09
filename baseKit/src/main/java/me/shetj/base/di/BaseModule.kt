package me.shetj.base.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.shetj.base.BaseKit
import me.shetj.base.netcoroutine.cache.KCCache
import me.shetj.base.netcoroutine.cache.LruDiskCache
import me.shetj.base.network.https.HttpsUtils
import me.shetj.base.network.interceptor.HeadersInterceptor
import me.shetj.base.network.interceptor.HttpLoggingInterceptor
import me.shetj.base.network.interceptor.ReceivedCookiesInterceptor
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.network.other.OkHttpDns
import me.shetj.base.saver.SaverDatabase
import me.shetj.base.tools.app.Utils
import me.shetj.base.tools.file.EnvironmentStorage
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.scope.get
import org.koin.dsl.module
import java.io.File
import java.util.concurrent.TimeUnit

internal fun getHttpModule(): Module {
    return module {
        single {
            get<OkHttpClient.Builder>().build()
        }

        single {
            HttpLoggingInterceptor("OkHttp").apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
                setLogEnable(true)
            }
        }

        single {
            ReceivedCookiesInterceptor(false)
        }

        single {
            HttpHeaders().apply {
                put(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, HttpHeaders.acceptLanguage)
                put(HttpHeaders.HEAD_KEY_USER_AGENT, HttpHeaders.userAgent)
            }
        }

        single {
            val timeout = 20000L // 默认的超时时间20秒

            OkHttpClient.Builder().apply {
                connectTimeout(timeout, TimeUnit.MILLISECONDS)
                readTimeout(timeout, TimeUnit.MILLISECONDS)
                writeTimeout(timeout, TimeUnit.MILLISECONDS)
                addInterceptor(HeadersInterceptor(get(HttpHeaders::class.java)))
                addInterceptor(get<ReceivedCookiesInterceptor>())
                hostnameVerifier { _, _ -> true } // 主机验证,默认都是通过的
                val sslParams: HttpsUtils.SSLParams = HttpsUtils.getSslSocketFactory(null, null, null)
                sslParams.trustManager?.let { trustManager -> sslParams.sSLSocketFactory?.let { it1 -> sslSocketFactory(it1, trustManager) } }
                addInterceptor(get<HttpLoggingInterceptor>())
                val path = EnvironmentStorage.getPath(root = Utils.app.cacheDir.absolutePath, packagePath = ".unKnow")
                cache(Cache(File(path), 1024 * 1024 * 12))
                dns(OkHttpDns.getInstance())
            }
        }

        single {
            val okHttpClient: OkHttpClient = get()
            HttpClient(OkHttp) {
                engine {
                    preconfigured = okHttpClient
                }
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                    })
                }
                defaultRequest {
                    url(BaseKit.baseUrl ?: "https://x.com/")
                }
            }
        }

        single {
            LruDiskCache(BaseKit.app.getExternalFilesDir("cacheFile"), 1024 * 1024 * 100)
        }

        single {
            KCCache()
        }
    }
}

internal fun getDBModule(): Module {
    return module {
        single(createdAtStart = false) { SaverDatabase.getInstance(BaseKit.app) }

        // try to override existing definition. 覆盖其他实例
        // (override = true) -> FIX:Please use override option or check for definition '[Single:'me.shetj.base.saver.SaverDao']'
        single(createdAtStart = false) { get<SaverDatabase>().saverDao() }
    }
}

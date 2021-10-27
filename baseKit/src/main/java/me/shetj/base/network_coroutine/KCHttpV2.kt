package me.shetj.base.network_coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import me.shetj.base.ktx.logd
import me.shetj.base.ktx.toBean
import me.shetj.base.network.exception.ApiException
import me.shetj.base.network.exception.ApiException.ERROR.OK_CACHE_EXCEPTION
import me.shetj.base.network.exception.CacheException
import me.shetj.base.network.kt.createJson
import me.shetj.base.network_coroutine.cache.CacheMode
import me.shetj.base.network_coroutine.cache.CacheOption
import me.shetj.base.network_coroutine.cache.KCCache
import okhttp3.RequestBody
import org.koin.java.KoinJavaComponent.get
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream


/**
 * 协程 Http请求
 * 感觉可能用的不错，所以就只写这几个方法了
 */
object KCHttpV2 {

    val apiService: KCApiService = get(KCApiService::class.java)

    val kcCache: KCCache by lazy { KCCache() }

    @JvmOverloads
    suspend inline fun <reified T> get(
        url: String,
        maps: Map<String, String>? = HashMap(),
        noinline cacheOption: (CacheOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            getDataFromApiOrCache(cacheOption) {
                apiService.get(url, maps).string()
            }
        }
    }

    @JvmOverloads
    suspend inline fun <reified T> post(
        url: String,
        maps: Map<String, String>? = HashMap(),
        noinline cacheOption: (CacheOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            getDataFromApiOrCache(cacheOption) {
                apiService.post(url, maps).string()
            }
        }
    }

    @JvmOverloads
    suspend inline fun <reified T> postJson(
        url: String,
        json: String,
        noinline cacheOption: (CacheOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            getDataFromApiOrCache(cacheOption) {
                apiService.postJson(url, json.createJson()).string()
            }
        }
    }

    @JvmOverloads
    suspend inline fun <reified T> postBody(
        url: String,
        body: Any,
        noinline cacheOption: (CacheOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            getDataFromApiOrCache(cacheOption) {
                apiService.postBody(url, body).string()
            }
        }
    }

    @JvmOverloads
    suspend inline fun <reified T> postBody(
        url: String,
        body: RequestBody,
        noinline cacheOption: (CacheOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            getDataFromApiOrCache(cacheOption) {
                apiService.postBody(url, body).string()
            }
        }
    }

    suspend inline fun <reified T> getDataFromApiOrCache(
        noinline cacheOption: (CacheOption.() -> Unit)?,
        crossinline formNetworkValue: suspend () -> String
    ): T {
        val cache = cacheOption?.let { CacheOption().apply(cacheOption) }
        return when {
            cache?.cacheMode == CacheMode.DEFAULT -> {

                //不使用自定义缓存,默认缓存规则，走OKhttp的Cache缓存
                formNetworkValue()
            }
            cache?.cacheMode == CacheMode.FIRST_NET -> {
                //先请求网络，请求网络失败后再加载缓存
                try {
                    formNetworkValue()
                } catch (e: Exception) {
                    withContext(Dispatchers.IO) {
                        kcCache.load(cache.cacheKey, cache.cacheTime)?.also {
                            "use cache key = ${cache.cacheKey} \n,value = $it ".logd()
                        }
                    } ?: throw ApiException.handleException(e)
                }
            }
            cache?.cacheMode == CacheMode.FIRST_CACHE -> {
                // 先加载缓存，缓存没有再去请求网络
                withContext(Dispatchers.IO) {
                    kcCache.load(cache.cacheKey, cache.cacheTime)
                        ?.also {
                            "use cache :cacheKey = ${cache.cacheKey} \n,value = $it ".logd()
                        }
                } ?: kotlin.run {
                    formNetworkValue().also {
                        saveCache(cache, it)
                    }
                }
            }
            cache?.cacheMode == CacheMode.ONLY_NET -> {
                //仅加载网络，但数据依然会被缓存
                formNetworkValue().also {
                    saveCache(cache, it)
                }
            }
            cache?.cacheMode == CacheMode.ONLY_CACHE -> {
                //只读取缓存
                withContext(Dispatchers.IO) {
                    kcCache.load(cache.cacheKey, cache.cacheTime)?.also {
                        "use cache : cacheKey = ${cache.cacheKey} \n,value = $it ".logd()
                    }
                } ?: throw CacheException(OK_CACHE_EXCEPTION,"cacheKey = '${cache.cacheKey}' no cache")
            }
            cache?.cacheMode == CacheMode.CACHE_NET_DISTINCT -> {
                /* 先使用缓存，不管是否存在，仍然请求网络，会先把缓存回调给你，
                 * 络请求回来发现数据是一样的就不会再返回，否则再返回
                 */
                val cacheInfo = withContext(Dispatchers.IO) {
                    kcCache.load(cache.cacheKey, cache.cacheTime)
                }
                val apiInfo = formNetworkValue()

                if (cacheInfo != apiInfo) {
                    saveCache(cache, apiInfo)
                    apiInfo
                } else {
                    throw CacheException(OK_CACHE_EXCEPTION,"the same data,so not update")
                }
            }
            else -> {
                formNetworkValue().also {
                    saveCache(cache, it)
                }
            }
        }.funTo()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @JvmOverloads
    suspend fun download(
        url: String, outputFile: String,
        onError: download_error = {},
        onProcess: download_process = { _, _, _ -> },
        onSuccess: download_success = { }
    ) {
        flow {
            try {
                val body = apiService.downloadFile(url)
                val contentLength = body.contentLength()
                val inputStream = body.byteStream()
                val file = File(outputFile)
                val outputStream = FileOutputStream(file)
                var currentLength = 0
                val bufferSize = 1024 * 8
                val buffer = ByteArray(bufferSize)
                val bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
                var readLength: Int
                while (bufferedInputStream.read(buffer, 0, bufferSize)
                        .also { readLength = it } != -1
                ) {
                    outputStream.write(buffer, 0, readLength)
                    currentLength += readLength
                    emit(
                        HttpResult.progress(
                            currentLength.toLong(),
                            contentLength,
                            currentLength.toFloat() / contentLength.toFloat()
                        )
                    )
                }
                bufferedInputStream.close()
                outputStream.close()
                inputStream.close()
                emit(HttpResult.success(file))
            } catch (e: Exception) {
                emit(HttpResult.failure<File>(ApiException.handleException(e)))
            }
        }.flowOn(Dispatchers.IO)
            .collect {
                it.fold(onFailure = { e ->
                    e?.let { it1 -> onError(it1) }
                }, onSuccess = { file ->
                    onSuccess(file)
                }, onLoading = { progress ->
                    onProcess(progress.currentLength, progress.length, progress.process)
                })
            }
    }

    suspend fun saveCache(cache: CacheOption?, data: String) {
        if (!cache?.cacheKey.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                kcCache.save(cache?.cacheKey, data)
            }
        }
    }

    inline fun <reified T> String.funTo() = if (T::class.java != String::class.java) {
        this.toBean()!!
    } else {
        this as T
    }
}

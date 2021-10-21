package me.shetj.base.network_coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import me.shetj.base.ktx.toBean
import me.shetj.base.network.cache.CacheOption
import me.shetj.base.network.cache.KCCache
import me.shetj.base.network.exception.ApiException
import me.shetj.base.network.kt.createJson
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
            val data = getDataFromApiOrCache(cacheOption) {
                apiService.get(url, maps).string()
            }
            funTo(data)
        }
    }

    suspend fun saveCache(cache: CacheOption?, data: String) {
        if (!cache?.cacheKey.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                kcCache.save(cache?.cacheKey, data)
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
            val data = getDataFromApiOrCache(cacheOption) {
                apiService.post(url, maps).string()
            }
            funTo(data)
        }
    }


    suspend inline fun <reified T> postJson(
        url: String,
        json: String,
        noinline cacheOption: (CacheOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            val data = getDataFromApiOrCache(cacheOption) {
                apiService.postJson(url, json.createJson()).string()
            }
            funTo(data)
        }
    }


    suspend inline fun <reified T> postBody(
        url: String,
        body: Any,
        noinline cacheOption: (CacheOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            val data = getDataFromApiOrCache(cacheOption) {
                apiService.postBody(url, body).string()
            }
            funTo(data)
        }
    }


    suspend inline fun <reified T> postBody(
        url: String,
        body: RequestBody,
        noinline cacheOption: (CacheOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            val data = getDataFromApiOrCache(cacheOption) {
                apiService.postBody(url, body).string()
            }
            funTo(data)
        }
    }

    suspend inline fun getDataFromApiOrCache(
        noinline cacheOption: (CacheOption.() -> Unit)?,
        crossinline goApi: suspend () -> String
    ): String {
        val cache = cacheOption?.let { CacheOption().apply(cacheOption) }
        return when {
            cache?.cacheKey.isNullOrEmpty() -> {
                goApi().also {
                    saveCache(cache, it)
                }
            }
            kcCache.containsKey(cache!!.cacheKey!!) -> {
                withContext(Dispatchers.IO) {
                    kcCache.load(cache.cacheKey, cache.cacheTime)
                } ?: kotlin.run {
                    goApi().also {
                        saveCache(cache, it)
                    }
                }
            }
            else -> {
                goApi().also {
                    saveCache(cache, it)
                }
            }
        }
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
            val body = apiService.downloadFile(url)
            try {
                val contentLength = body.contentLength()
                val ios = body.byteStream()
                val file = File(outputFile)
                val ops = FileOutputStream(file)
                var currentLength = 0
                val bufferSize = 1024 * 8
                val buffer = ByteArray(bufferSize)
                val bufferedInputStream = BufferedInputStream(ios, bufferSize)
                var readLength: Int
                while (bufferedInputStream.read(buffer, 0, bufferSize)
                        .also { readLength = it } != -1
                ) {
                    ops.write(buffer, 0, readLength)
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
                ops.close()
                ios.close()
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

    inline fun <reified T> funTo(data: String) = if (T::class.java != String::class.java) {
        data.toBean()!!
    } else {
        data as T
    }
}

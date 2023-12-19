package me.shetj.base.netcoroutine

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import me.shetj.base.coroutine.DispatcherProvider
import me.shetj.base.ktx.convertToT
import me.shetj.base.ktx.logD
import me.shetj.base.netcoroutine.cache.CacheMode
import me.shetj.base.network.exception.ApiException
import me.shetj.base.network.exception.ApiException.ERROR.OK_CACHE_EXCEPTION
import me.shetj.base.network.exception.ApiException.ERROR.TIMEOUT_ERROR
import me.shetj.base.network.exception.CacheException
import me.shetj.base.network.kt.createJson
import okhttp3.RequestBody
import org.koin.java.KoinJavaComponent.get
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream

/**
 * 协程 Http请求
 * - 感觉可能用的不多，所以就只写这几个方法了
 * - [me.shetj.base.netcoroutine.RequestOption]控制缓存
 * aar 情况下不能使用，很奇怪像是打包的bug
 */
object KCHttpV4 {

    const val TAG = "KCHttpV4"

    private val apiService: KCApiService = get(KCApiService::class.java)

    suspend inline fun <reified T> get(
        url: String,
        maps: Map<String, String>,
        requestOption: RequestOption
    ): HttpResult<T> {
        return runCatching<T> {
            doGetCache(url, maps, requestOption).convertToT()
        }
    }

    suspend inline fun <reified T> post(
        url: String,
        maps: Map<String, String>,
        requestOption: RequestOption
    ): HttpResult<T> {
        return runCatching<T> {
            doPostCache(url, maps, requestOption).convertToT()
        }
    }

    suspend inline fun <reified T> postJson(
        url: String,
        json: String,
        requestOption: RequestOption
    ): HttpResult<T> {
        return runCatching<T> {
            doPostJsonCache(url, json, requestOption).convertToT()
        }
    }

    suspend inline fun <reified T> postBody(
        url: String,
        body: Any,
        requestOption: RequestOption
    ): HttpResult<T> {
        return runCatching<T> {
            doPostBodyCache(url, body, requestOption).convertToT()
        }
    }

    suspend inline fun <reified T> postBody(
        url: String,
        body: RequestBody,
        requestOption: RequestOption
    ): HttpResult<T> {
        return runCatching<T> {
            doPostBodyCache(url, body, requestOption).convertToT()
        }
    }

    suspend fun doGetCache(
        url: String,
        maps: Map<String, String> = HashMap(),
        requestOption: RequestOption = url.getDefReqOption()
    ): String {
        return getDataFromApiOrCache(requestOption, fromNetworkValue = { doGetRetry(url, maps, requestOption) })
    }

    suspend fun doGetRetry(
        url: String,
        maps: Map<String, String>? = HashMap(),
        requestOption: RequestOption? = null
    ): String {
        return if (requestOption == null) {
            apiService.get(url, maps).string()
        } else {
            retryRequest(timeout = requestOption.timeout, repeatNum = requestOption.repeatNum) {
                apiService.get(url, maps).string()
            }
        }
    }

    suspend fun doPostCache(
        url: String,
        maps: Map<String, String> = HashMap(),
        requestOption: RequestOption = url.getDefReqOption()
    ): String {
        return getDataFromApiOrCache(requestOption, fromNetworkValue = { doPostRetry(url, maps, requestOption) })
    }

    suspend fun doPostRetry(
        url: String,
        maps: Map<String, String>? = HashMap(),
        requestOption: RequestOption? = null
    ): String {
        return if (requestOption == null) {
            apiService.post(url, maps).string()
        } else {
            retryRequest(timeout = requestOption.timeout, repeatNum = requestOption.repeatNum) {
                apiService.post(url, maps).string()
            }
        }
    }

    suspend fun doPostJsonCache(
        url: String,
        json: String,
        requestOption: RequestOption = url.getDefReqOption()
    ): String {
        return getDataFromApiOrCache(requestOption, fromNetworkValue = { doPostJsonRetry(url, json, requestOption) })
    }

    suspend fun doPostJsonRetry(
        url: String,
        json: String,
        requestOption: RequestOption
    ): String {
        return retryRequest(timeout = requestOption.timeout, repeatNum = requestOption.repeatNum) {
            apiService.postJson(url, json.createJson()).string()
        }
    }

    suspend fun doPostBodyCache(
        url: String,
        body: Any,
        requestOption: RequestOption = url.getDefReqOption()
    ): String {
        return getDataFromApiOrCache(requestOption, fromNetworkValue = { doPostBodyRetry(url, body, requestOption) })
    }

    suspend fun doPostBodyRetry(
        url: String,
        body: Any,
        requestOption: RequestOption
    ): String {
        return retryRequest(timeout = requestOption.timeout, repeatNum = requestOption.repeatNum) {
            apiService.postBody(url, body).string()
        }
    }

    suspend fun doPostBodyCache(
        url: String,
        body: RequestBody,
        requestOption: RequestOption = url.getDefReqOption()
    ): String {
        return getDataFromApiOrCache(requestOption, fromNetworkValue = { doPostBodyRetry(url, body, requestOption) })
    }

    suspend fun doPostBodyRetry(
        url: String,
        body: RequestBody,
        requestOption: RequestOption
    ): String {
        return retryRequest(timeout = requestOption.timeout, repeatNum = requestOption.repeatNum) {
            apiService.postBody(url, body).string()
        }
    }

    /**
     * @param requestOption  请求选项
     * @param fromNetworkValue 如果没有使用缓存，就会通过改方法获取，来自网络内容
     */
    private suspend fun getDataFromApiOrCache(
        requestOption: RequestOption? = null,
        fromNetworkValue: suspend () -> String
    ): String {
        if (requestOption?.cacheKey.isNullOrEmpty()) {
            return fromNetworkValue().convertToT()
        }
        return when (requestOption?.cacheMode) {
            CacheMode.DEFAULT -> {
                // 不使用自定义缓存,默认缓存规则，走OKhttp的Cache缓存
                fromNetworkValue()
            }

            CacheMode.FIRST_NET -> {
                // 先请求网络，请求网络失败后再加载缓存
                try {
                    fromNetworkValue()
                } catch (e: Exception) {
                    withContext(DispatcherProvider.io()) {
                        HttpKit.getKCCache().load(requestOption.cacheKey, requestOption.cacheTime)?.also {
                            "use cache key = ${requestOption.cacheKey} \n,value = $it ".logD(TAG)
                        }
                    } ?: throw ApiException.handleException(e)
                }
            }

            CacheMode.FIRST_CACHE -> {
                // 先加载缓存，缓存没有再去请求网络
                withContext(DispatcherProvider.io()) {
                    HttpKit.getKCCache().load(requestOption.cacheKey, requestOption.cacheTime)
                        ?.also {
                            "use cache :cacheKey = ${requestOption.cacheKey} \n,value = $it ".logD(TAG)
                        }
                } ?: kotlin.run {
                    fromNetworkValue().also {
                        saveCache(requestOption, it)
                    }
                }
            }

            CacheMode.ONLY_NET -> {
                // 仅加载网络，但数据依然会被缓存
                fromNetworkValue().also {
                    saveCache(requestOption, it)
                }
            }

            CacheMode.ONLY_CACHE -> {
                // 只读取缓存
                withContext(DispatcherProvider.io()) {
                    HttpKit.getKCCache().load(requestOption.cacheKey, requestOption.cacheTime)?.also {
                        "use cache : cacheKey = ${requestOption.cacheKey} \n,value = $it ".logD(TAG)
                    }
                } ?: throw CacheException(
                    OK_CACHE_EXCEPTION,
                    "cacheKey = '${requestOption.cacheKey}' no cache"
                )
            }

            CacheMode.CACHE_NET_DISTINCT -> {
                /* 先使用缓存，不管是否存在，仍然请求网络，会先把缓存回调给你，
                 * 络请求回来发现数据是一样的就不会再返回，否则再返回
                 */
                val cacheInfo = withContext(DispatcherProvider.io()) {
                    HttpKit.getKCCache().load(requestOption.cacheKey, requestOption.cacheTime)
                }
                val apiInfo = fromNetworkValue()

                if (cacheInfo != apiInfo) {
                    saveCache(requestOption, apiInfo)
                    apiInfo
                } else {
                    throw CacheException(OK_CACHE_EXCEPTION, "the same data,so not update")
                }
            }

            else -> {
                fromNetworkValue().also {
                    saveCache(requestOption, it)
                }
            }
        }
    }

    @JvmOverloads
    suspend fun download(
        url: String,
        outputFile: String,
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
                var emitProgress = 0f

                val bufferSize = 1024 * 8
                val buffer = ByteArray(bufferSize)
                val bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
                var readLength: Int
                while (bufferedInputStream.read(buffer, 0, bufferSize)
                        .also { readLength = it } != -1
                ) {
                    outputStream.write(buffer, 0, readLength)
                    currentLength += readLength
                    val progress = currentLength.toFloat() / contentLength.toFloat()
                    // 每次超过%1才进行更新
                    if (progress - emitProgress >= 0.01) {
                        emitProgress = progress
                        emit(
                            HttpResult.progress(
                                currentLength.toLong(),
                                contentLength,
                                emitProgress
                            )
                        )
                    }
                }
                bufferedInputStream.close()
                outputStream.close()
                inputStream.close()
                emit(HttpResult.success(file))
            } catch (e: Exception) {
                emit(HttpResult.failure<File>(ApiException.handleException(e)))
            }
        }.flowOn(DispatcherProvider.io())
            .buffer()
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

    /**
     * 重试逻辑
     * @param repeatNum 重试次数
     * @param initialDelay 重试延迟,每次重试延迟多【次数*[factor]】秒,
     * @param timeout 协程的超时处理，这里超过retrofit的超时就意义不大了
     * @param factor 用处理重试延迟的样本
     */
    suspend fun retryRequest(
        repeatNum: Int = 3,
        initialDelay: Long = 1000, // 1 second
        timeout: Long = 10000, // 10 second
        factor: Double = 2.0,
        block: suspend () -> String
    ): String {
        if (repeatNum <= 0) return block()
        var currentDelay = initialDelay
        repeat(repeatNum - 1) {
            try {
                return requestWithTimeout(timeout, block)
            } catch (e: Exception) {
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(timeout)
            }
        }
        try {
            return requestWithTimeout(timeout, block)
        } catch (e: TimeoutCancellationException) {
            throw ApiException(e, TIMEOUT_ERROR).apply {
                this.setDisplayMessage("coroutines Timeout Cancel, Timeout $timeout, maybe has more time ")
            }
        } catch (e: Exception) {
            throw ApiException.handleException(e)
        }
    }

    /**
     * 自定义超时处理
     */
    private suspend fun requestWithTimeout(
        timeout: Long,
        block: suspend () -> String
    ): String {
        return if (timeout <= 0L) {
            block()
        } else {
            withTimeout(timeout) {
                block()
            }
        }
    }

    /**
     * use by [me.shetj.base.netcoroutine.KCHttpV4]
     */
    suspend fun saveCache(requestOption: RequestOption?, data: String) {
        if (!requestOption?.cacheKey.isNullOrBlank()) {
            withContext(DispatcherProvider.io()) {
                HttpKit.getKCCache().save(requestOption?.cacheKey, data)
            }
        }
    }
}

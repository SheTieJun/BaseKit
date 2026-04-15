package me.shetj.base.network.interceptor

import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject

/**
 * 统一参数修改拦截器基类
 * 适用于底层库，允许外部业务方继承并实现自己的参数追加逻辑
 */
open class ParameterInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 根据请求方法分发处理逻辑
        val newRequest = when (originalRequest.method) {
            "GET" -> modifyGetRequest(originalRequest)
            "POST" -> modifyPostRequest(originalRequest)
            // 可根据需要处理 PUT, PATCH 等
            else -> originalRequest
        }

        return chain.proceed(newRequest)
    }

    /**
     * 子类重写此方法，用于向 GET 请求追加 Query 参数
     */
    protected open fun addGetParams(builder: HttpUrl.Builder) {
        // Example: builder.addQueryParameter("common_param", "value")
    }

    /**
     * 子类重写此方法，用于向 POST 表单追加参数
     */
    protected open fun addPostFormParams(builder: FormBody.Builder) {
        // Example: builder.add("common_param", "value")
    }

    /**
     * 子类重写此方法，用于向 POST JSON 追加参数
     */
    protected open fun addPostJsonParams(jsonObject: JSONObject) {
        // Example: jsonObject.put("common_param", "value")
    }

    /**
     * 判断某个 MediaType 是否允许被作为 JSON 读取和修改。
     * 默认仅拦截 application/json。子类可重写以支持更多文本类型的 JSON 格式。
     */
    protected open fun isSupportedJsonType(mediaType: String?): Boolean {
        return mediaType?.contains("application/json", ignoreCase = true) == true
    }

    /**
     * 修改 GET 请求：通过 HttpUrl.Builder 动态追加查询参数
     */
    private fun modifyGetRequest(request: Request): Request {
        val urlBuilder = request.url.newBuilder()
        
        // 留给子类实现参数追加
        addGetParams(urlBuilder)

        return request.newBuilder()
            .url(urlBuilder.build())
            .build()
    }

    /**
     * 修改 POST 请求：根据不同的 Body 类型分别处理
     */
    private fun modifyPostRequest(request: Request): Request {
        // 如果没有请求体，无需处理
        val body = request.body ?: return request

        return when (body) {
            is FormBody -> modifyFormBody(request, body)
            else -> modifyJsonBody(request, body)
        }
    }

    /**
     * 修改表单类型的 POST 请求
     */
    private fun modifyFormBody(request: Request, body: FormBody): Request {
        val formBuilder = FormBody.Builder()

        // 遍历并复制原有的表单参数
        for (i in 0 until body.size) {
            formBuilder.add(body.name(i), body.value(i))
        }

        // 留给子类实现参数追加
        addPostFormParams(formBuilder)

        return request.newBuilder()
            .post(formBuilder.build())
            .build()
    }

    /**
     * 修改 JSON 类型的 POST 请求
     */
    private fun modifyJsonBody(request: Request, body: RequestBody): Request {
        val mediaType = body.contentType()
        
        // 安全检查：避免读取文件上传等大体积二进制流导致 OOM
        if (!isSupportedJsonType(mediaType?.toString())) {
            return request
        }

        return try {
            // 使用 Okio 的 Buffer 安全地读取现有 Body 的内容
            val buffer = Buffer()
            body.writeTo(buffer)
            val originalJsonString = buffer.readUtf8()

            // 解析原有 JSON 并注入新参数
            val jsonObject = if (originalJsonString.isNotEmpty()) {
                JSONObject(originalJsonString)
            } else {
                JSONObject()
            }

            // 留给子类实现参数追加
            addPostJsonParams(jsonObject)

            // 将修改后的 JSON 重新转为 RequestBody
            val modifiedBody = jsonObject.toString().toRequestBody(mediaType)

            request.newBuilder()
                .post(modifiedBody)
                .build()
                
        } catch (e: Exception) {
            // 容错处理：如果 JSON 解析或读取异常，回退使用原始请求
            e.printStackTrace()
            request
        }
    }
}

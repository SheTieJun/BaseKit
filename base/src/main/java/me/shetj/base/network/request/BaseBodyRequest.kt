package me.shetj.base.network.request

import io.reactivex.Observable
import me.shetj.base.network.body.RequestBodyUtils
import me.shetj.base.network.body.UploadProgressRequestBody
import me.shetj.base.network.callBack.ProgressResponseCallBack
import me.shetj.base.network.kt.toRequestBody
import me.shetj.base.network.model.HttpParams
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import java.io.File
import java.io.InputStream
import java.util.*

@Suppress("UNCHECKED_CAST")
abstract class BaseBodyRequest<R : BaseBodyRequest<R>>(url: String) : BaseRequest<R>(url) {

    protected var string: String? = null //文本类型
    protected var mediaType: MediaType? = null
    protected var json: String? = null//json
    protected var obj: Any? = null //上传对象
    protected var bs: ByteArray? = null //上传字节数据
    protected var request: RequestBody? = null //自定义请求体

    enum class UploadType {
        PART,
        BODY
    }

    private val currentUploadType = UploadType.PART

    open fun requestBody(requestBody: RequestBody): R {
        this.request = requestBody
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    open fun upString(string: String?): R {
        this.string = string
        mediaType = MediaType.parse("text/plain")
        return this as R
    }

    open fun upString(string: String?, mediaType: String?): R {
        this.string = string
        this.mediaType = MediaType.parse(checkNotNull(mediaType, { "mediaType==null" }))
        return this as R
    }

    open fun upObject(@Body `object`: Any): R {
        obj = `object`
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    open fun upJson(json: String?): R {
        this.json = json
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    open fun upBytes(bs: ByteArray?): R {
        this.bs = bs
        return this as R
    }

    open fun params(key: String?, file: File?, responseCallBack: ProgressResponseCallBack?): R {
        params.put(key, file, responseCallBack)
        return this as R
    }

    open fun params(key: String?, stream: InputStream?, fileName: String?, responseCallBack: ProgressResponseCallBack?): R {
        params.put(key, stream, fileName!!, responseCallBack)
        return this as R
    }

    open fun params(key: String?, bytes: ByteArray?, fileName: String?, responseCallBack: ProgressResponseCallBack?): R {
        params.put(key, bytes!!, fileName!!, responseCallBack)
        return this as R
    }

    open fun addFileParams(key: String?, files: List<File?>?, responseCallBack: ProgressResponseCallBack?): R {
        params.putFileParams(key, files, responseCallBack)
        return this as R
    }

    open fun addFileWrapperParams(key: String?, fileWrappers: List<HttpParams.FileWrapper<*>>?): R {
        params.putFileWrapperParams(key, fileWrappers)
        return this as R
    }

    open fun params(key: String?, file: File?, fileName: String?, responseCallBack: ProgressResponseCallBack?): R {
        params.put(key, file, fileName!!, responseCallBack)
        return this as R
    }

    open fun <T> params(key: String?, file: T, fileName: String?, contentType: MediaType?, responseCallBack: ProgressResponseCallBack?): R {
        params.put(key, file, fileName!!, contentType, responseCallBack)
        return this as R
    }

    override fun generateRequest(): Observable<ResponseBody> ?{
        return when {
            this.request != null -> {
                apiManager!!.postBody(this.url, this.request)
            }
            this.json != null -> {
                apiManager!!.postJson(this.url, this.json!!.toRequestBody())
            }
            this.obj != null -> {
                apiManager!!.postBody(this.url, this.obj)
            }
            this.string != null -> {
                apiManager!!.postBody(this.url, this.string!!.toRequestBody(mediaType))
            }
            this.bs != null -> {
                apiManager!!.postBody(this.url, this.bs!!.toRequestBody())
            }
            params.fileParamsMap!!.isEmpty() -> {
                apiManager!!.post(url, params.urlParamsMap!!.toMap())
            }
            else -> {
                return if (currentUploadType == UploadType.PART) { //part方式上传
                    uploadFilesWithParts()
                } else { //body方式上传
                    uploadFilesWithBodys()
                }
            }
        }


    }

    protected open fun uploadFilesWithParts(): Observable<ResponseBody> {
        val parts: MutableList<MultipartBody.Part> = ArrayList()
        //拼接参数键值对
        params.urlParamsMap?.forEach {
            parts.add(MultipartBody.Part.createFormData(it.key, it.value))
        }
        //拼接文件
        params.fileParamsMap?.forEach { it1 ->
            it1.value.forEach { it2 ->
                val part: MultipartBody.Part = addFile(it1.key, it2)
                parts.add(part)
            }
        }
        return apiManager!!.uploadFiles(url, parts)
    }

    protected open fun uploadFilesWithBodys(): Observable<ResponseBody> {
        val mBodyMap: MutableMap<String, RequestBody> = HashMap()
        //拼接参数键值对
        params.urlParamsMap?.forEach {
            val body = RequestBody.create(MediaType.parse("text/plain"), it.value)
            mBodyMap[it.key] = body
        }
        //拼接文件
        params.fileParamsMap?.forEach { it1 ->
            it1.value.forEach { it2 ->
                val requestBody: RequestBody? = getRequestBody(it2)
                val uploadProgressRequestBody = UploadProgressRequestBody(requestBody, it2.responseCallBack)
                mBodyMap[it1.key] = uploadProgressRequestBody
            }
        }

        return apiManager!!.uploadFiles(url, mBodyMap)
    }

    //文件方式
    open fun addFile(key: String, fileWrapper: HttpParams.FileWrapper<*>): MultipartBody.Part {
        //MediaType.parse("application/octet-stream", file)
        val requestBody = getRequestBody(fileWrapper)
        checkNotNull(requestBody, { "requestBody==null fileWrapper.file must is File/InputStream/byte[]" })
        //包装RequestBody，在其内部实现上传进度监听
        return if (fileWrapper.responseCallBack != null) {
            val uploadProgressRequestBody = UploadProgressRequestBody(requestBody, fileWrapper.responseCallBack)
            MultipartBody.Part.createFormData(key, fileWrapper.fileName, uploadProgressRequestBody)
        } else {
            MultipartBody.Part.createFormData(key, fileWrapper.fileName, requestBody)
        }
    }

    open fun getRequestBody(fileWrapper: HttpParams.FileWrapper<*>): RequestBody? {
        return when (fileWrapper.file) {
            is File -> {
                RequestBody.create(fileWrapper.contentType, fileWrapper.file as File)
            }
            is InputStream -> {
                RequestBodyUtils.create(fileWrapper.contentType, fileWrapper.file as InputStream)
            }
            is ByteArray -> {
                RequestBody.create(fileWrapper.contentType, fileWrapper.file as ByteArray)
            }
            else -> null
        }
    }
}
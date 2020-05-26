package me.shetj.base.network.func

import android.text.TextUtils
import io.reactivex.functions.Function
import me.shetj.base.network.kt.ClassUtils
import me.shetj.base.network.model.ApiResult
import me.shetj.base.tools.app.TimberUtil
import me.shetj.base.tools.json.GsonKit.jsonToBean
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * 一、当是自定义[me.shetj.base.network.model.ApiResult]
 *   1.如果是String 不处理，默认把所有数据返回  ；
 *   2.如果不是String,返回 T,去掉code and msg  ；
 * 二、当是不是[me.shetj.base.network.model.ApiResult]
 *   1.普通的class
 *   2.自定义泛型处理
 */
@Suppress("UNCHECKED_CAST")
class ApiResultFunc<T>(val type: Type) : Function<ResponseBody, ApiResult<T>> {

    override fun apply(responseBody: ResponseBody): ApiResult<T> {
        var apiResult: ApiResult<T> = ApiResult<T>().apply {
            code = -1
        }
        if (type is ParameterizedType) { //class me.shetj.base.network.model.ApiResult<T> 是泛型
            val cls = (type.rawType as Class<*>) //me.shetj.base.network.model.ApiResult
            //isAssignableFrom()方法是从类继承的角度去判断，instanceof关键字是从实例继承的角度去判断。
            //isAssignableFrom()方法是判断是否为某个类的父类，instanceof关键字是判断是否某个类的子类。
            //父类.class.isAssignableFrom(子类.class)
            if (ApiResult::class.java.isAssignableFrom(cls)) {
//                val typeArguments = (type  as ParameterizedType ).actualTypeArguments //具有<T>符号的变量是参数化类型 ：data 的类型
//                val clazz = ClassUtils.getClass(typeArguments[0], 0) // 获取T 的类型
                val rawType = ClassUtils.getClass(type, 0) //获取T的类型
                try {
                    val json = responseBody.string()

                    if (!List::class.java.isAssignableFrom(rawType) && rawType == String::class.java) {
                        val parseApiResult = parseApiResult(json, apiResult)
                        if (parseApiResult == null) {
                            apiResult.msg = "responseBody is null,can't be json"
                        }
                        apiResult.code = 0
                        apiResult.data = json as T
                    } else {
                        val bean = jsonToBean<ApiResult<T>>(json, type)
                        if (bean != null) {
                            apiResult = bean
                        } else {
                            apiResult.msg = "responseBody is not json"
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    apiResult.msg = e.message
                } finally {
                    responseBody.close()
                }
            } else {
                //自定义的泛型处理
                try {
                    val json = responseBody.string()
                    apiResult.code = 0
                    val bean: T? = jsonToBean<T>(json, type)
                    apiResult.data = bean
                } catch (e: JSONException) {
                    e.printStackTrace()
                    apiResult.msg = (e.message)
                } catch (e: IOException) {
                    e.printStackTrace()
                    apiResult.msg = (e.message)
                } finally {
                    responseBody.close()
                }
            }
        } else {
            //如果是自定义类型
            try {
                val json = responseBody.string()
                val clazz = ClassUtils.getClass(type, 0) //获取顶层的类型
                //默认是成成功了，如果解析异常就是表示获取失败
                apiResult.code = 0
                if (clazz != String::class.java) {
                    val bean: T? = jsonToBean<T>(json, type)
                    apiResult.data = bean
                } else if (clazz == String::class.java) {
                    apiResult.data = json as T
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                apiResult.msg = (e.message)
            } catch (e: IOException) {
                e.printStackTrace()
                apiResult.msg = (e.message)
            } finally {
                responseBody.close()
            }
        }
        return apiResult
    }

    @Throws(JSONException::class)
    private fun parseApiResult(json: String, apiResult: ApiResult<T>): ApiResult<T>? {
        if (TextUtils.isEmpty(json)) return null
        val jsonObject = JSONObject(json)
        if (jsonObject.has("code")) {
            apiResult.code = (jsonObject.getInt("code"))
        }
        if (jsonObject.has("msg")) {
            apiResult.msg = (jsonObject.getString("msg"))
        }
        return apiResult
    }
}
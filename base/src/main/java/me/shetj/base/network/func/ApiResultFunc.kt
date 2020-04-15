package me.shetj.base.network.func

import io.reactivex.functions.Function
import me.shetj.base.network.model.ApiResult
import me.shetj.base.tools.json.GsonKit
import okhttp3.ResponseBody
import java.lang.reflect.Type


class ApiResultFunc<T>(val type: Type) : Function<ResponseBody, ApiResult<T>> {

    override fun apply(responseBody: ResponseBody): ApiResult<T> {
        var apiResult: ApiResult<T> = ApiResult<T>().apply {
            code = -1
        }
        val json = responseBody.string()
        val bean = GsonKit.jsonToBean<ApiResult<T>>(json, type)

        if (bean != null) {
            apiResult = bean
        } else {
            apiResult
        }

//        if (type is ParameterizedType ){
//            val cls = (type.rawType as Class<*>)
//            //isAssignableFrom()方法是从类继承的角度去判断，instanceof关键字是从实例继承的角度去判断。
//            //isAssignableFrom()方法是判断是否为某个类的父类，instanceof关键字是判断是否某个类的子类。
//            //父类.class.isAssignableFrom(子类.class)
//            if (ApiResult::class.java.isAssignableFrom(cls)){
//                val typeArguments = type.actualTypeArguments //具有<T>符号的变量是参数化类型 ：data 的类型
//                val clazz = ClassUtils.getClass(typeArguments[0], 0) // 获取T 的类型
//                val rawType = ClassUtils.getClass(type, 0) //获取T的类型
//                try {
//                    val json = responseBody.string()
//                    if (!List::class.java.isAssignableFrom(rawType) && clazz == String::class.java){
//                        apiResult.code = 0
//                        apiResult.data = json as T
//                    }else{
//                        val bean = jsonToBean<ApiResult<T>>(json, type)
//                        if (bean != null){
//                            apiResult = bean
//                        }else{
//                            apiResult.msg = "responseBody is not json"
//                        }
//                    }
//                }catch (e:Exception){
//                    e.printStackTrace()
//                    apiResult.msg = e.message
//                }finally {
//                    responseBody.close()
//                }
//            }else{
//                apiResult.msg = "ApiResult.class.isAssignableFrom(cls) err!"
//            }
//        }else{
//            try {
//                val json = responseBody.string()
//                val parseApiResult = parseApiResult(json, apiResult)
//                if (parseApiResult ==null){
//                    apiResult.msg = "responseBody is null,can't be json"
//                }
//                val clazz = ClassUtils.getClass(type, 0)
//                if (clazz != String::class.java){
//                    if (parseApiResult !=null) {
//                        if (apiResult.data != null) {
//                            val bean = jsonToBean(apiResult.data.toString(), clazz)
//                            apiResult.data = bean as T
//                        } else {
//                            apiResult.msg = "apiResult is null"
//                        }
//                    }
//                }
//            }catch (e:Exception){
//
//            }finally {
//
//
//            }
//
//        }
//        val adapter: TypeAdapter<T> = GsonKit.getGson().getAdapter(TypeToken.get(type)) as TypeAdapter<T>
//        val jsonReader: JsonReader = GsonKit.getGson().newJsonReader(responseBody.charStream())
//        val use = responseBody.use { responseBody ->
//            val result = adapter.read(jsonReader)
//            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
//                throw JsonIOException("JSON document was not fully consumed.")
//            }
//            result
//        }
        return apiResult
    }
}
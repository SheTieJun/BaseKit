package me.shetj.base.base



/**
 * 配合liveData对数据处理
 */
sealed class DataState<T>

//正在请求接口数据
object Loading : DataState<Any>()

//完成：接口正确,但是数据为空
object Complete : DataState<Any>()

//完成：结果返回
class ResultData<T>(val data:T): DataState<T>()

//完成: 接口错误
class Error<T>(val msg: String?) : DataState<T>()

//完成：分页加载
class PageData<T>(val data: MutableList<T>,val isFirst: Boolean = true,val finish:Boolean = false) : DataState<T>()


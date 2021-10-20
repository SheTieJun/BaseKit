package me.shetj.base.base



/**
 * 配合liveData对数据处理
 */
sealed class DataState<T>

//完成：结果返回
data class ResultData<T>(val data:T): DataState<T>()

//完成：分页加载
data class PageData<T>(val data: MutableList<T>,val isFirst: Boolean = true,val finish:Boolean = false) : DataState<T>()


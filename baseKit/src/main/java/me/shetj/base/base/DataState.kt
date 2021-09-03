package me.shetj.base.base

import androidx.lifecycle.liveData


/**
 * 配合liveData对数据处理
 */
sealed class DataState<T>


//分页加载
class PageData<T>(val data: MutableList<T>,val isFirst: Boolean = true,val finish:Boolean = false) : DataState<T>()

//完成：结果返回
class ResultData<T>(val data:T): DataState<T>()

//完成：接口正确,但是数据为空
class Complete<T> : DataState<T>()

//完成: 接口错误
class Error<T>(val msg: String?) : DataState<T>()
package me.shetj.base.base


/**
 * 配合liveData对数据处理
 */
sealed class BaseState<T> {

    //分页加载
    class PageData<T>(val isFirst: Boolean = false, val data: MutableList<T>,val finish:Boolean = false) : BaseState<T>()

    //结果返回
    class EndData<T>(val data:T): BaseState<T>()

    //接口正确,但是数据为空
    class EndEmpty<T> : BaseState<T>()

    //接口错误
    class ERROR<T>(val msg: String?) : BaseState<T>()

}
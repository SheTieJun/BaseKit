package me.shetj.base.model

/**
 * 配合liveData对数据处理
 */
sealed class ResultData<T> {
    // 完成：分页加载
    data class PageData<T>(val data: MutableList<T>, val isFirst: Boolean = true, val finish: Boolean = false) :
        ResultData<T>()

    // 模型数据
    data class ModelData<T>(val data: T) : ResultData<T>()

    /**
     * 没有数据
     */
    object EmptyData : ResultData<Any>()
}

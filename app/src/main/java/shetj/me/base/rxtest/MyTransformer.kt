package shetj.me.base.rxtest

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableTransformer
import org.reactivestreams.Publisher


/**
 * 自定义Transformer表示一个批量操作符的变换器，
 * 如果你在很多Observable中使用相同的一系列操作符，
 * 可以抽离出来
 */
class MyTransformer : FlowableTransformer<String,Int> {
    override fun apply(upstream: Flowable<String>?): Publisher<Int> {
        return upstream?.map { 1024 }!!
    }
}
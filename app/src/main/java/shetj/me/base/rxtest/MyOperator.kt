package shetj.me.base.rxtest

import io.reactivex.rxjava3.core.FlowableOperator
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription


/**
 * 自定义操作
 */
class MyOperator: FlowableOperator<String,String> {

    override fun apply(subscriber: Subscriber<in String>?): Subscriber<in String> {

        return object :Subscriber<String>{

            override fun onSubscribe(s: Subscription?) {
                s?.request(System.currentTimeMillis())  //必须执行这个才可以执行
            }

            override fun onNext(t: String?) {
                subscriber?.onNext("$t : MyOperator")
            }

            override fun onError(t: Throwable?) {
                subscriber?.onError(t)
            }

            override fun onComplete() {
                subscriber?.onComplete()
            }
        }
    }
}
### [自定义操作符](https://juejin.im/post/6844903842698493960)

lift()是对原始Observable的事件序列的每一项做变换，  
而compose()是将原始Observable自身进行变换成另一个Observable。

自定义Operator
自定义操作

自定义Transformer

自定义Plugin
```
 RxJavaPlugins.setOnObservableAssembly(new CustomObservableAssembly());//任意操作符都有回调
        RxJavaPlugins.setOnObservableSubscribe(new CustomObservableSubscribe());//每次subscribe时候有回调

```

### Subject
Subject作用是很大的，借用官方的解释，Subject在同一时间内，既可以作为Observable，也可以作为Observer:

ReplaySubject :
> 该Subject会接收数据，当被订阅时，将所有接收到的数据全部发送给订阅者。

BehaviorSubject
> 订阅后，会发送订阅前最后一个参数（包括异常），后之发送之后接受的参数。

AsyncSubject
> 订阅后，AsyncSubject仅在调用onComplete时发出，发送最后一个参数

CompletableSubject
> 直接完成

MaybeSubject
> MaybeSubject所有方法都是线程安全的，用于验证结果

PublishSubject
> 订阅后，发送之后接受到的参数

SerializedSubject
> 串行，保证线程按钮，其他普通的subject 不要在多线程使用

SingleSubject
> SingleSubject 线程安全，用于验证结果

UnicastSubject
> 只允许一个 Observer 进行监听
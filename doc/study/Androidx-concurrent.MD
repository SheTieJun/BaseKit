## Androidx-concurrent 

代码示例：

```Kotlin


val future = CallbackToFutureAdapter.getFuture { completer: Completer<String> ->
    //任务开始 异步任务
    completer.set("任务完成")
    //completer.setException(RuntimeException("任务失败"))
    completer.addCancellationListener(Runnable {
        //任务取消
    }, MoreExecutors.directExecutor())

}
//使用协程
runBlocking {
    future.await()
}

```
## JankStats跟踪和分析应用的性能问题

## 集成
```groovy
implementation "androidx.metrics:metrics-performance:1.0.0-alpha03"
```

```kotlin
     // metrics state holder can be retrieved regardless of JankStats initialization
        val metricsStateHolder = PerformanceMetricsState.getHolderForHierarchy(binding.root)

        // initialize JankStats for current window
        jankStats = JankStats.createAndTrack(window, jankFrameListener)

        // add activity name as state
        metricsStateHolder.state?.putState("Activity", javaClass.simpleName)
```

- JankStats.createAndTrack 方法接受对 Window 对象的引用，该对象是该 Window 内的视图层次结构的代理，也是 Window 本身的代理。jankFrameListener 会在内部用于从平台向 JankStats 传输信息的同一线程上调用。
- 对任何 JankStats 对象启用跟踪和报告功能，请调用 isTrackingEnabled = true
- JankStats 库会将每一帧的所有跟踪数据报告给已启用的 JankStats 对象的 OnFrameListener。
- 监听器通过 FrameData 对象提供有关卡顿的每帧信息。其中包含有关所请求帧的以下信息：
  - `isjank`：布尔值标志，用于指示帧中是否存在卡顿。
  - `frameDurationUiNanos`：帧的时长（以纳秒为单位）。
  - `frameStartNanos`：帧开始的时间（以纳秒为单位）。
  - `states`：应用在帧期间的状态。
  - FrameDataApi24 提供的 `frameDurationCpuNanos` 可显示帧的非 GPU 部分的所用时间。
  - FrameDataApi31 提供的 `frameOverrunNanos` 可显示帧的完成时间超出其截止时间的用时量。
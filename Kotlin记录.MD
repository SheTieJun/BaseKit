##### 修饰词 

`internal` // 在同一module中可见

`private `只在该类(以及它的成员)中可见

`protected` 和 `private `一样但在子类中也可见

`internal` 在本模块的所有可以访问到声明区域的均可以访问该类的所有 internal 成员 ( internal — any client inside this module who sees the declaring class sees its internal members;)

`public` 任何地方可见 (public — any client who sees the declaring class sees its public members.)+

##### 特性
1. 函数扩展 
2. 默认参数
3. Coroutine 协程异步 
```  
kotlin {
    experimental {
        coroutines 'enable'
    }
}
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:0.20'
 implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:0.20'
 ```
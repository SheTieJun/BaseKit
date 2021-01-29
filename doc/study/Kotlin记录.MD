##### 修饰词 

`internal` // 在同一module中可见

`private `只在该类(以及它的成员)中可见

`protected` 和 `private `一样但在子类中也可见

`internal` 在本模块的所有可以访问到声明区域的均可以访问该类的所有 internal 成员 ( internal — any client inside this module who sees the declaring class sees its internal members;)

`public` 任何地方可见 (public — any client who sees the declaring class sees its public members.)+

`inline` 内联函数 copy 而不是对象

`crossinline`  不允许内部直接返回

`infix` 中缀函数

`open `  标记可以继承，重载，重写 ；默认是不可以

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
 
##### 标记
`combine ` 

- `public ` 表示公开同Java 任何客户端都可见其 public 成员
- `protected` 同Java ，在子类中可见
- `private`  表私有,同Java 这个类内部（包含其所有成员）可见
- `internal` 表在"相同模块"内随处可见
- `expect ` 表示将一个声明标记为平台相关，并期待在平台模块中实现。
- `actual` 表示多平台项目中的一个平台相关实现
- `final ` 表final类
- `open `  表可以继承，重载，重写
- `abstract `表抽象类
- `sealed ` 表密封类 ：密封类是不能被实例化的，
    - 其子类可以定在密封类外部，但是必须在同一文件中 
    - 感觉可以用来实现枚举的功能，而且比枚举更优秀
- `const` 表示将属性标记为编译期常量
- `external`  表示将一个声明标记为不是在 Kotlin 中实现（通过 JNI 访问或者在 JavaScript 中实现）
- `override` 表示重写，将一个成员标记为超类成员的覆盖
- `lateinit` 表示允许在构造函数之外初始化非空属性
- `tailrec` 将一个函数标记为尾递归（允许编译器将递归替换为迭代）
- `vararg`  允许一个参数传入可变数量的参数
- `suspend` 将一个函数或 lambda 表达式标记为挂起式（可用做协程）
- `inner` 表内部类
    -  inner 以便能够访问外部类的成员。内部类会带有一个对外部类的对象的引用
- `enum ` 表枚举
    - 每一个枚举常量就是一个对象，用逗号分开
    - name（枚举常量名）和ordinal（枚举常量位置）
    - 可以使用 enumValues<T>()和 enumValueOf<T>()函数以泛型的方式访问枚举类中的常量。
        - println(enumValues<Color>().joinToString { it.name })    println(enumValueOf<Color>("RED"))
- `interface` 表接口类
    - 不带结构体的函数可以省略大括号，且不用强制重写带结构体的函数就可以直接调用。
- `annotation` 声明一个注解类
- `companion` 声明一个伴生对象
- `inline` 告诉编译器在调用处内联传给它的函数和 lambda 表达式 (内联才是copy ,否则是生成一个对象)
  - crossinline 不允许内部直接返回
- `infix` 允许以中缀表示法调用函数
- `operator` 将一个函数标记为重载一个操作符或者实现一个约定
- `data` 表数据类
    - 生成equals()函数与hasCode()函数
    - 生成toString()函数，由类名（参数1 = 值1，参数2 = 值2，....）构成
    - 由所定义的属性自动生成component1()、component2()、...、componentN()函数，其对应于属性的声明顺序。
    - copy()函数。 
- `typealias`  类型别名


DSL 特定领域语言：lambda \扩展函数\顶层函数\invoke约定



SupervisorJob() 便于协程的故障不会再层级间传播

[迁移Kotlin Android Extensions](https://blog.csdn.net/qq_17766199/article/details/109557820)


job 中的IO,都是会被阻塞的

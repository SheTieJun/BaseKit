package shetj.me.base.annotation

import android.util.Log


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Debug(
    val level: Int = Log.ERROR,
    /**
     * @return 打印方法的运行时间
     */
    val enableTime: Boolean = false,
    /**
     * @return tag的名称，默认是类名，也可以设置
     */
    val tagName: String = "",
    /**
     * @return 是否观察field的值，如果观察就会就拿到对象里面全部的field值
     */
    val watchField: Boolean = false,
    /**
     *
     * @return 是否观察方法的调用栈
     */
    val watchStack: Boolean = false
)

package me.shetj.annotation.apt

/**
 * 专门用来空实现接口
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@kotlin.annotation.Target(AnnotationTarget.CLASS,AnnotationTarget.TYPE)
annotation class SimpleImpl
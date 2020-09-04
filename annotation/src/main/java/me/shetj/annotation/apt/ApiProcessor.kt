package me.shetj.annotation.apt


/**
 * 生成的文件名称：标记的class name + [name]
 */
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class ApiProcessor(val name: String = "Factory")


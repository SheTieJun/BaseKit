package me.shetj.annotation.apt


@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class ApiProcessor(val name: String = "Impl")


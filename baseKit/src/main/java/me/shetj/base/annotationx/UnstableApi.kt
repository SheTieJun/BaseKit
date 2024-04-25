package me.shetj.base.annotationx


/**
 * Test api
 * 用来表示是测试api
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
annotation class TestApi
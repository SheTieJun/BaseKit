package me.shetj.compilers

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName


object ClassNameUtils {
    val VIEW = ClassName("android.view", "View")
    val CONTEXT = ClassName("android.content", "Context")
    val RESOURCES = ClassName("android.content.res", "Resources")
    val UI_THREAD = ClassName("androidx.annotation", "UiThread")
    val CALL_SUPER = ClassName("androidx.annotation", "CallSuper")
    val SUPPRESS_LINT = ClassName("android.annotation", "SuppressLint")
    val BITMAP_FACTORY = ClassName("android.graphics", "BitmapFactory")
    val CONTEXT_COMPAT = ClassName("androidx.core.content", "ContextCompat")
    val ANIMATION_UTILS = ClassName("android.view.animation", "AnimationUtils")
    val SCHEDULERS = ClassName("io.reactivex.rxjava3.schedulers", "Schedulers")
    val ANDROIDSCHEDULERS =
        ClassName("io.reactivex.rxjava3.android.schedulers", "AndroidSchedulers")
    val RxUtil =
            ClassName("me.shetj.base.network.kt", "RxUtil")
    val RxHttp =
            ClassName("me.shetj.base.network", "RxHttp")

}

@Suppress("DEPRECATION")
fun Element.javaToKotlinType(): TypeName =
    asType().asTypeName().javaToKotlinType()

fun TypeName.javaToKotlinType(): TypeName = if (this is ParameterizedTypeName) {
    (rawType.javaToKotlinType() as ClassName).parameterizedBy(
        *typeArguments.map { it.javaToKotlinType() }.toTypedArray()
    )
} else {
    val className = JavaToKotlinClassMap.INSTANCE
        .mapJavaToKotlin(FqName(toString()))?.asSingleFqName()?.asString()
    if (className == null) this
    else ClassName.bestGuess(className)
}

package me.shetj.compilers

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

open class SimpleImplProxy(
        private val elementUtils: Elements,
        private val classElement: TypeElement,
        private val name: String
) {


    private fun getPageName() = elementUtils.getPackageOf(classElement).qualifiedName.toString()

    fun buildTo(): FileSpec {
        val funList = ArrayList<FunSpec>()
        val enclosedElements = classElement.enclosedElements

        enclosedElements.forEach { executableElement ->
            if (executableElement is ExecutableElement) {
                val methodName = executableElement.simpleName.toString()
                executableElement.parameters
                val func = FunSpec.builder(methodName)
                        .addModifiers(KModifier.OVERRIDE)
                        .apply {
                            executableElement.parameters.forEach { vElement ->
                                this.addParameter(
                                        vElement.simpleName.toString(),
                                        vElement.javaToKotlinType()
                                )
                            }
                        }
                        .build()
                funList.add(func)
            }
        }
        val realName = classElement.simpleName.toString() + name

        return FileSpec.builder(getPageName(), realName)
                .addType(
                        TypeSpec.classBuilder(realName)
                                .addKdoc("专门用来空实现接口")
                                .addSuperinterface(classElement.javaToKotlinType())
                                .apply {
                                    funList.forEach {
                                        addFunction(it)
                                    }
                                }
                                .build()
                ).build()
    }


    fun String.getFixName(): String {
        return when (this) {
            "object" -> "`$this`"
            else -> this
        }
    }
}

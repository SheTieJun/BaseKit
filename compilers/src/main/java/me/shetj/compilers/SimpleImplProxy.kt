package me.shetj.compilers

import com.squareup.kotlinpoet.*
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


        val typeParameters = classElement.typeParameters


        return FileSpec.builder(getPageName(), realName)
                .addType(
                        TypeSpec.classBuilder(realName)
                                .apply {
                                    if (!typeParameters.isNullOrEmpty()){
                                        typeParameters.forEach {
                                            addTypeVariable(TypeVariableName(it.simpleName.toString()))
                                        }
                                    }
                                }
                                .addKdoc("专门用来空实现接口,默认实现方法暂时不支持返回具体类型")
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

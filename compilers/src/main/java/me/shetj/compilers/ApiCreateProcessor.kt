package me.shetj.compilers

import com.google.auto.service.AutoService
import me.shetj.annotation.apt.ApiProcessor
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement


@SupportedAnnotationTypes(value = ["me.shetj.annotation.apt.ApiProcessor"]) // //得到注解处理器可以支持的注解类型
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class ApiCreateProcessor : AbstractProcessor() {

    private val kindMap = HashMap<String, FactoryCreatorProxy>()

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)

    }

    override fun process(p0: MutableSet<out TypeElement>, rEv: RoundEnvironment): Boolean {
        val elements = rEv.getElementsAnnotatedWith(ApiProcessor::class.java)
        if (elements.isNullOrEmpty()) return true
        elements.forEach {
            //必须是给接口用
                val classElement = it as TypeElement
                val fullClassName: String = classElement.qualifiedName.toString()
                var proxy: FactoryCreatorProxy? = kindMap[fullClassName]
                if (proxy == null) {
                    val annotation: ApiProcessor = it.getAnnotation(
                            ApiProcessor::class.java
                    )
                    proxy = FactoryCreatorProxy(
                        processingEnv.elementUtils,
                        classElement,
                        annotation.name
                    )
                    kindMap[fullClassName] = proxy
                }
        }
        kindMap.values.forEach { proxy ->
            try {
                proxy.buildTo().apply {
                writeTo(processingEnv.filer)
                }
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
        return true
    }
}
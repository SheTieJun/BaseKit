package me.shetj.compilers

import com.google.auto.service.AutoService
import me.shetj.annotation.apt.ApiProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.io.IOException
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import kotlin.collections.HashMap


@SupportedAnnotationTypes(value = ["me.shetj.annotation.apt.ApiProcessor"]) // //得到注解处理器可以支持的注解类型
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.DYNAMIC) //增量更新
@AutoService(Processor::class)
class ApiCreateProcessor : AbstractProcessor() {

    private val kindMap = HashMap<String, FactoryCreatorProxy>()

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)

    }

    //返回注解处理器可处理的注解操作
    override
    fun getSupportedOptions(): Set<String> {
        return Collections.singleton(IncrementalAnnotationProcessorType.ISOLATING.processorOption);
    }

    override fun process(p0: MutableSet<out TypeElement>, rEv: RoundEnvironment): Boolean {
        val elements = rEv.getElementsAnnotatedWith(ApiProcessor::class.java)
        if (elements.isNullOrEmpty()) return true
        elements.forEach {
            if (it.kind.isInterface) {
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
        }
        kindMap.values.forEach { proxy ->
            try {
                proxy.buildTo().apply {
                    writeTo(processingEnv.filer)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return true
    }
}
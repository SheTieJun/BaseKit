package me.shetj.compilers

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import me.shetj.annotation.apt.SimpleImpl
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes(value = ["me.shetj.annotation.apt.SimpleImpl"])
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
open class SimpleImplProcessor : AbstractProcessor() {

    private val kindMap = HashMap<String, SimpleImplProxy>()

    @KotlinPoetMetadataPreview
    override fun process(p0: MutableSet<out TypeElement>, rEv: RoundEnvironment): Boolean {

        val elements = rEv.getElementsAnnotatedWith(SimpleImpl::class.java)

        if (elements.isNullOrEmpty()) return true

        elements.forEach {
            if (it.kind.isClass || it.kind.isInterface) {
                //必须是给接口用
                val classElement = it as TypeElement
                val fullClassName: String = classElement.qualifiedName.toString()
                var proxy: SimpleImplProxy? = kindMap[fullClassName]
                if (proxy == null) {
                    proxy = SimpleImplProxy(
                            processingEnv.elementUtils,
                            classElement,
                            "Impl"
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
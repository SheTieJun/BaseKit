package asm

import java.util.*
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

internal class DebugMethodAdapter(
    api: Int,
    methodVisitor: MethodVisitor?,
    access: Int,
    private val methodName: String?,
    private val desc: String?,
    private val className: String?,
    logAll: Boolean,
    fieldInfos: Vector<FieldInfos>
) : AdviceAdapter(api, methodVisitor, access, methodName, desc) {
    private var hasTraceLog = false
    private var methodId = 0
    private val isStaticMethod: Boolean
    private var startTimeId = 0
    private val argumentArrays: Array<Type>
    private var level = 3
    private var enableTime = false
    private var logAll = false
    private var tagName = ""
    private val fieldInfos: Vector<FieldInfos>
    private var watchField = false
    private var watchStack = false

    init {
        argumentArrays = Type.getArgumentTypes(desc)
        //定义一下方法的名称列表长度
        isStaticMethod = access and ACC_STATIC != 0
        this.logAll = logAll
        this.fieldInfos = fieldInfos
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        if (methodName == "<init>()") {
            return
        }
        if (hasTraceLog || logAll) {
            methodId = newLocal(Type.INT_TYPE)
            mv.visitMethodInsn(INVOKESTATIC, Constants.LOG_CACHE_NAME, "request", "()I", false)
            mv.visitIntInsn(ISTORE, methodId)
            addArgument()
            startTimeId = newLocal(Type.LONG_TYPE)
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
            mv.visitIntInsn(LSTORE, startTimeId)
        }
    }

    /**
     * 添加参数
     */
    private fun addArgument() {
        if (logAll || hasTraceLog) {
            for (i in argumentArrays.indices) {
                val type = argumentArrays[i]
                val index = if (isStaticMethod) i else i + 1
                val opcode = type.getOpcode(ILOAD)
                mv.visitVarInsn(opcode, index)
                box(type)
                mv.visitVarInsn(ILOAD, methodId)
                mv.visitLdcInsn(type.className)
                visitMethodInsn(
                    INVOKESTATIC, Constants.LOG_CACHE_NAME, "addMethodArgument",
                    "(Ljava/lang/Object;ILjava/lang/String;)V", false
                )
            }
        }
    }

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
        if (descriptor == Constants.ANNOTATION_NAME) {
            hasTraceLog = true
        }
        return DebugAnnotationVisitor(super.visitAnnotation(descriptor, visible))
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)

        if (methodName == "<init>") {
            return
        }
        if (hasTraceLog || logAll) {
            //保存处理一下field类型的参数值
            handleFieldInfos()
            when (opcode) {
                RETURN -> {
                    visitInsn(ACONST_NULL)
                }
                ARETURN, ATHROW -> {
                    dup()
                }
                else -> {
                    if (opcode == LRETURN || opcode == DRETURN) {
                        dup2()
                    } else {
                        dup()
                    }
                    box(Type.getReturnType(methodDesc))
                }
            }
            mv.visitLdcInsn(className)
            mv.visitLdcInsn(methodName)
            mv.visitLdcInsn(desc)
            mv.visitVarInsn(LLOAD, startTimeId)
            mv.visitVarInsn(ILOAD, methodId)
            mv.visitMethodInsn(
                INVOKESTATIC, Constants.LOG_CACHE_NAME, "updateMethodInfo",
                "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JI)V", false
            )
            mv.visitVarInsn(ILOAD, methodId)
            mv.visitLdcInsn(level)
            mv.visitLdcInsn(enableTime)
            mv.visitLdcInsn(tagName)
            mv.visitLdcInsn(watchStack)
            mv.visitMethodInsn(
                INVOKESTATIC, Constants.LOG_CACHE_NAME,
                "printMethodInfo", "(IIZLjava/lang/String;Z)V", false
            )
        }
    }

    /**
     * 处理fieldInfo相关信息
     */
    private fun handleFieldInfos() {
        //不是静态方法才获取，如果是静态方法则不获取
        if (watchField && !fieldInfos.isEmpty() && !isStaticMethod) {
            for (info in fieldInfos) {
                mv.visitVarInsn(ALOAD, 0)
                mv.visitFieldInsn(GETFIELD, className, info.name, info.descriptor)
                //进行一下装箱操作，不然会包类型转换错误
                box(info.type)
                mv.visitLdcInsn(info.name)
                mv.visitLdcInsn(info.descriptor)
                mv.visitMethodInsn(
                    INVOKESTATIC, Constants.LOG_CACHE_NAME,
                    "setFieldValues", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V", false
                )
            }
        }
    }

    override fun visitEnd() {
        super.visitEnd()
    }

    internal inner class DebugAnnotationVisitor(annotationVisitor: AnnotationVisitor?) : AnnotationVisitor(ASM9, annotationVisitor) {
        override fun visit(name: String, value: Any) {
            when (name) {
                "level" -> level = value.toString().toInt()
                "enableTime" -> enableTime = java.lang.Boolean.parseBoolean(value.toString())
                "tagName" -> tagName = value.toString()
                "watchField" -> {
                    watchField = java.lang.Boolean.parseBoolean(value.toString())
                    watchStack = java.lang.Boolean.parseBoolean(value.toString())
                }

                "watchStack" -> watchStack = java.lang.Boolean.parseBoolean(value.toString())
            }
            super.visit(name, value)
        }
    }

}
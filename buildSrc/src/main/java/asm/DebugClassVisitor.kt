package asm

import java.util.*
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type


class DebugClassVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor), Opcodes {
    private var className: String? = null
    //是否打印全部方法的log，默认是false，只有类上加了cnLog才会打印全部的log
    private var logAll = false
    //用于存放fieldInfo
    private val fieldInfos: Vector<FieldInfos> = Vector(10)

    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name;
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {

        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)

        return DebugMethodAdapter(api, methodVisitor, access, name, descriptor, className, logAll, fieldInfos)
    }

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
        //获取类上的annotation，如果有类上的annotation，那么给每一个方法生成一个log
        //如果有cnLog那么其他的方法上的log就不再处理
        if (descriptor == Constants.ANNOTATION_NAME) {
            //做一个处理
            logAll = true
        }
        return super.visitAnnotation(descriptor, visible)
    }


    override fun visitField(access: Int, name: String?, descriptor: String?, signature: String?, value: Any?): FieldVisitor {
        val type: Type = Type.getType(descriptor)
        //判断是否是静态方法，如果是静态方法则不能去获取类中非field的值，如果不是静态方法则可以获取field的值
        //用fieldInfo将当前的field存放起来
        fieldInfos.add(FieldInfos(descriptor, access, type, name))
        return super.visitField(access, name, descriptor, signature, value)
    }
}
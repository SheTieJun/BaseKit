package asm

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.LocalVariablesSorter

class DebugMethodVisitor(private val methodName: String,descriptor:String?, methodVisitor: MethodVisitor?) :
    LocalVariablesSorter(Opcodes.ASM9, Opcodes.ACC_STATIC, descriptor, methodVisitor) {
    private var isDebug = false
    private var startTimeVarIndex = 0
    override fun visitAnnotation(desc: String?, visible: Boolean): AnnotationVisitor? {
        if (desc == "Lshetj/me/base/annotation/Debug;") {
            isDebug = true
        }
        return super.visitAnnotation(desc, visible)
    }
    override fun visitCode() {
        super.visitCode()
        if (isDebug) {
            // 记录开始时间
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/System",
                "nanoTime",
                "()J",
                false
            )
            startTimeVarIndex =  newLocal(Type.LONG_TYPE)
            mv.visitVarInsn(Opcodes.LSTORE, startTimeVarIndex)
        }
    }
    override fun visitInsn(opcode: Int) {
        if (isDebug && (opcode == Opcodes.RETURN || opcode == Opcodes.IRETURN || opcode == Opcodes.LRETURN || opcode == Opcodes.FRETURN || opcode == Opcodes.DRETURN || opcode == Opcodes.ARETURN)) {
            // 记录结束时间
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/System",
                "nanoTime",
                "()J",
                false
            )
            val endTimeVarIndex = newLocal(Type.LONG_TYPE)
            mv.visitVarInsn(Opcodes.LSTORE, endTimeVarIndex)
            // 计算时间差
            mv.visitVarInsn(Opcodes.LLOAD, endTimeVarIndex)
            mv.visitVarInsn(Opcodes.LLOAD, startTimeVarIndex)
            mv.visitInsn(Opcodes.LSUB)
            // 输出时间差
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;",
                false
            )
            mv.visitLdcInsn("DebugMethodVisitor: $methodName")
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false
            )
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;",
                false
            )
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/String",
                "valueOf",
                "(J)Ljava/lang/String;",
                false
            )
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "print",
                "(Ljava/lang/String;)V",
                false
            )
        }
        super.visitInsn(opcode)
    }
    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack + 4, maxLocals)
    }
}
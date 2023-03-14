package asm

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


class PrivacyMethodVisitor(private val methodVisitor: MethodVisitor) : MethodVisitor(Opcodes.ASM9, methodVisitor) {


    override fun visitCode() {
        super.visitCode()
        methodVisitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            "java/lang/System",
            "out",
            "Ljava/io/PrintStream;"
        );
        methodVisitor.visitLdcInsn("Privacy has use：")
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/io/PrintStream",
            "println",
            "(Ljava/lang/String;)V",
            false
        )
    }

}
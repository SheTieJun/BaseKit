package asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


class DebugClassVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor), Opcodes {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (descriptor == null||name == null){
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }
        return DebugMethodVisitor(name,descriptor,super.visitMethod(access, name, descriptor, signature, exceptions))
    }

}
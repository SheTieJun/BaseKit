package asm

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * 用于存放 field相关信息
 */
class FieldInfos internal constructor(
    /**
     * 描述信息
     */
    var descriptor: String?, var access: Int, var type: Type, var name: String?
) {
    val loadCode: Int
        /**
         * 获取opcode
         *
         * @return 返回load opcode
         */
        get() = type.getOpcode(Opcodes.ILOAD)
    val storeCode: Int
        /**
         * @return 返回store opcode
         */
        get() = type.getOpcode(Opcodes.ISTORE)
}
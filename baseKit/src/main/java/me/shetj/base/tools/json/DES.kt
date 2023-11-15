package me.shetj.base.tools.json

import android.util.Base64
import android.util.Base64.NO_WRAP
import androidx.annotation.Keep
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec

/**
 * 将字符串进行DES加密解密
 * @author shetj
 */
class DES {

    private var code = 0

    constructor() {}

    /**
     * 构造函数
     * @param code 加密方式：0-“ISO-8859-1”编码，1-base64编码，其它-默认编码（utf-8）
     */
    constructor(code: Int) {
        this.code = code
    }

    /**
     * 将字符串进行DES加密
     * @param source 未加密源字符串
     * @return 加密后字符串
     */
    @Keep
    fun encrypt(source: String): String? {
        val retByte: ByteArray?

        // Create SecretKey object
        val dks: DESKeySpec?
        try {
            dks = DESKeySpec(KEY)
            val keyFactory = SecretKeyFactory.getInstance(ALGORITHM)
            val secureKey = keyFactory.generateSecret(dks)

            // Create IvParameterSpec object with initialization vector
            val spec = IvParameterSpec(IV)

            // Create Cipter object
            val cipher = Cipher.getInstance(TRANSFORMATION)

            // Initialize Cipher object
            cipher.init(Cipher.ENCRYPT_MODE, secureKey, spec)

            // Decrypting data
            retByte = cipher.doFinal(source.toByteArray())

            return when (code) {
                0 -> String(retByte, Charset.forName("ISO-8859-1"))
                1 -> Base64.encodeToString(retByte, NO_WRAP)
                else -> String(retByte)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 将DES加密的字符串解密
     * @param encrypted 加密过的字符串
     * @return 未加密源字符串
     */
    @Keep
    fun decrypt(encrypted: String): String? {
        var retByte: ByteArray?

        // Create SecretKey object
        val dks: DESKeySpec?
        try {
            dks = DESKeySpec(KEY)
            val keyFactory = SecretKeyFactory.getInstance(ALGORITHM)
            val secureKey = keyFactory.generateSecret(dks)

            val spec = IvParameterSpec(IV)

            val cipher = Cipher.getInstance(TRANSFORMATION)

            cipher.init(Cipher.DECRYPT_MODE, secureKey, spec)

            retByte = when (code) {
                0 -> encrypted.toByteArray(charset("ISO-8859-1"))
                1 -> Base64.decode(encrypted, NO_WRAP)
                else -> encrypted.toByteArray()
            }
            retByte = cipher.doFinal(retByte)
            return String(retByte, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    companion object {

        /** 加密KEY  */
        private val KEY = "6;9Ku7;:84VG*B68".toByteArray()

        /** 算法  */
        private const val ALGORITHM = "DES"

        /** IV  */
        private val IV = "sHjrydLq".toByteArray()

        /** TRANSFORMATION  */
        private const val TRANSFORMATION = "DES/CBC/PKCS5Padding"
    }
}

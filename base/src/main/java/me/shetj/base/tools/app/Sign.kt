package me.shetj.base.tools.app

import java.io.IOException
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.util.HashMap
import java.util.TreeMap
import kotlin.experimental.and

/**
 * 签名算法
 *
 *
 * 假设参与参数签名计算的请求参数分别是“k1”、“k2”、“k3”，它们的值分别是“v1”、“v2”、“v3”，则参数签名计算方法如下：
 *
 *
 * 将请求参数格式化为“key=value”格式，即“k1=v1”、“k2=v2”、“k3=v3”；
 * 将格式化好的参数键值对以字典序升序排列后，拼接在一起，即“k1=v1k2=v2k3=v3”；
 * 在拼接好的字符串末尾追加上与服务器约定的应用secret参数值； 上述字符串的MD5值即为签名的值。
 */
object Sign {


    /**
     * 签名生成算法
     *
     * @param params <String></String>,String> params 请求参数集，所有参数必须已转换为字符串类型
     * @param secret  secret 签名密钥
     * @return 签名
     * @throws IOException 输入输出异常
     */

    @Throws(IOException::class)
    fun getSignature(params: HashMap<String, String>, secret: String): String {
        // 先将参数以其参数名的字典序升序进行排序
        val sortedParams = TreeMap(params)
        val entrys = sortedParams.entries

        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        val baseString = StringBuilder()
        for ((key, value) in entrys) {
            baseString.append(key).append("=").append(value)
        }
        baseString.append(secret)

        // 使用MD5对待签名串求签
        val bytes: ByteArray
        try {
            val md5 = MessageDigest.getInstance("MD5")
            bytes = md5.digest(baseString.toString().toByteArray(charset("UTF-8")))
        } catch (ex: GeneralSecurityException) {
            throw IOException(ex)
        }

        // 将MD5输出的二进制结果转换为小写的十六进制
        val sign = StringBuilder()
        for (aByte in bytes) {
            val hex = Integer.toHexString(aByte.toInt() and 0xFF)
            if (hex.length == 1) {
                sign.append("0")
            }
            sign.append(hex)
        }
        return sign.toString()
    }

}

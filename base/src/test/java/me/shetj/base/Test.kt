package me.shetj.base

import org.junit.Test
import java.security.MessageDigest

/**
 *
 * <b>@packageName：</b> me.shetj.base<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2019/3/7 0007<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */

class Test{
    private fun encodeToMD5(s: String): String {
        var hash = ByteArray(0)
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    s.toByteArray(charset("UTF-8")))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            if (b.toInt() and 0xFF  < 0x10) {
                hex.append("0")
            }
            hex.append(Integer.toHexString((b.toInt() and 0xFF )))
        }
        return hex.toString()
    }

    @Test
    fun test(){
        print( encodeToMD5("我是xxx"))
    }
}
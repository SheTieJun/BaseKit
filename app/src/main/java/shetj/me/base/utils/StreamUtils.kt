package shetj.me.base.utils

import androidx.annotation.Keep

import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * 流转换成字符串
 */
@Keep
object StreamUtils {

    /**
     * @param inputStream inputStream
     * @return 字符串转换之后的
     */
    fun streamToString(inputStream: InputStream): String {
        try {
            val out = ByteArrayOutputStream()

            val buffer = ByteArray(1024)
            var len = 0
            len = inputStream.read(buffer)
            while (len!= -1) {
                out.write(buffer, 0, len)
                out.flush()
                len = inputStream.read(buffer)
            }
            val result = out.toString()
            out.close()
            inputStream.close()
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}
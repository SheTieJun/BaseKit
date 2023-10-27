@file:Suppress("DEPRECATION")

package shetj.me.base.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.net.URL
import java.security.MessageDigest
import me.shetj.base.ktx.md5

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2023/9/11<br>
 */
object HlsUtils {

    /**
     * Hls decode url
     * 重新生成解密的url
     * @factory return的string 必须按照RFC-3986规范：文件协议file://或者网络协议http://
     */
    suspend fun hlsDecodeUrl(context: Context, m3u8Url: String,factory: suspend (keyUri:String) ->String): String{
        return withContext(Dispatchers.IO) {
           return@withContext kotlin.runCatching {
                //截取.之前的baseurl
                val onlineBaseUrl = m3u8Url.substringBeforeLast("/")+"/"
                val playlistUrl = URL(m3u8Url)
                val connection = playlistUrl.openConnection()
                val inputStream = connection.getInputStream()
                val reader = BufferedReader(InputStreamReader(inputStream))
               val fileNameM3u8 = "h1-${m3u8Url.md5}"
                //写入到新的文件
                val m3u8File = File(context.filesDir, fileNameM3u8)
                val writer = BufferedWriter(FileWriter(m3u8File))

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (line?.startsWith("#EXT-X-KEY:") == true && line?.contains("METHOD=AES-128") == true) {
                        val keyAttributes = line?.substringAfter("#EXT-X-KEY:")!!.split(",")
                        val uri = keyAttributes.firstOrNull { it.startsWith("URI=") }?.substringAfter("=")?.replace("\"", "")
                        val iv = keyAttributes.firstOrNull { it.startsWith("IV=") }?.substringAfter("=")?.replace("\"", "")
                        val decodedString = uri?.let { String(Base64.decode(uri, Base64.DEFAULT)) }
                        val newUri = decodedString?.let { factory.invoke(it) }
                        val newKeyLine = "#EXT-X-KEY:METHOD=AES-128,URI=\"$newUri\",IV=$iv"
                        writer.write(newKeyLine)
                        writer.newLine()
                    } else if (line?.startsWith("#EXTINF:") == true) {
                        if (line?.startsWith("#EXTINF:") == true) {
                            // 提取持续时间
                            val duration = line!!.substringAfter("#EXTINF:").substringBefore(",")
                            // 提取媒体文件名
                            val filename = reader.readLine()
                            // 拼接线上 URL
                            val onlineUrl = onlineBaseUrl + filename
                            // 生成新的 #EXTINF 行和媒体文件 URL
                            val newLine = "#EXTINF:$duration,"
                            writer.write(newLine) //例如#EXTINF::9.312500,
                            writer.newLine()
                            writer.write(onlineUrl) //https://xxxxxx/v.f1004902_0.ts
                            writer.newLine()
                        }
                    } else {
                        writer.write(line)
                        writer.newLine()
                    }
                }
                reader.close()
                inputStream.close()
                writer.close()
               fileNameM3u8
            }.getOrDefault(m3u8Url)

        }
    }


    fun getWiFiIpAddress(context: Context): String {
        var ipAddress = 0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            ipAddress = wifiManager.connectionInfo.ipAddress
        } else {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.run {
                activeNetwork?.let { network ->
                    (getNetworkCapabilities(network)?.transportInfo as? WifiInfo)?.let { wifiInfo ->
                        ipAddress = wifiInfo.ipAddress
                    }
                }
            }
        }
        if (ipAddress == 0) return "127.0.0.1"
        return (ipAddress and 0xFF).toString() + "." + (ipAddress shr 8 and 0xFF) + "." + (ipAddress shr 16 and 0xFF) + "." + (ipAddress shr 24 and 0xFF)
    }

}
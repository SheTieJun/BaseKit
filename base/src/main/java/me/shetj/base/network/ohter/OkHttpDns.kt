package me.shetj.base.network.ohter

import me.shetj.base.network.RxHttp
import okhttp3.Dns
import java.net.InetAddress

/**
 * 本地dns 解析
 *
 * 实现简单，只需通过实现Dns接口即可接入HttpDns服务
 *
 * 通用性强，该方案在HTTPS,SN以及设置Cookie等场景均适用。规避了证书校验，域名检查等环节
 */
class OkHttpDns( ) : Dns {

    companion object {

        @Volatile
        private var sInstance: OkHttpDns? = null

        fun getInstance( ): OkHttpDns {
            return sInstance ?: synchronized(OkHttpDns::class.java) {
                return OkHttpDns().also {
                    sInstance = it
                }
            }
        }
    }

    override fun lookup(hostname: String): MutableList<InetAddress> {
        val ip = getIpByHost(hostname)
        ip?.let {
            return InetAddress.getAllByName(ip).asList().toMutableList()
        }
        return Dns.SYSTEM.lookup(hostname)
    }

    private fun getIpByHost(hostname: String): String? {
        return RxHttp.getInstance().getDnsMap()[hostname]
    }
}
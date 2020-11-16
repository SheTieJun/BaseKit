package me.shetj.base.network.ohter

import me.shetj.base.network.RxHttp
import okhttp3.Dns
import java.net.InetAddress

/**
 * 本地dns 解析
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
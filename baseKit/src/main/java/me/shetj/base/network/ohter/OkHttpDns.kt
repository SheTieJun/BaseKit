/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.network.ohter

import java.net.InetAddress
import me.shetj.base.S
import okhttp3.Dns

/**
 * 本地dns 解析
 *
 * 实现简单，只需通过实现Dns接口即可接入HttpDns服务
 *
 * 通用性强，该方案在HTTPS,SN以及设置Cookie等场景均适用。规避了证书校验，域名检查等环节
 */
class OkHttpDns : Dns {

    companion object {

        @Volatile
        private var sInstance: OkHttpDns? = null

        fun getInstance(): OkHttpDns {
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
        return S.getDnsMap()[hostname]
    }
}

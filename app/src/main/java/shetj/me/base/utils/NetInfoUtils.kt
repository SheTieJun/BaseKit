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


package shetj.me.base.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import timber.log.Timber


class NetInfoUtils {

    //NET_CAPABILITY_MMS： Multimedia Messaging Service,表示网络可以通过MMSC发送MMS（彩信）；
    //NET_CAPABILITY_SUPL： Secure User Plane Location,网络可以使用基站进行辅助GPS定位；
    //NET_CAPABILITY_DUN ： Dial-Up Network,网络支持拨号的方式接入
    //NET_CAPABILITY_FOTA ： Firmware Over The Air,网络可以使用FOTA服务器进行软件升级；
    //NET_CAPABILITY_IMS ： IP Multimedia Subsystem, 网络可以使用多媒体系统服务
    //NET_CAPABILITY_CBS : Cell BroadCast Messaging, 网络可以接收基站广播消息
    //NET_CAPABILITY_WIFI_P2P:Wifi Peer to Peer(Wifi Direct)，网络支持WIFI直连
    //NET_CAPABILITY_INTERNET：网络支持互联网访问

    fun Context.requestNetWork() {


        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder = NetworkRequest.Builder()
        val request = builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()
        cm.requestNetwork(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Timber.i("网络连接了")
            }


            override fun onUnavailable() {
                super.onUnavailable()
                Timber.i("网络断开了")
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Timber.i("wifi网络已连接")
                    } else {
                        Timber.i("移动网络已连接")
                    }
                }
            }
        })
    }
}
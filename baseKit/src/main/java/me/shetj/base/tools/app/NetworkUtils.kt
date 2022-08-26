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
package me.shetj.base.tools.app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.content.getSystemService

/**
 * / **
 * * GPRS    2G(2.5) General Packet Radia Service 114kbps
 * * EDGE    2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
 * * UMTS    3G WCDMA 联通3G Universal MOBILE Telecommunication System 完整的3G移动通信技术标准
 * * CDMA    2G 电信 Code Division Multiple Access 码分多址
 * * EVDO_0  3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
 * * EVDO_A  3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
 * * 1xRTT   2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
 * * HSDPA   3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
 * * HSUPA   3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
 * * HSPA    3G (分HSDPA,HSUPA) High Speed Packet Access
 * * IDEN    2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
 * * EVDO_B  3G EV-DO Rev.B 14.7Mbps 下行 3.5G
 * * LTE     4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
 * * EHRPD   3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
 * * HSPAP   3G HSPAP 比 HSDPA 快些
 */
object NetworkUtils {
    /**
     * 打开网络设置界面
     * 3.0以下打开设置界面
     * @param context 上下文
     */
    fun openWirelessSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_SETTINGS))
    }

    /**
     * 获取活动网路信息
     * @param context 上下文
     * @return NetworkCapabilities
     */
    private fun getActiveNetworkInfo(context: Context): NetworkCapabilities? {
        return context
            .getSystemService<ConnectivityManager>()?.let {
                it.getNetworkCapabilities(it.activeNetwork)
            }
    }

    /**
     * 判断网络是否可用
     * 需添加权限 android.permission.ACCESS_NETWORK_STATE
     */
    fun isAvailable(context: Context): Boolean {
        return getActiveNetworkInfo(context)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    }

    /**
     * 判断网络是否连接
     * 需添加权限 android.permission.ACCESS_NETWORK_STATE
     * @param context 上下文
     * @return true: 是<br></br>false: 否
     */
    fun isConnected(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && (info.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                info.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }


    /**
     * 判断wifi是否连接状态
     * 需添加权限 android.permission.ACCESS_NETWORK_STATE
     * @param context 上下文
     * @return true: 连接<br></br>false: 未连接
     */
    fun isWifiConnected(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    /**
     * 获取移动网络运营商名称
     * 如中国联通、中国移动、中国电信
     * @param context 上下文
     * @return 移动网络运营商名称
     */
    fun getNetworkOperatorName(context: Context): String? {
        return context.getSystemService<TelephonyManager>()?.networkOperatorName
    }

    /**
     * 获取移动终端类型
     * @param context 上下文
     * @return 手机制式
     *  * PHONE_TYPE_NONE  : 0 手机制式未知
     *  * PHONE_TYPE_GSM   : 1 手机制式为GSM，移动和联通
     *  * PHONE_TYPE_CDMA  : 2 手机制式为CDMA，电信
     *  * PHONE_TYPE_SIP   : 3
     */
    fun getPhoneType(context: Context): String {
        val i = context.getSystemService<TelephonyManager>()?.phoneType ?: -1
        return when (i) {
            1 -> "PHONE_TYPE_GSM"
            2 -> "PHONE_TYPE_CDMA"
            3 -> "PHONE_TYPE_SIP"
            else -> "PHONE_TYPE_NONE"
        }
    }
}

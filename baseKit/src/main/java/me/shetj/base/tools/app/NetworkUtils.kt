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
@file:Suppress("DEPRECATION")

package me.shetj.base.tools.app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import android.telephony.TelephonyManager

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
    const val NETWORK_WIFI = 1 // wifi network
    const val NETWORK_5G = 5 // "4G" networks
    const val NETWORK_4G = 4 // "4G" networks
    const val NETWORK_3G = 3 // "3G" networks
    const val NETWORK_2G = 2 // "2G" networks
    const val NETWORK_UNKNOWN = 6 // unknown network
    const val NETWORK_NO = -1 // no network
    private const val NETWORK_TYPE_GSM = 16
    private const val NETWORK_TYPE_TD_SCDMA = 17
    private const val NETWORK_TYPE_IWLAN = 18

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
     * @return NetworkInfo
     */
    private fun getActiveNetworkInfo(context: Context): NetworkInfo? {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    /**
     * 判断网络是否可用
     * 需添加权限 android.permission.ACCESS_NETWORK_STATE
     */
    fun isAvailable(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.isAvailable
    }

    /**
     * 判断网络是否连接
     * 需添加权限 android.permission.ACCESS_NETWORK_STATE
     * @param context 上下文
     * @return true: 是<br></br>false: 否
     */
    fun isConnected(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.isConnected
    }

    /**
     * 判断网络是否是4G
     * 需添加权限 android.permission.ACCESS_NETWORK_STATE
     * @param context 上下文
     * @return true: 是<br></br>false: 不是
     */
    fun is4G(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.isAvailable && info.subtype == TelephonyManager.NETWORK_TYPE_LTE
    }

    /**
     * 判断wifi是否连接状态
     * 需添加权限 android.permission.ACCESS_NETWORK_STATE
     * @param context 上下文
     * @return true: 连接<br></br>false: 未连接
     */
    fun isWifiConnected(context: Context): Boolean {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE)
        return cm?.let { it as ConnectivityManager }?.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI
    }

    /**
     * 获取移动网络运营商名称
     * 如中国联通、中国移动、中国电信
     * @param context 上下文
     * @return 移动网络运营商名称
     */
    fun getNetworkOperatorName(context: Context): String? {
        val tm = context
            .getSystemService(Context.TELEPHONY_SERVICE)
        return tm?.let { it as TelephonyManager }?.networkOperatorName
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
    fun getPhoneType(context: Context): Int {
        val tm = context
            .getSystemService(Context.TELEPHONY_SERVICE)
        return tm?.let { it as TelephonyManager }?.phoneType ?: -1
    }

    /**
     * 获取当前的网络类型(WIFI,2G,3G,4G)
     * 需添加权限 android.permission.ACCESS_NETWORK_STATE
     * @param context 上下文
     * @return 网络类型
     *
     *  * NETWORK_WIFI    = 1;
     *  * NETWORK_4G      = 4;
     *  * NETWORK_3G      = 3;
     *  * NETWORK_2G      = 2;
     *  * NETWORK_UNKNOWN = 5;
     *  * NETWORK_NO      = -1;
     *
     */
    fun getNetWorkType(context: Context): Int {
        var netType = NETWORK_NO
        val info = getActiveNetworkInfo(context)
        if (info != null && info.isAvailable) {
            netType = when (info.type) {
                ConnectivityManager.TYPE_WIFI -> {
                    NETWORK_WIFI
                }
                ConnectivityManager.TYPE_MOBILE -> {
                    when (info.subtype) {
                        NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS,
                        TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE,
                        TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NETWORK_2G
                        NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A,
                        TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0,
                        TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA,
                        TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B,
                        TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NETWORK_3G
                        NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> NETWORK_4G
                        TelephonyManager.NETWORK_TYPE_NR -> NETWORK_5G
                        else -> {
                            val subtypeName = info.subtypeName
                            if (subtypeName.equals("TD-SCDMA", ignoreCase = true) ||
                                subtypeName.equals("WCDMA", ignoreCase = true) ||
                                subtypeName.equals("CDMA2000", ignoreCase = true)
                            ) {
                                NETWORK_3G
                            } else {
                                NETWORK_UNKNOWN
                            }
                        }
                    }
                }
                else -> {
                    NETWORK_UNKNOWN
                }
            }
        }
        return netType
    }

    /**
     * 获取当前的网络类型(WIFI,2G,3G,4G)
     * 依赖上面的方法
     * @param context 上下文
     * @return 网络类型名称
     *
     *  * NETWORK_WIFI
     *  * NETWORK_4G
     *  * NETWORK_3G
     *  * NETWORK_2G
     *  * NETWORK_UNKNOWN
     *  * NETWORK_NO
     *
     */
    fun getNetWorkTypeName(context: Context): String {
        return when (getNetWorkType(context)) {
            NETWORK_WIFI -> "NETWORK_WIFI"
            NETWORK_4G -> "NETWORK_4G"
            NETWORK_3G -> "NETWORK_3G"
            NETWORK_2G -> "NETWORK_2G"
            NETWORK_NO -> "NETWORK_NO"
            else -> "NETWORK_UNKNOWN"
        }
    }
}

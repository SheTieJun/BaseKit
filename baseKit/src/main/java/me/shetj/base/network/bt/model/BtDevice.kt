package me.shetj.base.network.bt.model

/**
 * 蓝牙设备信息
 */
data class BtDevice(
    /** 设备名称 */
    val name: String,
    /** MAC 地址 */
    val address: String,
    /** 信号强度 (dBm) */
    val rssi: Int = 0,
    /** 是否已配对 */
    val bonded: Boolean = false,
) {
    /** 唯一标识 = MAC 地址 */
    val id: String get() = address
}

package me.shetj.base.network.lan.model

/**
 * 局域网设备信息
 */
data class LanDevice(
    /** 设备名称（NSD 服务名） */
    val name: String,
    /** IP 地址 */
    val host: String,
    /** 通信端口 */
    val port: Int,
    /** 服务类型标识，用于区分不同应用 */
    val serviceType: String,
) {
    /** 唯一标识 = name + serviceType */
    val id: String get() = "$name@$serviceType"
}

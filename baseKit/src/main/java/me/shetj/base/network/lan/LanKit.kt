package me.shetj.base.network.lan

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import me.shetj.base.ktx.logI
import me.shetj.base.network.lan.discovery.NsdHelper
import me.shetj.base.network.lan.model.LanDevice
import me.shetj.base.network.lan.model.LanMessage
import me.shetj.base.network.lan.tcp.TcpLink
import java.security.MessageDigest

/**
 * 局域网设备发现与通信工具
 *
 * 使用示例：
 * ```
 * // 不带密钥
 * LanKit.start(context)
 *
 * // 带密钥：只有持有相同密钥的设备才能建立通信
 * LanKit.start(context, secretKey = "my-app-key-2024")
 *
 * // 发现设备
 * lifecycleScope.launch {
 *     LanKit.devices.collect { devices -> ... }
 * }
 *
 * // 连接、收发消息
 * LanKit.connect(device)
 * LanKit.send(device, "hello")
 * LanKit.messages.collect { msg -> ... }
 * ```
 */
@SuppressLint("HardwareIds")
object LanKit {

    private const val TAG = "LanKit"

    private var nsdHelper: NsdHelper? = null
    private var tcpLink: TcpLink? = null
    private var context: Context? = null

    /** 设备 ID */
    val deviceId: String
        get() = context?.let { Settings.Secure.getString(it.contentResolver, Settings.Secure.ANDROID_ID) }
            ?: "unknown"

    @Volatile
    var isRunning: Boolean = false
        private set

    /** 收到的消息 */
    val messages: SharedFlow<LanMessage>
        get() = tcpLink?.messages ?: throw IllegalStateException("LanKit not started, call start() first")

    /** 发现的设备列表 */
    val devices: Flow<List<LanDevice>>
        get() = nsdHelper?.discover() ?: throw IllegalStateException("LanKit not started, call start() first")

    // ==================== 初始化 ====================

    /**
     * 初始化并启动局域网服务
     * @param ctx Application Context
     * @param secretKey 握手密钥 — 同时在 NSD 发现层(TCP 握手层做双重校验。null 则不校验
     */
    fun start(ctx: Context, secretKey: String? = null) {
        if (isRunning) return
        context = ctx.applicationContext

        val keyHash = secretKey?.let { hashKey(it) }

        // 1. 启动 TCP 服务端（带握手校验）
        val link = TcpLink(TAG, secretKey)
        val portResult = link.startServer()
        if (portResult.isFailure) {
            "TCP server start failed".logI(TAG)
            return
        }
        val port = portResult.getOrThrow()
        tcpLink = link

        // 2. 注册 NSD 服务（keyHash 不同则在不同发现频道）
        nsdHelper = NsdHelper(ctx.applicationContext, deviceId, port, keyHash)
        CoroutineScope(Dispatchers.IO).launch {
            nsdHelper?.register()
        }

        isRunning = true
        val channel = if (keyHash != null) "$keyHash" else "default"
        "LanKit started, port=$port, channel=$channel".logI(TAG)
    }

    /** 对密钥做 SHA-256，取前 8 位十六进制作为 NSD 频道标识 */
    private fun hashKey(key: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(key.toByteArray(Charsets.UTF_8))
        return hash.take(4).joinToString("") { "%02x".format(it) }
    }

    // ==================== 连接 ====================

    /** 连接到指定设备 */
    fun connect(device: LanDevice): Result<Unit> {
        return tcpLink?.connect(device.host, device.port)
            ?: Result.failure(IllegalStateException("LanKit not started"))
    }

    /** 断开指定设备 */
    fun disconnect(device: LanDevice) {
        tcpLink?.disconnect(device.host, device.port)
    }

    // ==================== 收发 ====================

    /** 发送消息到指定设备 */
    fun send(device: LanDevice, payload: String, type: String = LanMessage.TYPE_TEXT): Result<Unit> {
        val msg = LanMessage(payload = payload, type = type, senderId = deviceId)
        return tcpLink?.send(device.host, device.port, msg)
            ?: Result.failure(IllegalStateException("LanKit not started"))
    }

    /** 广播消息到所有已连接设备 */
    fun broadcast(payload: String, type: String = LanMessage.TYPE_TEXT): Result<Unit> {
        val msg = LanMessage(payload = payload, type = type, senderId = deviceId)
        return tcpLink?.broadcast(msg)
            ?: Result.failure(IllegalStateException("LanKit not started"))
    }

    // ==================== 生命周期 ====================

    /** 停止所有服务 */
    fun stop() {
        nsdHelper?.unregister()
        nsdHelper = null
        tcpLink?.stop()
        tcpLink = null
        isRunning = false
        "LanKit stopped".logI(TAG)
    }
}

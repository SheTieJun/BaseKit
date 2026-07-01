package me.shetj.base.network.bt

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.PermissionChecker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import me.shetj.base.ktx.logI
import me.shetj.base.network.bt.discovery.BtDiscovery
import me.shetj.base.network.bt.model.BtDevice
import me.shetj.base.network.bt.transport.BtRfcommLink
import me.shetj.base.network.lan.model.LanMessage

/**
 * 蓝牙设备发现与通信工具
 *
 * 需要权限：
 * - Android 12+: BLUETOOTH_SCAN, BLUETOOTH_CONNECT
 * - Android <12: BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_FINE_LOCATION
 *
 * 使用示例：
 * ```
 * if (!BtKit.hasPermissions(context)) {
 *     BtKit.requestPermissions(activity)
 *     return
 * }
 * BtKit.start(context)
 *
 * // 发现设备
 * lifecycleScope.launch {
 *     BtKit.devices.collect { devices -> ... }
 * }
 *
 * // 连接、收发消息
 * BtKit.connect(device)
 * BtKit.send(device, "hello")
 * BtKit.messages.collect { msg -> ... }
 * ```
 */
object BtKit {

    private const val TAG = "BtKit"

    private var discovery: BtDiscovery? = null
    private var transport: BtRfcommLink? = null
    private var context: Context? = null

    @Volatile
    var isRunning: Boolean = false
        private set

    /** 收到的消息 */
    val messages: SharedFlow<LanMessage>
        get() = transport?.messages ?: throw IllegalStateException("BtKit not started")

    /** 发现的设备列表 */
    val devices: Flow<List<BtDevice>>
        get() = discovery?.discover() ?: throw IllegalStateException("BtKit not started")

    /** 蓝牙是否可用且已开启 */
    val isAvailable: Boolean
        get() = BluetoothAdapter.getDefaultAdapter()?.isEnabled == true

    // ==================== 权限 ====================

    /** 返回当前 Android 版本所需的蓝牙权限列表 */
    fun requiredPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            listOf(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /** 检查是否已获取所有必要的蓝牙权限 */
    fun hasPermissions(ctx: Context): Boolean {
        val missing = getMissingPermissions(ctx)
        if (missing.isNotEmpty()) {
            "BtKit missing permissions: ${missing.joinToString()}".logI(TAG)
        }
        return missing.isEmpty()
    }

    /** 返回尚未授予的权限列表 */
    fun getMissingPermissions(ctx: Context): List<String> {
        return requiredPermissions().filter {
            PermissionChecker.checkSelfPermission(ctx, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    // ==================== 初始化 ====================

    /**
     * 启动蓝牙服务
     * @param ctx Application Context
     */
    fun start(ctx: Context) {
        if (isRunning) return
        context = ctx.applicationContext

        // 检查权限
        val missing = getMissingPermissions(ctx.applicationContext)
        if (missing.isNotEmpty()) {
            "BtKit start blocked: missing permissions [${missing.joinToString()}]".logI(TAG)
            return
        }

        if (!isAvailable) {
            "Bluetooth not available or not enabled".logI(TAG)
            return
        }

        // 1. 启动 RFCOMM 服务端
        val link = BtRfcommLink(TAG)
        val result = link.startServer()
        if (result.isFailure) {
            "RFCOMM server start failed: ${result.exceptionOrNull()?.message}".logI(TAG)
            return
        }
        transport = link

        // 2. 开始设备发现
        discovery = BtDiscovery(ctx.applicationContext)

        isRunning = true
        "BtKit started".logI(TAG)
    }

    // ==================== 连接 ====================

    /** 连接到指定蓝牙设备 */
    fun connect(device: BtDevice): Result<Unit> {
        val link = transport ?: return Result.failure(IllegalStateException("BtKit not started"))

        // 连接前再校验一次权限
        val ctx = context ?: return Result.failure(IllegalStateException("Context not available"))
        val missing = getMissingPermissions(ctx)
        if (missing.isNotEmpty()) {
            return Result.failure(
                SecurityException("Missing permissions: ${missing.joinToString()}")
            )
        }

        val bt = BluetoothAdapter.getDefaultAdapter()
            ?: return Result.failure(Exception("Bluetooth not supported"))
        val btDevice = bt.getRemoteDevice(device.address)
        return link.connect(btDevice)
    }

    /** 断开指定设备 */
    fun disconnect(device: BtDevice) {
        transport?.disconnect(device.address)
    }

    // ==================== 收发 ====================

    /** 发送消息到指定设备 */
    fun send(device: BtDevice, payload: String, type: String = LanMessage.TYPE_TEXT): Result<Unit> {
        val msg = LanMessage(payload = payload, type = type)
        return transport?.send(device.address, msg)
            ?: Result.failure(IllegalStateException("BtKit not started"))
    }

    /** 广播消息到所有已连接设备 */
    fun broadcast(payload: String, type: String = LanMessage.TYPE_TEXT): Result<Unit> {
        val msg = LanMessage(payload = payload, type = type)
        return transport?.broadcast(msg)
            ?: Result.failure(IllegalStateException("BtKit not started"))
    }

    // ==================== 生命周期 ====================

    /** 停止所有服务 */
    fun stop() {
        transport?.stop()
        transport = null
        discovery = null
        isRunning = false
        "BtKit stopped".logI(TAG)
    }
}

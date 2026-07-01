package me.shetj.base.network.bt.transport

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.shetj.base.ktx.logI
import me.shetj.base.network.lan.model.LanMessage
import me.shetj.base.tools.json.getGson
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 蓝牙 RFCOMM 传输层（服务端 + 客户端）
 * - 使用标准 SPP UUID 建立 RFCOMM 通道
 * - 支持多设备同时连接
 * - 消息帧格式：4 字节长度 + JSON 内容（与 LanKit TcpLink 一致）
 *
 * @param tag 日志标签
 */
internal class BtRfcommLink(
    private val tag: String,
) {
    /** 标准串口 UUID */
    private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var serverSocket: BluetoothServerSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val outputStreams = ConcurrentHashMap<String, DataOutputStream>()
    private val running = AtomicBoolean(false)
    private val gson by lazy { getGson() }

    /** 收到的消息流 */
    val messages: MutableSharedFlow<LanMessage> = MutableSharedFlow()

    /** 是否蓝牙可用 */
    val isAvailable: Boolean get() = adapter?.isEnabled == true

    // ==================== 服务端 ====================

    /** 启动 RFCOMM 服务端，等待其他设备连接 */
    fun startServer(): Result<Unit> {
        val bt = adapter ?: return Result.failure(Exception("Bluetooth not supported"))
        if (!bt.isEnabled) return Result.failure(Exception("Bluetooth not enabled"))

        return try {
            serverSocket = bt.listenUsingRfcommWithServiceRecord("BtKit", sppUuid)
            running.set(true)

            scope.launch { acceptLoop() }

            "RFCOMM server started, name=${bt.name}".logI(tag)
            Result.success(Unit)
        } catch (e: IOException) {
            "RFCOMM server start failed: ${e.message}".logI(tag)
            Result.failure(e)
        }
    }

    private suspend fun acceptLoop() {
        while (running.get()) {
            try {
                val socket = serverSocket?.accept() ?: continue
                scope.launch { handleClient(socket) }
            } catch (_: IOException) {
                break
            } catch (e: Exception) {
                "RFCOMM accept error: ${e.message}".logI(tag)
            }
        }
    }

    /** 处理客户端连接 — 读取帧并 emit 消息 */
    private suspend fun handleClient(socket: BluetoothSocket) {
        val remoteKey = socket.remoteDevice.address
        try {
            val deviceName = socket.remoteDevice.name ?: "unknown"
            "RFCOMM connected: $deviceName ($remoteKey)".logI(tag)

            val input = DataInputStream(socket.inputStream)
            val output = DataOutputStream(socket.outputStream)
            outputStreams[remoteKey] = output

            while (running.get() && scope.isActive) {
                val message = readFrame(input, remoteKey) ?: break
                messages.emit(message)
            }
        } catch (e: IOException) {
            if (running.get()) "RFCOMM read error($remoteKey): ${e.message}".logI(tag)
        } finally {
            outputStreams.remove(remoteKey)
            try { socket.close() } catch (_: IOException) { }
        }
    }

    // ==================== 客户端 ====================

    /** 连接到指定蓝牙设备 */
    fun connect(device: BluetoothDevice): Result<Unit> {
        val address = device.address
        return try {
            val socket = device.createRfcommSocketToServiceRecord(sppUuid)
            socket.connect()
            val output = DataOutputStream(socket.outputStream)
            outputStreams[address] = output
            "RFCOMM connected to ${device.name ?: address}".logI(tag)

            scope.launch {
                try {
                    val input = DataInputStream(socket.inputStream)
                    while (running.get() && scope.isActive) {
                        val message = readFrame(input, address) ?: break
                        messages.emit(message)
                    }
                } catch (e: IOException) {
                    if (running.get()) "RFCOMM read error($address): ${e.message}".logI(tag)
                } finally {
                    outputStreams.remove(address)
                    try { socket.close() } catch (_: IOException) { }
                }
            }

            Result.success(Unit)
        } catch (e: IOException) {
            "RFCOMM connect failed($address): ${e.message}".logI(tag)
            Result.failure(e)
        }
    }

    // ==================== 收发消息 ====================

    /** 发送消息到指定设备 */
    fun send(address: String, message: LanMessage): Result<Unit> {
        return try {
            val output = outputStreams[address] ?: return Result.failure(
                IOException("Not connected to $address")
            )
            writeFrame(output, message)
            "RFCOMM sent to $address: ${message.payload.take(100)}".logI(tag)
            Result.success(Unit)
        } catch (e: IOException) {
            "RFCOMM send failed($address): ${e.message}".logI(tag)
            Result.failure(e)
        }
    }

    /** 广播消息给所有已连接设备 */
    fun broadcast(message: LanMessage): Result<Unit> {
        val errors = mutableListOf<String>()
        outputStreams.forEach { (key, output) ->
            try {
                writeFrame(output, message)
            } catch (e: IOException) {
                errors.add("$key: ${e.message}")
            }
        }
        return if (errors.isEmpty()) Result.success(Unit)
        else Result.failure(IOException("Broadcast errors: ${errors.joinToString()}"))
    }

    // ==================== 内部方法 ====================

    private fun writeFrame(output: DataOutputStream, message: LanMessage) {
        val json = gson.toJson(message)
        val bytes = json.toByteArray(Charsets.UTF_8)
        output.writeInt(bytes.size)
        output.write(bytes)
        output.flush()
    }

    private fun readFrame(input: DataInputStream, remoteKey: String): LanMessage? {
        val lengthBytes = ByteArray(4)
        input.readFully(lengthBytes)
        val length = ByteBuffer.wrap(lengthBytes).int
        if (length <= 0 || length > 1024 * 1024) return null

        val bytes = ByteArray(length)
        input.readFully(bytes)
        val json = String(bytes, Charsets.UTF_8)

        return try {
            gson.fromJson(json, LanMessage::class.java)
        } catch (_: Exception) {
            LanMessage(payload = json, senderId = remoteKey)
        }
    }

    // ==================== 生命周期 ====================

    /** 断开指定设备 */
    fun disconnect(address: String) {
        outputStreams[address]?.close()
        outputStreams.remove(address)
        "RFCOMM disconnected: $address".logI(tag)
    }

    /** 停止所有连接和服务端 */
    fun stop() {
        "RFCOMM stopping...".logI(tag)
        running.set(false)
        outputStreams.values.forEach { try { it.close() } catch (_: Exception) { } }
        outputStreams.clear()
        try { serverSocket?.close() } catch (_: IOException) { }
        serverSocket = null
        "RFCOMM stopped".logI(tag)
    }
}

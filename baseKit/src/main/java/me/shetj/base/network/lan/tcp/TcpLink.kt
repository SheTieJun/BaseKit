package me.shetj.base.network.lan.tcp

import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.shetj.base.ktx.logI
import me.shetj.base.network.lan.model.LanMessage
import me.shetj.base.tools.json.GsonKit
import me.shetj.base.tools.json.getGson
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * TCP 链路管理（服务端 + 客户端）
 * - 自动选择可用端口启动服务端
 * - 支持同时连接多个设备
 * - 消息帧格式：4 字节长度 + JSON 内容
 * - 握手校验：密钥匹配才能建立通信
 *
 * @param tag 日志标签
 * @param secretKey 握手密钥，null 表示跳过握手校验
 */
internal class TcpLink(
    private val tag: String,
    private val secretKey: String? = null,
) {
    private var serverSocket: ServerSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    /** 客户端的输出流缓存，key = host:port */
    private val outputStreams = ConcurrentHashMap<String, DataOutputStream>()
    private val running = AtomicBoolean(false)

    /** 收到的消息流（只包含握手通过后的业务消息） */
    val messages: MutableSharedFlow<LanMessage> = MutableSharedFlow()

    /** 当前服务端端口，-1 表示未启动 */
    @Volatile
    var serverPort: Int = -1
        private set

    private val gson by lazy { GsonKit.gson }

    // ==================== 服务端 ====================

    /** 启动 TCP 服务端，自动选择可用端口 */
    fun startServer(): Result<Int> {
        return try {
            serverSocket = ServerSocket(0) // 0 = 系统自动分配端口
            serverPort = serverSocket!!.localPort
            running.set(true)

            scope.launch { acceptLoop() }

            val ip = serverSocket?.inetAddress?.hostAddress ?: "0.0.0.0"
            "TCP server started on $ip:$serverPort".logI(tag)
            Result.success(serverPort)
        } catch (e: Exception) {
            "TCP server start failed: ${e.message}".logI(tag)
            Result.failure(e)
        }
    }

    @WorkerThread
    private fun acceptLoop() {
        while (running.get()) {
            try {
                val client = serverSocket?.accept() ?: continue
                scope.launch { handleClient(client) }
            } catch (_: SocketException) {
                break
            } catch (e: Exception) {
                "Accept error: ${e.message}".logI(tag)
            }
        }
    }

    /** 服务端处理客户端连接 — 先握手校验，通过后才收发业务消息 */
    private suspend fun handleClient(socket: Socket) {
        val remoteKey = "${socket.inetAddress.hostAddress}:${socket.port}"
        try {
            "Client connected: $remoteKey".logI(tag)
            val input = DataInputStream(socket.getInputStream())
            val output = DataOutputStream(socket.getOutputStream())

            // 读第一条消息作为握手
            val handshake = readFrame(input, remoteKey)
            if (!validateHandshake(handshake)) {
                "Handshake rejected: $remoteKey, key mismatch".logI(tag)
                try { socket.close() } catch (_: Exception) { }
                return
            }
            "Handshake accepted: $remoteKey, sender=${handshake?.senderId}".logI(tag)

            outputStreams[remoteKey] = output

            // 握手通过，继续收发业务消息
            while (running.get() && scope.isActive) {
                val message = readFrame(input, remoteKey) ?: break
                messages.emit(message)
            }
        } catch (e: Exception) {
            if (running.get()) "Client handler error: ${e.message}".logI(tag)
        } finally {
            outputStreams.remove(remoteKey)
            try { socket.close() } catch (_: Exception) { }
        }
    }

    // ==================== 客户端 ====================

    /** 连接到远程设备 */
    fun connect(host: String, port: Int, timeoutMs: Int = 5000): Result<Unit> {
        val remoteKey = "$host:$port"
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(host, port), timeoutMs)
            val output = DataOutputStream(socket.getOutputStream())

            // 先发送握手消息
            if (secretKey != null) {
                val handshake = LanMessage(type = LanMessage.TYPE_HANDSHAKE, payload = secretKey)
                writeFrame(output, handshake)
                "Handshake sent to $remoteKey".logI(tag)
            }

            outputStreams[remoteKey] = output
            "Connected to $remoteKey".logI(tag)

            scope.launch {
                try {
                    val input = DataInputStream(socket.getInputStream())
                    while (running.get() && scope.isActive) {
                        val message = readFrame(input, remoteKey) ?: break
                        messages.emit(message)
                    }
                } catch (e: Exception) {
                    if (running.get()) "Connection read error: ${e.message}".logI(tag)
                } finally {
                    outputStreams.remove(remoteKey)
                    try { socket.close() } catch (_: Exception) { }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            "Connect failed to $remoteKey: ${e.message}".logI(tag)
            Result.failure(e)
        }
    }

    // ==================== 收发消息 ====================

    /** 发送消息到指定设备 */
    fun send(host: String, port: Int, message: LanMessage): Result<Unit> {
        val key = "$host:$port"
        return try {
            val output = outputStreams[key] ?: return Result.failure(
                Exception("Not connected to $key, call connect first")
            )
            writeFrame(output, message)
            "Sent to $key: ${message.payload.take(100)}".logI(tag)
            Result.success(Unit)
        } catch (e: Exception) {
            "Send failed to $key: ${e.message}".logI(tag)
            Result.failure(e)
        }
    }

    /** 广播消息给所有已连接设备 */
    fun broadcast(message: LanMessage): Result<Unit> {
        val errors = mutableListOf<String>()
        outputStreams.forEach { (key, output) ->
            try {
                writeFrame(output, message)
            } catch (e: Exception) {
                errors.add("$key: ${e.message}")
            }
        }
        return if (errors.isEmpty()) Result.success(Unit)
        else Result.failure(Exception("Broadcast errors: ${errors.joinToString()}"))
    }

    // ==================== 内部方法 ====================

    /** 写入一帧：4 字节长度 + JSON */
    private fun writeFrame(output: DataOutputStream, message: LanMessage) {
        val json = gson.toJson(message)
        val bytes = json.toByteArray(Charsets.UTF_8)
        output.writeInt(bytes.size)
        output.write(bytes)
        output.flush()
    }

    /** 阻塞读取一帧 */
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

    /** 校验握手消息：密钥匹配（secretKey 为 null 时跳过校验） */
    private fun validateHandshake(handshake: LanMessage?): Boolean {
        if (secretKey == null) return true // 未设置密钥则接受所有连接
        return handshake?.type == LanMessage.TYPE_HANDSHAKE && handshake.payload == secretKey
    }

    // ==================== 生命周期 ====================

    /** 断开与指定设备的连接 */
    fun disconnect(host: String, port: Int) {
        val key = "$host:$port"
        outputStreams[key]?.close()
        outputStreams.remove(key)
        "Disconnected: $key".logI(tag)
    }

    /** 停止所有连接和服务端 */
    fun stop() {
        "TCP Link stopping...".logI(tag)
        running.set(false)
        outputStreams.values.forEach { try { it.close() } catch (_: Exception) { } }
        outputStreams.clear()
        try { serverSocket?.close() } catch (_: Exception) { }
        serverSocket = null
        serverPort = -1
        "TCP Link stopped".logI(tag)
    }
}

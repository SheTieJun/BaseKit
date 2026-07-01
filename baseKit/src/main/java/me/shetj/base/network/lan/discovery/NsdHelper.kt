package me.shetj.base.network.lan.discovery

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import me.shetj.base.ktx.logI
import me.shetj.base.network.lan.model.LanDevice
import java.net.InetAddress
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * NSD 局域网设备发现
 *
 * @param context Application Context
 * @param serviceName 本机服务名（注册时使用）
 * @param port 本机 TCP 通信端口
 * @param keyHash 密钥哈希，用于区分不同应用的发现频道；null 则使用默认频道
 */
internal class NsdHelper(
    private val context: Context,
    private val serviceName: String,
    private val port: Int,
    private val keyHash: String? = null,
) {
    private val nsdManager: NsdManager? by lazy {
        context.getSystemService(Context.NSD_SERVICE) as? NsdManager
    }

    /** 服务类型 = _basekit-{keyHash}._tcp.，密钥不同则在不同频道，互不可见 */
    private val serviceType: String
        get() = if (keyHash != null) "_basekit-$keyHash._tcp." else "_basekit._tcp."

    /** 是否已注册 */
    @Volatile
    var isRegistered: Boolean = false
        private set

    // ==================== 注册本机服务 ====================

    private var registrationListener: NsdManager.RegistrationListener? = null

    /** 注册本机服务到局域网，让其他设备能发现 */
    suspend fun register(): Result<Unit> = suspendCancellableCoroutine { cont ->
        val manager = nsdManager ?: run {
            cont.resume(Result.failure(Exception("NsdManager not available")))
            return@suspendCancellableCoroutine
        }

        val serviceInfo = NsdServiceInfo().apply {
            serviceName = this@NsdHelper.serviceName
            serviceType = this@NsdHelper.serviceType
            port = this@NsdHelper.port
        }

        registrationListener = createRegistrationListener(cont)
        manager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    private fun createRegistrationListener(cont: CancellableContinuation<Result<Unit>>) =
        object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
                "NSD registered: ${serviceInfo?.serviceName}".logI(TAG)
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                val msg = "NSD register failed, error: $errorCode"
                msg.logI(TAG)
                if (cont.isActive) cont.resume(Result.failure(Exception(msg)))
            }

            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {
                isRegistered = false
                "NSD unregistered: ${serviceInfo?.serviceName}".logI(TAG)
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                "NSD unregister failed, error: $errorCode".logI(TAG)
            }
        }.also {
            isRegistered = true
            if (cont.isActive) cont.resume(Result.success(Unit))
        }

    /** 取消注册 */
    fun unregister() {
        registrationListener?.let {
            nsdManager?.unregisterService(it)
        }
        registrationListener = null
        isRegistered = false
    }

    // ==================== 发现设备 ====================

    /** 开始扫描局域网设备，通过 Flow 持续发送发现的设备 */
    fun discover(): Flow<List<LanDevice>> = callbackFlow {
        val manager = nsdManager ?: run {
            close(Exception("NsdManager not available"))
            return@callbackFlow
        }

        val discovered = mutableMapOf<String, LanDevice>()

        val discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {
                "NSD discovery started: $serviceType".logI(TAG)
            }

            override fun onDiscoveryStopped(serviceType: String) {
                "NSD discovery stopped: $serviceType".logI(TAG)
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                // NSD 回调不在协程中，用 runBlocking 桥接
                val device = runBlocking {
                    suspendCancellableCoroutine<LanDevice?> { c ->
                        manager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                                if (c.isActive) c.resume(null)
                            }

                            override fun onServiceResolved(resolvedInfo: NsdServiceInfo) {
                                val host = resolvedInfo.host?.hostAddress ?: return
                                val device = LanDevice(
                                    name = resolvedInfo.serviceName,
                                    host = host,
                                    port = resolvedInfo.port,
                                    serviceType = serviceType,
                                )
                                if (c.isActive) c.resume(device)
                            }
                        })
                    }
                }
                if (device != null) {
                    discovered[device.id] = device
                    trySend(discovered.values.toList())
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                discovered.remove("${serviceInfo.serviceName}@$serviceType")
                trySend(discovered.values.toList())
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                "NSD start discovery failed, error: $errorCode".logI(TAG)
                close(Exception("NSD discovery start failed: $errorCode"))
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                "NSD stop discovery failed, error: $errorCode".logI(TAG)
            }
        }

        manager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)

        awaitClose {
            try {
                manager.stopServiceDiscovery(discoveryListener)
            } catch (_: Exception) { }
        }
    }

    companion object {
        private const val TAG = "NsdHelper"
    }
}

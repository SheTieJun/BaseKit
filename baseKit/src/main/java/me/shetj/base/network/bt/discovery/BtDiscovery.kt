package me.shetj.base.network.bt.discovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import me.shetj.base.ktx.logI
import me.shetj.base.network.bt.model.BtDevice

/**
 * 蓝牙设备发现
 * - 扫描范围：已配对设备 + 附近可发现设备
 * - 通过 Flow 持续输出设备列表
 */
internal class BtDiscovery(
    private val context: Context,
) {
    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    /** 开始扫描设备 */
    fun discover(): Flow<List<BtDevice>> = callbackFlow {
        val bt = adapter
        if (bt == null || !bt.isEnabled) {
            close()
            return@callbackFlow
        }

        val discovered = mutableMapOf<String, BtDevice>()

        // 先加入已配对设备
        bt.bondedDevices?.forEach { device ->
            val d = toBtDevice(device)
            if (d != null) discovered[d.address] = d
        }
        trySend(discovered.values.toList())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE,BluetoothDevice::class.java) ?: return
                        } else {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return
                        }
                        val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, 0.toShort()).toInt()
                        val d = toBtDevice(device, rssi) ?: return
                        discovered[d.address] = d
                        trySend(discovered.values.toList())
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        "BT discovery cycle finished, found ${discovered.size} devices".logI(TAG)
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(receiver, filter)

        // 循环扫描：每次扫描约 12 秒
        launchScanLoop(bt)

        awaitClose {
            try { context.unregisterReceiver(receiver) } catch (_: Exception) { }
            try { bt.cancelDiscovery() } catch (_: Exception) { }
        }
    }

    /** 循环扫描，每轮间隔 3 秒 */
    private suspend fun launchScanLoop(bt: BluetoothAdapter) {
        while (true) {
            if (bt.isDiscovering) bt.cancelDiscovery()
            if (bt.startDiscovery()) {
                "BT discovery started".logI(TAG)
            }
            // 等待扫描完成（默认 12 秒）+ 间隔 3 秒
            delay(15_000)
        }
    }

    private fun toBtDevice(device: BluetoothDevice, rssi: Int = 0): BtDevice? {
        val name = device.name ?: device.address ?: return null
        return BtDevice(
            name = name,
            address = device.address,
            rssi = rssi,
            bonded = device.bondState == BluetoothDevice.BOND_BONDED,
        )
    }

    companion object {
        private const val TAG = "BtDiscovery"
    }
}

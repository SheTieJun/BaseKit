package me.shetj.base.receiver

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.provider.Settings.System.ALARM_ALERT
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 蓝牙或者耳机 状态
 */
class HeadsetBroadcastReceive : BroadcastReceiver() {

    private var listener: HeadsetListener? = null
    private val filter = IntentFilter().apply {
        addAction(Intent.ACTION_SCREEN_OFF)
        addAction(ALARM_ALERT)
        addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    }
    private val isRegister:AtomicBoolean = AtomicBoolean(false)

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                //连接
                listener?.connected()
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                //断开
                listener?.disConnected()
            }
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                when (blueState) {
                    BluetoothAdapter.STATE_OFF -> {
                        //关闭
                        listener?.blueState(false)
                    }
                    BluetoothAdapter.STATE_ON -> {
                        //打开
                        listener?.blueState(true)
                    }
                }
            }
            AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                //耳机拔出时，可以暂停视频播放或做其他事情
                listener?.becomingNoisy()
            }
        }
    }

    fun setListener(listener: HeadsetListener) {
        this.listener = listener
    }


    fun registerReceiver(context: Context) {
        if (isRegister.compareAndSet(false,true)) {
            context.registerReceiver(this, filter)
        }
    }

    fun unRegister(context: Context){
        if (isRegister.compareAndSet(true,false)) {
            context.unregisterReceiver(this)
        }
    }

    interface HeadsetListener {

        /**
         * 断开连接
         */
        fun disConnected(){

        }

        /**
         * 连接
         */
        fun connected(){

        }

        /**
         * 耳机拔出
         */
        fun becomingNoisy(){

        }

        /**
         * 蓝牙状态
         */
        fun blueState(isOpen:Boolean){

        }
    }

}
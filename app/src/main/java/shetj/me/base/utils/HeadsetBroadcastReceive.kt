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


package shetj.me.base.utils

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
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
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
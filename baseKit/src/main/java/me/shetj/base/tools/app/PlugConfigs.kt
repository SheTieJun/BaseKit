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
package me.shetj.base.tools.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import java.util.concurrent.atomic.AtomicBoolean
import me.shetj.base.model.PlugLiveData

/**
 * 用来判断是否连接上了耳机
 */
@Suppress("DEPRECATION")
internal class PlugConfigs(val context: Context, var connected: Boolean = false) {

    private val isRegister = AtomicBoolean(false)
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_HEADSET_PLUG) {
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        getInstance(context).connected = false
                        PlugLiveData.getInstance().disConnect()
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        PlugLiveData.getInstance().connect()
                    }
                }
            }
        }
    }
    private val intentFilter = IntentFilter(Intent.ACTION_HEADSET_PLUG)

    fun registerReceiver() {
        if (isRegister.compareAndSet(false, true)) {
            connected = audioManager.isWiredHeadsetOn
            PlugLiveData.getInstance().postValue(connected)
            context.registerReceiver(mReceiver, intentFilter)
        }
    }

    fun unregisterReceiver() {
        if (isRegister.compareAndSet(true, false)) {
            context.unregisterReceiver(mReceiver)
        }
    }

    fun getMaxVoice() = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    fun setAudioVoice(volume: Int) {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            volume,
            0
        )
    }

    companion object {
        @Volatile
        private var sInstance: PlugConfigs? = null

        fun getInstance(context: Context): PlugConfigs {
            return sInstance ?: synchronized(PlugConfigs::class.java) {
                return PlugConfigs(context).also {
                    it.connected = it.audioManager.isWiredHeadsetOn
                    PlugLiveData.getInstance().postValue(it.connected)
                    sInstance = it
                }
            }
        }

        fun onDestroy() {
            sInstance?.unregisterReceiver()
            sInstance = null
        }
    }
}

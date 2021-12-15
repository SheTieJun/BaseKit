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
import androidx.annotation.FloatRange
import androidx.core.math.MathUtils
import java.util.concurrent.atomic.AtomicBoolean
import me.shetj.base.model.VolumeLiveData

/**
 * 声音音量控制
 */
internal class VolumeConfig(val context: Context) {

    private val isRegister = AtomicBoolean(false)
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals("android.media.VOLUME_CHANGED_ACTION")) {
                VolumeLiveData.getInstance().postValue(getCurVolume())
            }
        }
    }

    private val intentFilter = IntentFilter().apply {
        addAction("android.media.VOLUME_CHANGED_ACTION")
    }

    fun getCurVolume(): Int {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    fun registerReceiver() {
        if (isRegister.compareAndSet(false, true)) {
            context.registerReceiver(mReceiver, intentFilter)
        }
    }

    fun unregisterReceiver() {
        // 回到最初的声音
        if (isRegister.compareAndSet(true, false)) {
            context.unregisterReceiver(mReceiver)
        }
    }

    fun getMaxVoice() = max

    fun setAudioVoiceF(@FloatRange(from = 0.0, to = 1.0) volume: Float) {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (MathUtils.clamp(volume, 0f, 1f) * max).toInt(),
            0
        )
    }

    fun setAudioVoice(volume: Int) {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            MathUtils.clamp(volume, 0, max),
            0
        )
    }

    companion object {
        @Volatile
        private var sInstance: VolumeConfig? = null

        fun getInstance(context: Context): VolumeConfig {
            return sInstance ?: synchronized(VolumeConfig::class.java) {
                return VolumeConfig(context.applicationContext).also {
                    VolumeLiveData.getInstance().postValue(it.getCurVolume())
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

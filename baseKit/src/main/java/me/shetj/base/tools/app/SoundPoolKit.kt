package me.shetj.base.tools.app

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import androidx.annotation.RawRes

/**
 * 用来播放音效
 */
object SoundPoolKit {
    fun playMusic(context: Context, @RawRes id: Int): SoundPool {
        // sdk版本21是SoundPool 的一个分水岭
        val builder = SoundPool.Builder()
        // 传入最多播放音频数量,
        builder.setMaxStreams(1)
        // AudioAttributes是一个封装音频各种属性的方法
        val attrBuilder = AudioAttributes.Builder()
        // 设置音频流的合适的属性
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        // 加载一个AudioAttributes
        builder.setAudioAttributes(attrBuilder.build())
        val soundPool: SoundPool = builder.build()

        // 可以通过四种途径来记载一个音频资源：
        // 1.通过一个AssetFileDescriptor对象
        // int load(AssetFileDescriptor afd, int priority)
        // 2.通过一个资源ID
        // int load(Context context, int resId, int priority)
        // 3.通过指定的路径加载
        // int load(String path, int priority)
        // 4.通过FileDescriptor加载
        // int load(FileDescriptor fd, long offset, long length, int priority)
        // 声音ID 加载音频资源,这里用的是第二种，第三个参数为priority，声音的优先级*API中指出，priority参数目前没有效果，建议设置为1。
        val voiceId = soundPool.load(context, id, 1)
        // 异步需要等待加载完成，音频才能播放成功
        soundPool.setOnLoadCompleteListener { pool, _, _ ->
            // 第一个参数soundID
            // 第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
            // 第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
            // 第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
            // 第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
            // 第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
            pool.play(voiceId, 1f, 1f, 1, 0, 1f)
        }
        return soundPool
    }
}

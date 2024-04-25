package me.shetj.base.media3.listener

import android.os.Handler
import android.os.Looper
import androidx.media3.common.Metadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

/**
 * Position play listener
 * 主要是用来监听播放的位置，也可以自己扩展
 */
open class PositionPlayListener : Player.Listener {

    protected var player: Player? = null

    /**
     * Handler 这里只是demo,实际使用的时候，需要根据实际情况处理, 不应该创建太多个重复功能的Handler
     */
    val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: android.os.Message) {
            super.handleMessage(msg)
            when (msg.what) {
                Player.STATE_READY -> {
                    onPositionChanged(getPosition(), getDuration())
                    sendEmptyMessageDelayed(Player.STATE_READY, 1000)
                }

                Player.STATE_ENDED -> {
                    removeMessages(Player.STATE_READY)
                }
            }
        }
    }

    private fun getDuration(): Long {
        return player?.duration ?: 0L
    }

    private fun getPosition(): Long {
        return player?.currentPosition ?: 0L
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)
        if (this.player == null) {
            this.player = player
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        handler.sendEmptyMessage(if (isPlaying) Player.STATE_READY else Player.STATE_ENDED)
    }

    @UnstableApi
    override fun onMetadata(metadata: Metadata) {
        super.onMetadata(metadata)
    }

    open fun onPositionChanged(position: Long, duration: Long) {
    }

}
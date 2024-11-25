package shetj.me.base.record

import android.text.TextUtils
import me.shetj.base.BaseKit
import me.shetj.base.lifecycle.AbLifecycleCopeComponent
import me.shetj.player.PlayerListener
import me.shetj.recorder.core.AudioUtils
import me.shetj.recorder.core.BaseRecorder
import me.shetj.recorder.core.FileUtils
import me.shetj.recorder.core.PermissionListener
import me.shetj.recorder.core.RecordListener
import me.shetj.recorder.core.RecordState
import me.shetj.recorder.core.SimRecordListener
import me.shetj.recorder.core.recorder
import me.shetj.recorder.mixRecorder.build

/**
 * 录音工具类
 */
class RecordUtils(
    private val maxTime: Long = 30 * 60 * 1000,
    private var callBack: SimRecordListener? = null
) : AbLifecycleCopeComponent(), RecordListener, PermissionListener {

    val isRecording: Boolean
        get() {
            return mRecorder!!.state == RecordState.RECORDING
        }

    fun hasRecord(): Boolean {
        return if (mRecorder != null) {
            mRecorder?.duration!! > 0 && mRecorder!!.state != RecordState.STOPPED
        } else {
            false
        }
    }

    fun isHasBGM() = hasBGM

    init {
        initRecorder()
    }

    private var startTime: Long = 0 //秒 s
    private var mRecorder: BaseRecorder? = null
    private var hasBGM: Boolean = false
    var saveFile: String? = null
        private set

    @JvmOverloads
    fun startOrPause(file: String = "") {
        if (mRecorder == null) {
            initRecorder()
        }
        when (mRecorder?.state) {
            RecordState.STOPPED -> {
                if (TextUtils.isEmpty(file)) {
                    val mRecordFile = BaseKit.app.cacheDir.absolutePath +"/"+System.currentTimeMillis()+"-b.mp3"
                    this.saveFile = mRecordFile
                } else {
                    this.saveFile = file
                }
                mRecorder?.setOutputFile(saveFile!!, !TextUtils.isEmpty(file))
                mRecorder?.start()
            }
            RecordState.PAUSED -> {
                mRecorder?.resume()
            }
            RecordState.RECORDING -> {
                mRecorder?.pause()
            }
            else ->{ }
        }
    }

    @JvmOverloads
    fun startOrComplete(file: String = "") {
        if (mRecorder == null) {
            initRecorder()
        }
        when (mRecorder?.state) {
            RecordState.STOPPED -> {
                if (TextUtils.isEmpty(file)) {
                    val mRecordFile = BaseKit.app.cacheDir.absolutePath +"/"+System.currentTimeMillis()+"-b.mp3"
                    this.saveFile = mRecordFile
                } else {
                    this.saveFile = file
                }
                mRecorder?.setOutputFile(saveFile!!, !TextUtils.isEmpty(file))
                mRecorder?.start()
                startBGM()
            }
            RecordState.RECORDING -> {
                mRecorder?.complete()
            }
            else ->{}
        }
    }

    /**
     * VOICE_COMMUNICATION 消除回声和噪声问题
     * MIC 麦克风- 因为有噪音问题
     */
    private fun initRecorder() {
        mRecorder = recorder {
            samplingRate = 48000
            audioChannel = 1
            mp3BitRate = 128
            recordListener = this@RecordUtils
            permissionListener = this@RecordUtils
            enableAudioEffect = true
            isDebug = false
        }.build(BaseKit.app)
        mRecorder?.setMaxTime(maxTime, 60000)
    }

    fun isPause(): Boolean {
        return mRecorder?.state == RecordState.PAUSED
    }

    fun setBackgroundPlayerListener(listener: PlayerListener) {
        mRecorder?.setBackgroundMusicListener(listener)
    }

    fun updateCallBack(callBack: SimRecordListener?){
        this.callBack = callBack
    }

    fun pause() {
        mRecorder?.pause()
    }

    fun clear() {
        mRecorder?.destroy()
    }

    fun reset() {
        mRecorder?.reset()
    }

    fun cleanPath() {
        saveFile?.let {
            FileUtils.deleteFile(it)
            saveFile = null
        }
    }

    /**
     * 录音异常
     */
    private fun resolveError() {
        if (mRecorder != null && mRecorder!!.isActive) {
            mRecorder!!.complete()
        }
        cleanPath()
    }

    fun setBGMUrl(url: String?) {
        hasBGM = url != null
        url?.let {
            mRecorder?.setAudioChannel(AudioUtils.getAudioChannel(url))
            mRecorder?.setLoopMusic(true)
            mRecorder?.setBackgroundMusic(it)
        }
    }

    fun setMaxTime(maxTime: Long){
        mRecorder?.setMaxTime(maxTime,3000)
    }

    /**
     * 停止录音
     */
    fun completeRecord() {
        mRecorder?.complete()
    }

    override fun needPermission() {
        callBack?.needPermission()
    }

    override fun onStart() {
        super.onStart()
        callBack?.onStart()
    }

    private fun startBGM() {
        if (hasBGM) {
            //如果设置背景应用
            if (mRecorder?.isPlayMusic() == false) {
                mRecorder?.startPlayMusic()
            }
        }
    }

    override fun onSuccess(isAutoComplete: Boolean, file: String, time: Long) {
        callBack?.onSuccess(isAutoComplete, file, time)
    }

    override fun onResume() {
        super.onResume()
        callBack?.onResume()
    }

    override fun onReset() {
        onStop()
        callBack?.onReset()
    }

    override fun onRecording(time: Long, volume: Int) {
        callBack?.onRecording(startTime + time, volume)
    }

    override fun onPause() {
        super.onPause()
        callBack?.onPause()
    }


    override fun onRemind(duration: Long) {
        callBack?.onRemind(duration)
    }


    override fun onMaxChange(time: Long) {
        callBack?.onMaxChange(time)
    }

    override fun onError(e: Exception) {
        onStop()
        resolveError()
        e.printStackTrace()
        callBack?.onError(e)
    }


    fun setVolume(volume: Float) {
        mRecorder?.setBGMVolume(volume)
    }

    fun destroy() {
        onDeStory()
        callBack = null
        mRecorder?.destroy()
    }


}

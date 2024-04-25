package me.shetj.base.media3.kit

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Rational
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.media3.session.MediaController
import me.shetj.base.R
import me.shetj.base.media3.MediaX
import java.lang.ref.WeakReference

class PicInPicKit() {

    private val  controller: MediaController?
        get() = MediaX.getMediaBrowser()
    private var weakReference: WeakReference<FragmentActivity>? = null
    private val broadcastReceiver = object : BroadcastReceiver() {

        // Called when an item is clicked.
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || intent.action != ACTION_STOPWATCH_CONTROL) {
                return
            }
            when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                CONTROL_TYPE_START_OR_PAUSE -> startOrPause()
                CONTROL_TYPE_CLEAR -> clear()
            }
        }
    }

    companion object {
        private const val ACTION_STOPWATCH_CONTROL = "stopwatch_control"

        /** Intent extra for stopwatch controls from Picture-in-Picture mode.  */
        private const val EXTRA_CONTROL_TYPE = "control_type"
        private const val CONTROL_TYPE_CLEAR = 1
        private const val CONTROL_TYPE_START_OR_PAUSE = 2

        private const val REQUEST_CLEAR = 3
        private const val REQUEST_START_OR_PAUSE = 4

        val enablePicInPic = MutableLiveData(false)

        fun iWantToBeInPipModeNow(): Boolean {
            return enablePicInPic.value == true
        }

        fun enablePicInPic() {
            enablePicInPic.postValue(true)
        }

        fun disablePicInPic() {
            enablePicInPic.postValue(false)
        }
    }

    private fun startOrPause() {
        if (controller?.isPlaying == true) {
            controller?.pause()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity()?.setPictureInPictureParams(updatePictureInPictureParams(getActivity()!!, false))
            }
        } else {
            controller?.play()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity()?.setPictureInPictureParams(updatePictureInPictureParams(getActivity()!!, started = true))
            }
        }
    }

    private fun clear() {
        controller?.seekTo(0)
    }


    fun getActivity(): FragmentActivity? {
        return weakReference?.get()
    }

    fun registerReceiver(context: FragmentActivity) {
        weakReference = WeakReference(context)
        context.lifecycle.addObserver(observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_CREATE) {
                    context.registerReceiver(broadcastReceiver, IntentFilter(ACTION_STOPWATCH_CONTROL))
                } else if (event == Lifecycle.Event.ON_DESTROY) {
                    context.unregisterReceiver(broadcastReceiver)
                }
            }
        })
    }


    fun canUsePicInPic(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) && iWantToBeInPipModeNow()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePictureInPictureParams(activity: FragmentActivity, started: Boolean): PictureInPictureParams {
        val params = PictureInPictureParams.Builder()
            // Set action items for the picture-in-picture mode. These are the only custom controls
            // available during the picture-in-picture mode.
            .setActions(
                listOf(
                    // "Clear" action.
                    createRemoteAction(
                        activity,
                        R.drawable.icon_refresh,
                        R.string.clear,
                        REQUEST_CLEAR,
                        CONTROL_TYPE_CLEAR
                    ),
                    if (started) {
                        // "Pause" action when the stopwatch is already started.
                        createRemoteAction(
                            activity,
                            R.drawable.media3_notification_pause,
                            R.string.pause,
                            REQUEST_START_OR_PAUSE,
                            CONTROL_TYPE_START_OR_PAUSE
                        )
                    } else {
                        // "Start" action when the stopwatch is not started.
                        createRemoteAction(
                            activity,
                            R.drawable.media3_notification_play,
                            R.string.start,
                            REQUEST_START_OR_PAUSE,
                            CONTROL_TYPE_START_OR_PAUSE
                        )
                    }
                )
            )
            // Set the aspect ratio of the picture-in-picture mode.
            .setAspectRatio(Rational(16, 9))
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    //从 Android 12 开始，您可以使用 setAutoEnterEnabled 标志，在手势导航模式下向上滑动转到主屏幕时，更流畅地过渡到画中画模式。
                    setAutoEnterEnabled(true)
                    setSeamlessResizeEnabled(false)
                }
            }
            .build()
        activity.setPictureInPictureParams(params)
        return params
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createRemoteAction(
        activity: FragmentActivity,
        @DrawableRes iconResId: Int,
        @StringRes titleResId: Int,
        requestCode: Int,
        controlType: Int
    ): RemoteAction {
        return RemoteAction(
            /* icon = */ Icon.createWithResource(activity, iconResId),
            /* title = */ activity.getString(titleResId),
            /* contentDescription = */ activity.getString(titleResId),
            /* intent = */ PendingIntent.getBroadcast(
                activity,
                requestCode,
                Intent(ACTION_STOPWATCH_CONTROL)
                    .putExtra(EXTRA_CONTROL_TYPE, controlType),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun enterPictureInPictureMode(activity: FragmentActivity) {
        activity.enterPictureInPictureMode(updatePictureInPictureParams(activity, controller?.isPlaying ?: false))
    }
}
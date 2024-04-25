package me.shetj.base.media3

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSourceBitmapLoader
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.CacheBitmapLoader
import androidx.media3.session.CommandButton
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import me.shetj.base.R
import me.shetj.base.media3.kit.PlayServiceHelper
import me.shetj.base.media3.kit.VideoUtils

class PlaybackService : MediaLibraryService() {
  private val librarySessionCallback = CustomMediaLibrarySessionCallback()

  private lateinit var player: ExoPlayer
  private lateinit var mediaLibrarySession: MediaLibrarySession
  private lateinit var customCommands: List<CommandButton>

  companion object {
    private const val SEARCH_QUERY_PREFIX_COMPAT = "androidx://media3-session/playFromSearch"
    private const val SEARCH_QUERY_PREFIX = "androidx://media3-session/setMediaUri"
    private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON =
      "android.media3.session.demo.SHUFFLE_ON"
    private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF =
      "android.media3.session.demo.SHUFFLE_OFF"
    private const val NOTIFICATION_ID = 123
    private const val CHANNEL_ID = "Media3"
    val immutableFlag = PendingIntent.FLAG_IMMUTABLE
  }

  @UnstableApi
  override fun onCreate() {
    super.onCreate()
    customCommands =
      listOf(
        getShuffleCommandButton(
            SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON, Bundle.EMPTY)
        ),
        getShuffleCommandButton(
            SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF, Bundle.EMPTY)
        )
      )
    initializeSessionAndPlayer()
    setListener(MediaSessionServiceListener())
  }

  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
    return mediaLibrarySession
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    if (!player.playWhenReady || player.mediaItemCount == 0) {
      stopSelf()
    }
  }
  @UnstableApi
  override fun onDestroy() {
    mediaLibrarySession.setSessionActivity(getBackStackedActivity())
    mediaLibrarySession.release()
    player.release()
    clearListener()
    super.onDestroy()
  }
  private inner class CustomMediaLibrarySessionCallback : MediaLibrarySession.Callback {

    @UnstableApi
    override fun onConnect(session: MediaSession, controller: MediaSession.ControllerInfo): MediaSession.ConnectionResult {
      val availableSessionCommands =
        MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()
      for (commandButton in customCommands) {
        // Add custom command to available session commands.
        commandButton.sessionCommand?.let { availableSessionCommands.add(it) }
      }
      return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
        .setAvailableSessionCommands(availableSessionCommands.build())
        .build()
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
      if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON == customCommand.customAction) {
        // Enable shuffling.
        player.shuffleModeEnabled = true
        // Change the custom layout to contain the `Disable shuffling` command.
        session.setCustomLayout(ImmutableList.of(customCommands[1]))
      } else if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF == customCommand.customAction) {
        // Disable shuffling.
        player.shuffleModeEnabled = false
        // Change the custom layout to contain the `Enable shuffling` command.
        session.setCustomLayout(ImmutableList.of(customCommands[0]))
      }
      return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
      if (params != null && params.isRecent) {
        // The service currently does not support playback resumption. Tell System UI by returning
        // an error of type 'RESULT_ERROR_NOT_SUPPORTED' for a `params.isRecent` request. See
        // https://github.com/androidx/media/issues/355
        return Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED))
      }
      return Futures.immediateFuture(LibraryResult.ofItem(MediaX.getMediaItemFactory().getRootItem(), params))
    }

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> {
      val item =
        MediaX.getMediaItemFactory().getItem(mediaId)
          ?: return Futures.immediateFuture(
              LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
          )
      return Futures.immediateFuture(LibraryResult.ofItem(item, /* params= */ null))
    }

    override fun onSubscribe(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        params: LibraryParams?
    ): ListenableFuture<LibraryResult<Void>> {
      val children =
        MediaX.getMediaItemFactory().getChildren(parentId)
          ?: return Futures.immediateFuture(
              LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
          )
      session.notifyChildrenChanged(browser, parentId, children.size, params)
      return Futures.immediateFuture(LibraryResult.ofVoid())
    }

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
      val children =
        MediaX.getMediaItemFactory().getChildren(parentId)
          ?: return Futures.immediateFuture(
              LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
          )

      return Futures.immediateFuture(LibraryResult.ofItemList(children, params))
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: List<MediaItem>
    ): ListenableFuture<List<MediaItem>> {
      return Futures.immediateFuture(mediaItems)
    }

    private fun getMediaItemFromSearchQuery(query: String): MediaItem {
      // Only accept query with pattern "play [Title]" or "[Title]"
      // Where [Title]: must be exactly matched
      // If no media with exact name found, play a random media instead
      val mediaTitle =
        if (query.startsWith("play ", ignoreCase = true)) {
          query.drop(5)
        } else {
          query
        }

      return MediaX.getMediaItemFactory().getItemFromTitle(mediaTitle)
    }
  }
  @UnstableApi
  private fun initializeSessionAndPlayer() {
    //ProgressiveMediaSource：用于播放本地或网络上的普通（非流式）媒体文件
   // DefaultHttpDataSource： 用于从网络上加载媒体数据，支持自定义HTTP请求头和代理设置，支持带有缓存的数据加载

    player =
      ExoPlayer.Builder(this)
        .setHandleAudioBecomingNoisy(true) // 自动暂停播放
        .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true) // 自动处理音频焦点
        .setMediaSourceFactory(DefaultMediaSourceFactory(DefaultDataSource.Factory(this, VideoUtils.getCacheFactory(this))))
//        .setMediaSourceFactory(HlsMediaSource.Factory(DefaultHlsDataSourceFactory(DefaultDataSource.Factory(this))))
        .build()

    mediaLibrarySession =
      MediaLibrarySession.Builder(this, player, librarySessionCallback)
        .setSessionActivity(PlayServiceHelper.getSingleTopActivity(this))
        .setCustomLayout(ImmutableList.of(customCommands[0]))
        .setBitmapLoader(CacheBitmapLoader(DataSourceBitmapLoader(/* context= */ this)))
        .build()
  }


  private fun getBackStackedActivity(): PendingIntent {
    return TaskStackBuilder.create(this).run {
      this@PlaybackService.packageManager.getLaunchIntentForPackage(this@PlaybackService.packageName)?.let { addNextIntent(it) }
      addNextIntent(PlayServiceHelper.getSessionActivityIntent(this@PlaybackService))
      getPendingIntent(0, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT)!!
    }
  }

  @SuppressLint("PrivateResource")
  private fun getShuffleCommandButton(sessionCommand: SessionCommand): CommandButton {
    val isOn = sessionCommand.customAction == CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON
    return CommandButton.Builder()
      .setDisplayName(
        getString(
          if (isOn) androidx.media3.ui.R.string.exo_controls_shuffle_on_description
          else androidx.media3.ui.R.string.exo_controls_shuffle_off_description
        )
      )
      .setSessionCommand(sessionCommand)
      .setIconResId(if (isOn) androidx.media3.ui.R.drawable.exo_icon_shuffle_off else androidx.media3.ui.R.drawable.exo_icon_shuffle_on)
      .build()
  }

  @UnstableApi
  private inner class MediaSessionServiceListener : Listener {

    /**
     * This method is only required to be implemented on Android 12 or above when an attempt is made
     * by a media controller to resume playback when the {@link MediaSessionService} is in the
     * background.
     */
    @SuppressLint("MissingPermission") // TODO: b/280766358 - Request this permission at runtime.
    override fun onForegroundServiceStartNotAllowedException() {
      val notificationManagerCompat = NotificationManagerCompat.from(this@PlaybackService)
      ensureNotificationChannel(notificationManagerCompat)
      val pendingIntent =
        TaskStackBuilder.create(this@PlaybackService).run {
          addNextIntent(PlayServiceHelper.getSessionActivityIntent(this@PlaybackService))
          getPendingIntent(0, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT)
        }

      val builder =
        NotificationCompat.Builder(this@PlaybackService, CHANNEL_ID)
          .setContentIntent(pendingIntent)
          .setSmallIcon(androidx.media3.session.R.drawable.media3_notification_small_icon)
          .setContentTitle(getString(R.string.notification_content_title))
          .setStyle(
            NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_content_text))
          )
          .setPriority(NotificationCompat.PRIORITY_DEFAULT)
          .setAutoCancel(true)
      notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
    }
  }
  @UnstableApi
  private fun ensureNotificationChannel(notificationManagerCompat: NotificationManagerCompat) {
    if (Util.SDK_INT < 26 || notificationManagerCompat.getNotificationChannel(CHANNEL_ID) != null) {
      return
    }

    val channel =
        NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
    notificationManagerCompat.createNotificationChannel(channel)
  }
}
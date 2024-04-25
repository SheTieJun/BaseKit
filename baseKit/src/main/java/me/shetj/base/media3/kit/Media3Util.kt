package me.shetj.base.media3.kit

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.datasource.cronet.CronetUtil
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.DefaultRenderersFactory.ExtensionRendererMode
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import java.io.File
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.Executors

@UnstableApi
object Media3Util {
    private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"

    /**
     * Whether the demo application uses Cronet for networking. Note that Cronet does not provide
     * automatic support for cookies (https://github.com/google/ExoPlayer/issues/5975).
     *
     *
     * If set to false, the platform's default network stack is used with a [CookieManager]
     * configured in [.getHttpDataSourceFactory].
     */
    private const val USE_CRONET_FOR_NETWORKING = true
    private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
    private var dataSourceFactory: DataSource.Factory? = null
    private var httpDataSourceFactory: DataSource.Factory? = null
    private var databaseProvider: DatabaseProvider? = null
    private var downloadDirectory: File? = null
    private var downloadCache: Cache? = null
    private var downloadManager: DownloadManager? = null
    private var downloadNotificationHelper: DownloadNotificationHelper? = null

    /**
     * Returns whether extension renderers should be used.
     */
    fun useExtensionRenderers(): Boolean {
        return true
    }

    fun buildRenderersFactory(
        context: Context, preferExtensionRenderer: Boolean
    ): RenderersFactory {
        val extensionRendererMode: @ExtensionRendererMode Int =
            if (useExtensionRenderers()) (if (preferExtensionRenderer) DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER else DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON) else DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        return DefaultRenderersFactory(context.applicationContext)
            .setExtensionRendererMode(extensionRendererMode)
    }

    @Synchronized
    fun getHttpDataSourceFactory(context: Context): DataSource.Factory {
        if (httpDataSourceFactory == null) {
            if (USE_CRONET_FOR_NETWORKING) {
                val cronetEngine = CronetUtil.buildCronetEngine( context.applicationContext)
                if (cronetEngine != null) {
                    httpDataSourceFactory = CronetDataSource.Factory(cronetEngine, Executors.newSingleThreadExecutor())
                }
            }
            if (httpDataSourceFactory == null) {
                // We don't want to use Cronet, or we failed to instantiate a CronetEngine.
                val cookieManager = CookieManager()
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
                CookieHandler.setDefault(cookieManager)
                httpDataSourceFactory = DefaultHttpDataSource.Factory()
            }
        }
        return httpDataSourceFactory!!
    }

    /**
     * Returns a [DataSource.Factory].
     */
    @Synchronized
    fun getDataSourceFactory(context: Context): DataSource.Factory? {
        val applicationContext =  context.applicationContext
        if (dataSourceFactory == null) {
            val upstreamFactory = DefaultDataSource.Factory(context, getHttpDataSourceFactory(applicationContext))
            dataSourceFactory = buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache(applicationContext))
        }
        return dataSourceFactory
    }

    @JvmStatic
    @Synchronized
    fun getDownloadNotificationHelper(
        context: Context?
    ): DownloadNotificationHelper? {
        if (downloadNotificationHelper == null) {
            downloadNotificationHelper = DownloadNotificationHelper(context!!, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
        }
        return downloadNotificationHelper
    }

    @JvmStatic
    @Synchronized
    fun getDownloadManager(context: Context): DownloadManager? {
        ensureDownloadManagerInitialized(context)
        return downloadManager
    }


    @OptIn(UnstableApi::class)
    @Synchronized
    fun getDownloadCache(context: Context): Cache {
        if (downloadCache == null) {
            val downloadContentDirectory = File(getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY)
            downloadCache = SimpleCache(
                downloadContentDirectory, NoOpCacheEvictor(), getDatabaseProvider(context)!!
            )
        }
        return downloadCache!!
    }

    @OptIn(UnstableApi::class)
    @Synchronized
    private fun ensureDownloadManagerInitialized(context: Context) {
        if (downloadManager == null) {
            downloadManager = DownloadManager(
                context,
                getDatabaseProvider(context)!!,
                getDownloadCache(context),
                getHttpDataSourceFactory(context),
                Executors.newFixedThreadPool( /* nThreads= */6)
            )
        }
    }

    @OptIn(UnstableApi::class)
    @Synchronized
    fun getDatabaseProvider(context: Context?): DatabaseProvider? {
        if (databaseProvider == null) {
            databaseProvider = StandaloneDatabaseProvider(context!!)
        }
        return databaseProvider
    }

    @Synchronized
    private fun getDownloadDirectory(context: Context): File? {
        if (downloadDirectory == null) {
            downloadDirectory = context.getExternalFilesDir( /* type= */null)
            if (downloadDirectory == null) {
                downloadDirectory = context.filesDir
            }
        }
        return downloadDirectory
    }

    @OptIn(UnstableApi::class)
    private fun buildReadOnlyCacheDataSource(
        upstreamFactory: DataSource.Factory, cache: Cache?
    ): CacheDataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(cache!!)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
}
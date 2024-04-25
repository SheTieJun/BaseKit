package me.shetj.base.media3.kit

import android.content.Context
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource

object VideoUtils {
    private var cacheFactory: DataSource.Factory? = null

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun getCacheFactory(ctx: Context): DataSource.Factory {
        if (cacheFactory == null) {
            cacheFactory = CacheDataSource.Factory()
                //和downloadCache一样的，就可以共用了
                .setCache(Media3Util.getDownloadCache(ctx))
                .setUpstreamDataSourceFactory(
                    DefaultHttpDataSource.Factory()
                        .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                        .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
//                    .setAllowCrossProtocolRedirects(allowCrossProtocolRedirects)
                )
        }
        return cacheFactory!!
    }

}
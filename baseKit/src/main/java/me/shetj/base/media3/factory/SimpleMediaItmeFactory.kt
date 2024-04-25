package me.shetj.base.media3.factory

import androidx.media3.common.MediaItem
import me.shetj.base.media3.factory.MediaItemFactory
import me.shetj.base.media3.kit.MediaItemTree

class SimpleMediaItemFactory : MediaItemFactory {

    override fun getRootItem(): MediaItem {
        return MediaItemTree.getRootItem()
    }

    override fun getChildren(parentId: String): List<MediaItem>? {
        return MediaItemTree.getChildren(parentId)
    }

    override fun getItem(mediaId: String): MediaItem? {
        return MediaItemTree.getItem(mediaId)
    }

    override fun getItemFromTitle(mediaTitle: String): MediaItem {
        return MediaItemTree.getItemFromTitle(mediaTitle)?: MediaItemTree.getRandomItem()
    }
}
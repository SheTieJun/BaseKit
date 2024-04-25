package me.shetj.base.media3.factory

import androidx.media3.common.MediaItem


interface MediaItemFactory {

    fun getRootItem(): MediaItem
    fun getChildren(parentId: String): List<MediaItem>?

    fun getItem(mediaId: String): MediaItem?
    fun getItemFromTitle(mediaTitle: String): MediaItem
}
package me.shetj.base.media3.kit

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MediaMetadata
import com.google.common.collect.ImmutableList

object MediaItemTree {
  private var treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
  private var titleMap: MutableMap<String, MediaItemNode> = mutableMapOf()
  private var isInitialized = false
  private const val ROOT_ID = "[rootID]"
  private const val ALBUM_ID = "[albumID]"
  private const val GENRE_ID = "[genreID]"
  private const val ARTIST_ID = "[artistID]"
  private const val ALBUM_PREFIX = "[album]"
  private const val GENRE_PREFIX = "[genre]"
  private const val ARTIST_PREFIX = "[artist]"
  private const val ITEM_PREFIX = "[item]"

  private class MediaItemNode(val item: MediaItem) {
    private val children: MutableList<MediaItem> = ArrayList()

    fun addChild(childID: String) {
      this.children.add(treeNodes[childID]!!.item)
    }

    fun getChildren(): List<MediaItem> {
      return ImmutableList.copyOf(children)
    }
  }

  fun buildMediaItem(
    title: String,
    mediaId: String,
    isPlayable: Boolean,
    isBrowsable: Boolean,
    mediaType: @MediaMetadata.MediaType Int,
    subtitleConfigurations: List<SubtitleConfiguration> = mutableListOf(),
    album: String? = null,
    artist: String? = null,
    genre: String? = null,
    sourceUri: Uri? = null,
    imageUri: Uri? = null,
    mimeType: String? = null,
  ): MediaItem {
    val metadata =
      MediaMetadata.Builder()
        .setAlbumTitle(album)
        .setTitle(title)
        .setArtist(artist)
        .setGenre(genre)
        .setIsBrowsable(isBrowsable)
        .setIsPlayable(isPlayable)
        .setArtworkUri(imageUri)
        .setMediaType(mediaType)
        .build()

    return MediaItem.Builder()
      .setMediaId(mediaId)
      .setSubtitleConfigurations(subtitleConfigurations)
      .setMediaMetadata(metadata)
      .setMimeType(mimeType)
      .setUri(sourceUri)
      .build()
  }

  fun getItem(id: String): MediaItem? {
    return treeNodes[id]?.item
  }

  fun getRootItem(): MediaItem {
    return treeNodes[ROOT_ID]!!.item
  }

  fun getChildren(id: String): List<MediaItem>? {
    return treeNodes[id]?.getChildren()
  }

  fun getRandomItem(): MediaItem {
    var curRoot = getRootItem()
    while (curRoot.mediaMetadata.isBrowsable == true) {
      val children = getChildren(curRoot.mediaId)!!
      curRoot = children.random()
    }
    return curRoot
  }

  fun getItemFromTitle(title: String): MediaItem? {
    return titleMap[title]?.item
  }
}
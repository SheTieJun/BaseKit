package me.shetj.base.share

import androidx.annotation.StringDef

@StringDef(ShareContentType.TEXT, ShareContentType.IMAGE, ShareContentType.AUDIO, ShareContentType.VIDEO, ShareContentType.FILE)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class ShareContentType {
    companion object {
        /**
         * Share Text
         */
        const val TEXT = "text/plain"

        /**
         * Share Image
         */
        const val IMAGE = "image/*"

        /**
         * Share Audio
         */
        const val AUDIO = "audio/*"

        /**
         * Share Video
         */
        const val VIDEO = "video/*"

        /**
         * Share File
         */
        const val FILE = "*/*"
    }
}
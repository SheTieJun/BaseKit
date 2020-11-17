package me.shetj.base.share

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import timber.log.Timber

class Share private constructor(builder: Builder) {
    /**
     * Current activity
     */
    private val activity: Activity?

    /**
     * Share content type
     */
    @ShareContentType
    private val contentType: String

    /**
     * Share title
     */
    private var title: String?

    /**
     * Share file Uri
     */
    private val shareFileUri: Uri?

    /**
     * Share content text
     */
    private val contentText: String?

    /**
     * Share to special component PackageName
     */
    private val componentPackageName: String?

    /**
     * Share to special component ClassName
     */
    private val componentClassName: String?

    /**
     * Share complete onActivityResult requestCode
     */
    private val requestCode: Int

    /**
     * Forced Use System Chooser
     */
    private val forcedUseSystemChooser: Boolean

    /**
     * shareBySystem
     */
    fun shareBySystem() {
        if (checkShareParam()) {
            var shareIntent = createShareIntent()
            if (shareIntent == null) {
                Timber.tag(TAG).e(  "shareBySystem cancel.")
                return
            }
            if (title == null) {
                title = ""
            }
            if (forcedUseSystemChooser) {
                shareIntent = Intent.createChooser(shareIntent, title)
            }
            if (shareIntent!!.resolveActivity(activity!!.packageManager) != null) {
                try {
                    if (requestCode != -1) {
                        activity.startActivityForResult(shareIntent, requestCode)
                    } else {
                        activity.startActivity(shareIntent)
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).e( Log.getStackTraceString(e))
                }
            }
        }
    }

    private fun createShareIntent(): Intent? {
        var shareIntent: Intent? = Intent()
        shareIntent!!.action = Intent.ACTION_SEND
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        shareIntent.addCategory("android.intent.category.DEFAULT")
        if (!TextUtils.isEmpty(componentPackageName) && !TextUtils.isEmpty(componentClassName)) {
            val comp = ComponentName(componentPackageName!!, componentClassName!!)
            shareIntent.component = comp
        }
        when (contentType) {
            ShareContentType.TEXT -> {
                shareIntent.putExtra(Intent.EXTRA_TEXT, contentText)
                shareIntent.type = "text/plain"
            }
            ShareContentType.IMAGE, ShareContentType.AUDIO, ShareContentType.VIDEO, ShareContentType.FILE -> {
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.addCategory("android.intent.category.DEFAULT")
                shareIntent.type = contentType
                shareIntent.putExtra(Intent.EXTRA_STREAM, shareFileUri)
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            else -> {
                Timber.tag(TAG).e( "$contentType is not support share type.")
                shareIntent = null
            }
        }
        return shareIntent
    }

    private fun checkShareParam(): Boolean {
        if (activity == null) {
            Timber.tag(TAG).e( "activity is null.")
            return false
        }
        if (TextUtils.isEmpty(contentType)) {
            Timber.tag(TAG).e( "Share content type is empty.")
            return false
        }
        if (ShareContentType.Companion.TEXT == contentType) {
            if (TextUtils.isEmpty(contentText)) {
                Timber.tag(TAG).e( "Share text context is empty.")
                return false
            }
        } else {
            if (shareFileUri == null) {
                Timber.tag(TAG).e( "Share file path is null.")
                return false
            }
        }
        return true
    }


     open  class Builder(val activity: Activity) {
        @ShareContentType
        var contentType: String = ShareContentType.FILE
        var title: String? = null
        var componentPackageName: String? = null
        var componentClassName: String? = null
        var shareFileUri: Uri? = null
        var textContent: String? = null
        var requestCode = -1
        var forcedUseSystemChooser = true

        /**
         * Set Content Type
         * @param contentType [ShareContentType]
         * @return Builder
         */
        fun setContentType(@ShareContentType contentType: String): Builder {
            this.contentType = contentType
            return this
        }

        /**
         * Set Title
         * @param title title
         * @return Builder
         */
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        /**
         * Set share file path
         * @param shareFileUri shareFileUri
         * @return Builder
         */
        fun setShareFileUri(shareFileUri: Uri?): Builder {
            this.shareFileUri = shareFileUri
            return this
        }

        /**
         * Set text content
         * @param textContent  textContent
         * @return Builder
         */
        fun setTextContent(textContent: String?): Builder {
            this.textContent = textContent
            return this
        }

        /**
         * Set Share To Component
         * @param componentPackageName componentPackageName
         * @param componentClassName componentPackageName
         * @return Builder
         */
        fun setShareToComponent(componentPackageName: String?, componentClassName: String?): Builder {
            this.componentPackageName = componentPackageName
            this.componentClassName = componentClassName
            return this
        }

        /**
         * Set onActivityResult requestCode, default value is -1
         * @param requestCode requestCode
         * @return Builder
         */
        fun setOnActivityResult(requestCode: Int): Builder {
            this.requestCode = requestCode
            return this
        }

        /**
         * Forced Use System Chooser To Share
         * @param enable default is true
         * @return Builder
         */
        fun forcedUseSystemChooser(enable: Boolean): Builder {
            forcedUseSystemChooser = enable
            return this
        }

        /**
         * build
         * @return Share2
         */
        fun build(): Share {
            return Share(this)
        }
    }

    companion object {
        private const val TAG = "Share"

        @JvmOverloads
        @JvmStatic
        fun shareText(activity: Activity,title: String = "Share Text", content: String) {
            Builder(activity)
                    .setContentType(ShareContentType.TEXT)
                    .setTextContent(content)
                    .setTitle(title)
                    .build()
                    .shareBySystem()
        }

        @JvmOverloads
        @JvmStatic
        fun shareImage(activity: Activity,title: String = "Share Image", content: Uri) {
            Builder(activity)
                    .setContentType(ShareContentType.IMAGE)
                    .setShareFileUri(content) //.setShareToComponent("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI")
                    .setTitle(title)
                    .build()
                    .shareBySystem()
        }
        @JvmOverloads
        @JvmStatic
        fun shareAudio(activity: Activity,title: String = "Share Audio", content: Uri) {
            Builder(activity)
                    .setContentType(ShareContentType.AUDIO)
                    .setShareFileUri(content)
                    .setTitle(title)
                    .build()
                    .shareBySystem()
        }

        @JvmOverloads
        @JvmStatic
        fun shareVideo(activity: Activity,title: String = "Share Video", content: Uri) {
            Builder(activity)
                    .setContentType(ShareContentType.VIDEO)
                    .setShareFileUri(content)
                    .setTitle(title)
                    .build()
                    .shareBySystem()
        }

        @JvmOverloads
        @JvmStatic
        fun shareFile(activity: Activity,title: String = "Share File", content: Uri) {
            Builder(activity)
                    .setContentType(ShareContentType.FILE)
                    .setShareFileUri(content)
                    .setTitle("Share File")
                    .build()
                    .shareBySystem()
        }
    }

    init {
        activity = builder.activity
        contentType = builder.contentType
        title = builder.title
        shareFileUri = builder.shareFileUri
        contentText = builder.textContent
        componentPackageName = builder.componentPackageName
        componentClassName = builder.componentClassName
        requestCode = builder.requestCode
        forcedUseSystemChooser = builder.forcedUseSystemChooser
    }
}
package me.shetj.base.ktx

import android.app.Activity
import android.net.Uri
import me.shetj.base.share.Share.Builder
import me.shetj.base.share.ShareContentType

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2020/9/10 0010<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b>  <br>
 */


fun Activity.shareText(title: String = "Share Text", content: String) {
    Builder(this)
            .setContentType(ShareContentType.TEXT)
            .setTextContent(content)
            .setTitle(title)
            .build()
            .shareBySystem()
}


fun Activity.shareImage(title: String = "Share Image", content: Uri) {
    Builder(this)
            .setContentType(ShareContentType.IMAGE)
            .setShareFileUri(content) //.setShareToComponent("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI")
            .setTitle(title)
            .build()
            .shareBySystem()
}

fun Activity.shareAudio(title: String = "Share Audio", content: Uri) {
    Builder(this)
            .setContentType(ShareContentType.AUDIO)
            .setShareFileUri(content)
            .setTitle(title)
            .build()
            .shareBySystem()
}

fun Activity.shareVideo(title: String = "Share Video", content: Uri) {
    Builder(this)
            .setContentType(ShareContentType.VIDEO)
            .setShareFileUri(content)
            .setTitle(title)
            .build()
            .shareBySystem()
}

fun Activity.shareFile(tite: String = "Share File", content: Uri) {
    Builder(this)
            .setContentType(ShareContentType.FILE)
            .setShareFileUri(content)
            .setTitle("Share File")
            .build()
            .shareBySystem()
}



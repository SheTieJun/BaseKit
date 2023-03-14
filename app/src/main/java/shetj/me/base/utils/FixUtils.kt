

package shetj.me.base.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import me.shetj.base.ktx.showToast
import java.util.*


/**
 * 	 * 对于回调
 * 如果只是单纯禁止，则只提示授权失败，以后再去请求没问题；
 * 如果回调发现有 不再提示的，则needJump2setting=true；
 * 把刚才所有被禁止的权限都告诉用户，让用户手动打开
 */
fun Activity.onRequestPermissionsResult2(permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    val badResults = ArrayList<String>()
    var needJump2setting = false
    var i = 0
    val len = grantResults.size
    while (i < len) {
        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
            badResults.add(permissions[i])
            //有不在提示的
            if (!shouldShowRequestPermissionRationale(permissions[i])) needJump2setting = true
        }
        i++
    }
    if (badResults.isEmpty()) return
    if (!needJump2setting) {
        "授权失败".showToast()
        return
    }
    val transformBadResults = arrayOfNulls<String>(badResults.size)
    badResults.toArray(transformBadResults)
    val b = Bundle()
    b.putBoolean("jump2setting", true)
    b.putStringArray("giveAuthorizationBySelf", transformBadResults)

}

/**
 * 隐藏底部栏方法
 * qq 浏览器
 */
private fun WebView.hideBottom() {
    try {
        //定义javaScript方法
        val javascript = ("javascript:function hideBottom() { "
                + "document.getElementsByClassName('tvp_app_download_onpause')[0].style.display='none',"
                + "document.getElementsByClassName('tvp_app_download_onpause').parentNode.removeChild(document.getElementsByClassName('tvp_app_download_onpause'))"
                + "}")
        loadUrl(javascript)
        loadUrl("javascript:hideBottom();")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
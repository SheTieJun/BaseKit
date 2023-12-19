package me.shetj.base.fix

import android.Manifest.permission
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import me.shetj.base.ktx.hasPermission

/**
 * 权限修复
 * 1. 读写权限：
 *  1. >= 30 是需要去权限设置页面获取完整的读写权限
 *  2. 大于11如果只是获取媒体相关的权限，
 */
object FixPermission {

    /**
     * 读取媒体权限权限,兼容Android 33
     */
    fun checkReadMediaFile(context: FragmentActivity, isRequest: Boolean = true): Boolean {
        val hasPermission = if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            context.hasPermission(
                permission.READ_MEDIA_VIDEO,
                permission.READ_MEDIA_IMAGES,
                permission.READ_MEDIA_AUDIO,
                isRequest = isRequest
            )
        } else if (VERSION.SDK_INT < VERSION_CODES.R) {
            context.hasPermission(permission.READ_EXTERNAL_STORAGE, isRequest = isRequest)
        } else {
            val hasStorageManager = Environment.isExternalStorageManager()
            if (isRequest && !hasStorageManager) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(String.format("package:%s", context.packageName))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    context.startActivity(intent)
                }
            }
            hasStorageManager
        }
        return hasPermission
    }

    /**
     * 获取外部文件读写权限
     */
    fun requestExternalFile(context: Activity) {
        if (VERSION.SDK_INT < VERSION_CODES.R) {
            context.hasPermission(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE, isRequest = true)
        } else {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(String.format("package:%s", context.packageName))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    context.startActivity(intent)
                }
            }
        }
    }
}

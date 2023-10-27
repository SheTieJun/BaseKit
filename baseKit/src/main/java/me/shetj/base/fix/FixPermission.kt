package me.shetj.base.fix

import android.Manifest.permission
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.provider.Settings
import me.shetj.base.ktx.hasPermission


/**
 * 权限修复
 */
object FixPermission {

    /**
     * 读取文件权限,兼容Android 33
     */
    fun checkReadMediaFile(context: Activity, isRequest: Boolean = true): Boolean {
        val hasPermission = if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            context.hasPermission(
                permission.READ_MEDIA_VIDEO,
                permission.READ_MEDIA_IMAGES,
                permission.READ_MEDIA_AUDIO,
                isRequest = isRequest
            )
        } else {
            checkHasExternalFile(context, isRequest = isRequest)
        }
        return hasPermission
    }

    /**
     *  读取外部公告存储的权限,为了兼容Android 11
     *  Android 11 自动获取外部项目的权限
     */
    fun checkHasExternalFile(context: Activity, needWrite: Boolean = false, isRequest: Boolean = true): Boolean {
        return if (VERSION.SDK_INT < VERSION_CODES.R || Environment.isExternalStorageManager()) {
            if (needWrite) {
                context.hasPermission(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE, isRequest = isRequest)
            } else {
                context.hasPermission(permission.READ_EXTERNAL_STORAGE, isRequest = isRequest)
            }
        } else {
            if (isRequest) {
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
            false
        }
    }

}
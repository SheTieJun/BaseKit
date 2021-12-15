/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package me.shetj.base.tools.app

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.Keep
import androidx.core.content.FileProvider
import me.shetj.base.tools.file.FileUtils
import java.io.File

@Keep
class IntentUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * 获取安装App（支持8.0）的意图
         *
         * 8.0需添加权限 `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
         *
         * @param filePath  文件路径
         * @param authority 7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
         * <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
         * @return intent
         */
        @JvmStatic
        fun getInstallAppIntent(filePath: String, authority: String): Intent? {
            return getInstallAppIntent(FileUtils.getFileByPath(filePath), authority)
        }

        /**
         * 获取安装App(支持8.0)的意图
         *
         * 8.0需添加权限 `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
         *
         * @param file      文件
         * @param authority 7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
         * <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
         * @return intent
         */
        @JvmStatic
        fun getInstallAppIntent(file: File?, authority: String): Intent? {
            if (file == null) {
                return null
            }
            val intent = Intent(Intent.ACTION_VIEW)
            val data: Uri
            val type = "application/vnd.android.package-archive"
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                data = Uri.fromFile(file)
            } else {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                data = FileProvider.getUriForFile(Utils.app, authority, file)
            }
            intent.setDataAndType(data, type)
            return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        /**
         * 获取卸载App的意图
         *
         * @param packageName 包名
         * @return intent
         */
        @JvmStatic
        fun getUninstallAppIntent(packageName: String): Intent {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        /**
         * 获取打开App的意图
         *
         * @param packageName 包名
         * @return intent
         */
        @JvmStatic
        fun getLaunchAppIntent(packageName: String): Intent? {
            return Utils.app.packageManager.getLaunchIntentForPackage(packageName)
        }

        /**
         * 获取App具体设置的意图
         *
         * @param packageName 包名
         * @return intent
         */
        fun getAppDetailsSettingsIntent(packageName: String): Intent {
            val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
            intent.data = Uri.parse("package:$packageName")
            return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        /**
         * 获取分享文本的意图
         *
         * @param content 分享文本
         * @return intent
         */
        @JvmStatic
        fun getShareTextIntent(content: String): Intent {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, content)
            return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        /**
         * 获取分享图片的意图
         *
         * @param content   文本
         * @param imagePath 图片文件路径
         * @return intent
         */
        @JvmStatic
        fun getShareImageIntent(content: String, imagePath: String): Intent? {
            return getShareImageIntent(content, FileUtils.getFileByPath(imagePath))
        }

        /**
         * 获取分享图片的意图
         *
         * @param content 文本
         * @param image   图片文件
         * @return intent
         */
        @JvmStatic
        fun getShareImageIntent(content: String, image: File?): Intent? {
            return if (!FileUtils.isFileExists(image)) {
                null
            } else getShareImageIntent(content, Uri.fromFile(image))
        }

        /**
         * 获取分享图片的意图
         *
         * @param content 分享文本
         * @param uri     图片uri
         * @return intent
         */
        @JvmStatic
        fun getShareImageIntent(content: String, uri: Uri): Intent {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, content)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "image/*"
            return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        /**
         * 获取其他应用组件的意图
         *
         * @param packageName 包名
         * @param className   全类名
         * @param bundle      bundle
         * @return intent
         */
        @JvmOverloads
        fun getComponentIntent(packageName: String, className: String, bundle: Bundle? = null): Intent {
            val intent = Intent(Intent.ACTION_VIEW)
            if (bundle != null) {
                intent.putExtras(bundle)
            }
            val cn = ComponentName(packageName, className)
            intent.component = cn
            return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        /**
         * 获取关机的意图
         *
         * 需添加权限 `<uses-permission android:name="android.permission.SHUTDOWN"/>`
         *
         * @return intent
         */
        @JvmStatic
        val shutdownIntent: Intent
            get() {
                val intent = Intent(Intent.ACTION_SHUTDOWN)
                return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

        /**
         * 获取跳至拨号界面意图
         *
         * @param phoneNumber 电话号码
         */
        @JvmStatic
        fun getDialIntent(phoneNumber: String): Intent {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        /**
         * 获取拨打电话意图
         *
         * 需添加权限 `<uses-permission android:name="android.permission.CALL_PHONE"/>`
         *
         * @param phoneNumber 电话号码
         */
        @JvmStatic
        fun getCallIntent(phoneNumber: String): Intent {
            val intent = Intent("android.intent.action.CALL", Uri.parse("tel:$phoneNumber"))
            return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        /**
         * 获取跳至发送短信界面的意图
         *
         * @param phoneNumber 接收号码
         * @param content     短信内容
         */
        @JvmStatic
        fun getSendSmsIntent(phoneNumber: String, content: String): Intent {
            val uri = Uri.parse("smsto:$phoneNumber")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body", content)
            return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }


        /**
         * 获取拍照的意图
         *
         * @param outUri 输出的uri
         * @return 拍照的意图
         */
        @JvmStatic
        fun getCaptureIntent(outUri: Uri): Intent {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
            return intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

}

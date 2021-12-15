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

import android.os.Environment

import java.io.File

class CleanUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * 清除内部缓存
         *
         * /data/data/com.xxx.xxx/cache
         *
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        @JvmStatic
        fun cleanInternalCache(): Boolean {
            return deleteFilesInDir(Utils.app.cacheDir)
        }

        /**
         * 清除内部文件
         *
         * /data/data/com.xxx.xxx/files
         *
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        @JvmStatic
        fun cleanInternalFiles(): Boolean {
            return deleteFilesInDir(Utils.app.filesDir)
        }

        /**
         * 清除内部数据库
         *
         * /data/data/com.xxx.xxx/databases
         *
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        @JvmStatic
        fun cleanInternalDbs(): Boolean {
            Utils.app.filesDir.parent ?: return true
            return deleteFilesInDir(Utils.app.filesDir.parent!! + File.separator + "databases")
        }

        /**
         * 根据名称清除数据库
         *
         * /data/data/com.xxx.xxx/databases/dbName
         *
         * @param dbName 数据库名称
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        @JvmStatic
        fun cleanInternalDbByName(dbName: String): Boolean {
            return Utils.app.deleteDatabase(dbName)
        }

        /**
         * 清除内部SP
         *
         * /data/data/com.xxx.xxx/shared_prefs
         *
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        @JvmStatic
        fun cleanInternalSP(): Boolean {
            if (Utils.app.filesDir.parent == null) return true
            return deleteFilesInDir(Utils.app.filesDir.parent!! + File.separator + "shared_prefs")
        }

        /**
         * 清除外部缓存
         *
         * /storage/emulated/0/android/data/com.xxx.xxx/cache
         *
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        @JvmStatic
        fun cleanExternalCache(): Boolean {
            return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && deleteFilesInDir(Utils.app.externalCacheDir)
        }

        /**
         * 清除自定义目录下的文件
         *
         * @param dirPath 目录路径
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        @JvmStatic
        fun cleanCustomCache(dirPath: String): Boolean {
            return deleteFilesInDir(dirPath)
        }

        /**
         * 清除自定义目录下的文件
         *
         * @param dir 目录
         * @return `true`: 清除成功<br></br>`false`: 清除失败
         */
        @JvmStatic
        fun cleanCustomCache(dir: File): Boolean {
            return deleteFilesInDir(dir)
        }

        @JvmStatic
        fun deleteFilesInDir(dirPath: String): Boolean {
            return deleteFilesInDir(getFileByPath(dirPath))
        }

        private fun deleteFilesInDir(dir: File?): Boolean {
            if (dir == null) return false
            // 目录不存在返回true
            if (!dir.exists()) return true
            // 不是目录返回false
            if (!dir.isDirectory) return false
            // 现在文件存在且是文件夹
            val files = dir.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (file in files) {
                    if (file.isFile) {
                        if (!file.delete()) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
            return true
        }

        private fun deleteDir(dir: File?): Boolean {
            if (dir == null) return false
            // 目录不存在返回true
            if (!dir.exists()) return true
            // 不是目录返回false
            if (!dir.isDirectory) return false
            // 现在文件存在且是文件夹
            val files = dir.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (file in files) {
                    if (file.isFile) {
                        if (!file.delete()) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
            return dir.delete()
        }

        private fun getFileByPath(filePath: String): File? {
            return if (isSpace(filePath)) null else File(filePath)
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) return true
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }
    }
}
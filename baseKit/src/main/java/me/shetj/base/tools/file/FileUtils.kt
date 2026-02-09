package me.shetj.base.tools.file

import java.io.BufferedInputStream
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

object FileUtils {
    private val LINE_SEP = System.getProperty("line.separator")

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    fun getFileByPath(filePath: String?): File? {
        return if (isSpace(filePath)) null else File(filePath!!)
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return `true`: 存在<br></br>
     * `false`: 不存在
     */
    fun isFileExists(filePath: String?): Boolean {
        return isFileExists(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return `true`: 存在<br></br>
     * `false`: 不存在
     */
    fun isFileExists(file: File?): Boolean {
        return file != null && file.exists()
    }

    /**
     * 重命名文件
     *
     * @param filePath 文件路径
     * @param newName  新名称
     * @return `true`: 重命名成功<br></br>
     * `false`: 重命名失败
     */
    fun rename(filePath: String?, newName: String?): Boolean {
        return rename(getFileByPath(filePath), newName)
    }

    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名称
     * @return `true`: 重命名成功<br></br>
     * `false`: 重命名失败
     */
    fun rename(file: File?, newName: String?): Boolean {
        // 文件为空返回false
        if (file == null) return false
        // 文件不存在返回false
        if (!file.exists()) return false
        // 新的文件名为空返回false
        if (isSpace(newName)) return false
        // 如果文件名没有改变返回true
        if (newName == file.name) return true
        val newFile = File(file.parent + File.separator + newName)
        // 如果重命名的文件已存在返回false
        return !newFile.exists() && file.renameTo(newFile)
    }

    /**
     * 判断是否是目录
     *
     * @param dirPath 目录路径
     * @return `true`: 是<br></br>
     * `false`: 否
     */
    fun isDir(dirPath: String?): Boolean {
        return isDir(getFileByPath(dirPath))
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     * @return `true`: 是<br></br>
     * `false`: 否
     */
    fun isDir(file: File?): Boolean {
        return file != null && file.exists() && file.isDirectory
    }

    /**
     * 判断是否是文件
     *
     * @param filePath 文件路径
     * @return `true`: 是<br></br>
     * `false`: 否
     */
    fun isFile(filePath: String?): Boolean {
        return isFile(getFileByPath(filePath))
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return `true`: 是<br></br>
     * `false`: 否
     */
    fun isFile(file: File?): Boolean {
        return file != null && file.exists() && file.isFile
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 目录路径
     * @return `true`: 存在或创建成功<br></br>
     * `false`: 不存在或创建失败
     */
    fun createOrExistsDir(dirPath: String?): Boolean {
        return createOrExistsDir(getFileByPath(dirPath))
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return `true`: 存在或创建成功<br></br>
     * `false`: 不存在或创建失败
     */
    fun createOrExistsDir(file: File?): Boolean {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param filePath 文件路径
     * @return `true`: 存在或创建成功<br></br>
     * `false`: 不存在或创建失败
     */
    fun createOrExistsFile(filePath: String?): Boolean {
        return createOrExistsFile(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return `true`: 存在或创建成功<br></br>
     * `false`: 不存在或创建失败
     */
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile
        if (!createOrExistsDir(file.parentFile)) return false
        return try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param filePath 文件路径
     * @return `true`: 创建成功<br></br>
     * `false`: 创建失败
     */
    fun createFileByDeleteOldFile(filePath: String?): Boolean {
        return createFileByDeleteOldFile(getFileByPath(filePath))
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file 文件
     * @return `true`: 创建成功<br></br>
     * `false`: 创建失败
     */
    fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        // 文件存在并且删除失败返回false
        if (file.exists() && !file.delete()) return false
        // 创建目录失败返回false
        if (!createOrExistsDir(file.parentFile)) return false
        return try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 复制或移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @param isMove      是否移动
     * @return `true`: 复制或移动成功<br></br>
     * `false`: 复制或移动失败
     */
    private fun copyOrMoveDir(
        srcDirPath: String?,
        destDirPath: String?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        return copyOrMoveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener, isMove)
    }

    /**
     * 复制或移动目录
     *
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @param isMove   是否移动
     * @return `true`: 复制或移动成功<br></br>
     * `false`: 复制或移动失败
     */
    private fun copyOrMoveDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        if (srcDir == null || destDir == null) return false
        // 如果目标目录在源目录中则返回false，看不懂的话好好想想递归怎么结束
        // srcPath : F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res
        // destPath: F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res1
        // 为防止以上这种情况出现出现误判，须分别在后面加个路径分隔符
        val srcPath = srcDir.path + File.separator
        val destPath = destDir.path + File.separator
        if (destPath.contains(srcPath)) return false
        // 源文件不存在或者不是目录则返回false
        if (!srcDir.exists() || !srcDir.isDirectory) return false
        if (destDir.exists()) {
            if (listener == null || listener.onReplace()) { // 需要覆盖则删除旧目录
                if (!deleteAllInDir(destDir)) { // 删除文件失败的话返回false
                    return false
                }
            } else { // 不需要覆盖直接返回即可true
                return true
            }
        }
        // 目标目录不存在返回false
        if (!createOrExistsDir(destDir)) return false
        val files = srcDir.listFiles()
        if (files != null) {
            for (file in files) {
                val oneDestFile = File(destPath + file.name)
                if (file.isFile) {
                    // 如果操作失败返回false
                    if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) return false
                } else if (file.isDirectory) {
                    // 如果操作失败返回false
                    if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) return false
                }
            }
        }
        return !isMove || deleteDir(srcDir)
    }

    /**
     * 复制或移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @param isMove       是否移动
     * @return `true`: 复制或移动成功<br></br>
     * `false`: 复制或移动失败
     */
    private fun copyOrMoveFile(
        srcFilePath: String?,
        destFilePath: String?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        return copyOrMoveFile(
            getFileByPath(srcFilePath),
            getFileByPath(destFilePath),
            listener,
            isMove
        )
    }

    /**
     * 复制或移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @param isMove   是否移动
     * @return `true`: 复制或移动成功<br></br>
     * `false`: 复制或移动失败
     */
    private fun copyOrMoveFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        if (srcFile == null || destFile == null) return false
        // 如果源文件和目标文件相同则返回false
        if (srcFile == destFile) return false
        // 源文件不存在或者不是文件则返回false
        if (!srcFile.exists() || !srcFile.isFile) return false
        if (destFile.exists()) { // 目标文件存在
            if (listener == null || listener.onReplace()) { // 需要覆盖则删除旧文件
                if (!destFile.delete()) { // 删除文件失败的话返回false
                    return false
                }
            } else { // 不需要覆盖直接返回即可true
                return true
            }
        }
        // 目标目录不存在返回false
        if (!createOrExistsDir(destFile.parentFile)) return false
        return try {
            (FileIOUtils.writeFileFromIS(destFile, FileInputStream(srcFile), false)
                    && !(isMove && !deleteFile(srcFile)))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 复制目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @return `true`: 复制成功<br></br>
     * `false`: 复制失败
     */
    fun copyDir(
        srcDirPath: String?,
        destDirPath: String?,
        listener: OnReplaceListener?
    ): Boolean {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener)
    }

    /**
     * 复制目录
     *
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @return `true`: 复制成功<br></br>
     * `false`: 复制失败
     */
    fun copyDir(srcDir: File?, destDir: File?, listener: OnReplaceListener?): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, false)
    }

    /**
     * 复制文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @return `true`: 复制成功<br></br>
     * `false`: 复制失败
     */
    fun copyFile(
        srcFilePath: String?,
        destFilePath: String?,
        listener: OnReplaceListener?
    ): Boolean {
        return copyFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener)
    }

    /**
     * 复制文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @return `true`: 复制成功<br></br>
     * `false`: 复制失败
     */
    fun copyFile(srcFile: File?, destFile: File?, listener: OnReplaceListener?): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, false)
    }

    /**
     * 移动目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @param listener    是否覆盖监听器
     * @return `true`: 移动成功<br></br>
     * `false`: 移动失败
     */
    fun moveDir(
        srcDirPath: String?,
        destDirPath: String?,
        listener: OnReplaceListener?
    ): Boolean {
        return moveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), listener)
    }

    /**
     * 移动目录
     *
     * @param srcDir   源目录
     * @param destDir  目标目录
     * @param listener 是否覆盖监听器
     * @return `true`: 移动成功<br></br>
     * `false`: 移动失败
     */
    fun moveDir(srcDir: File?, destDir: File?, listener: OnReplaceListener?): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, true)
    }

    /**
     * 移动文件
     *
     * @param srcFilePath  源文件路径
     * @param destFilePath 目标文件路径
     * @param listener     是否覆盖监听器
     * @return `true`: 移动成功<br></br>
     * `false`: 移动失败
     */
    fun moveFile(
        srcFilePath: String?,
        destFilePath: String?,
        listener: OnReplaceListener?
    ): Boolean {
        return moveFile(getFileByPath(srcFilePath), getFileByPath(destFilePath), listener)
    }

    /**
     * 移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param listener 是否覆盖监听器
     * @return `true`: 移动成功<br></br>
     * `false`: 移动失败
     */
    fun moveFile(srcFile: File?, destFile: File?, listener: OnReplaceListener?): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, true)
    }

    /**
     * 删除目录
     *
     * @param dirPath 目录路径
     * @return `true`: 删除成功<br></br>
     * `false`: 删除失败
     */
    fun deleteDir(dirPath: String?): Boolean {
        return deleteDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return `true`: 删除成功<br></br>
     * `false`: 删除失败
     */
    fun deleteDir(dir: File?): Boolean {
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

    /**
     * 删除文件
     *
     * @param srcFilePath 文件路径
     * @return `true`: 删除成功<br></br>
     * `false`: 删除失败
     */
    fun deleteFile(srcFilePath: String?): Boolean {
        return deleteFile(getFileByPath(srcFilePath))
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return `true`: 删除成功<br></br>
     * `false`: 删除失败
     */
    fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    /**
     * 删除目录下所有东西
     *
     * @param dirPath 目录路径
     * @return `true`: 删除成功<br></br>
     * `false`: 删除失败
     */
    fun deleteAllInDir(dirPath: String?): Boolean {
        return deleteAllInDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录下所有东西
     *
     * @param dir 目录
     * @return `true`: 删除成功<br></br>
     * `false`: 删除失败
     */
    fun deleteAllInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir) { true }
    }

    /**
     * 删除目录下所有文件
     *
     * @param dirPath 目录路径
     * @return `true`: 删除成功<br></br>
     * `false`: 删除失败
     */
    fun deleteFilesInDir(dirPath: String?): Boolean {
        return deleteFilesInDir(getFileByPath(dirPath))
    }

    /**
     * 删除目录下所有文件
     *
     * @param dir 目录
     * @return `true`: 删除成功<br></br>
     * `false`: 删除失败
     */
    fun deleteFilesInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir) { pathname -> pathname.isFile }
    }

    /**
     * 删除目录下所有过滤的文件
     *
     * @param dirPath 目录路径
     * @param filter  过滤器
     * @return `true`: 删除成功<br></br>
     * `false`: 删除失败
     */
    fun deleteFilesInDirWithFilter(dirPath: String?, filter: FileFilter): Boolean {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * 删除目录下所有过滤的文件
     *
     * @param dir    目录
     * @param filter 过滤器
     * @return `true`: 删除成功<br></br>
     * `false`: 删除失败
     */
    fun deleteFilesInDirWithFilter(dir: File?, filter: FileFilter): Boolean {
        if (dir == null) return false
        // 目录不存在返回true
        if (!dir.exists()) return true
        // 不是目录返回false
        if (!dir.isDirectory) return false
        // 现在文件存在且是文件夹
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (filter.accept(file)) {
                    if (file.isFile) {
                        if (!file.delete()) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
        }
        return true
    }

    /**
     * 获取目录下所有文件
     *
     * 不递归进子目录
     *
     * @param dirPath 目录路径
     * @return 文件链表
     */
    fun listFilesInDir(dirPath: String?): List<File>? {
        return listFilesInDir(dirPath, false)
    }

    /**
     * 获取目录下所有文件
     *
     * 不递归进子目录
     *
     * @param dir 目录
     * @return 文件链表
     */
    fun listFilesInDir(dir: File?): List<File>? {
        return listFilesInDir(dir, false)
    }

    /**
     * 获取目录下所有文件
     *
     * @param dirPath     目录路径
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDir(dirPath: String?, isRecursive: Boolean): List<File>? {
        return listFilesInDir(getFileByPath(dirPath), isRecursive)
    }

    /**
     * 获取目录下所有文件
     *
     * @param dir         目录
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDir(dir: File?, isRecursive: Boolean): List<File>? {
        return listFilesInDirWithFilter(dir, { true }, isRecursive)
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * 不递归进子目录
     *
     * @param dirPath 目录路径
     * @param filter  过滤器
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dirPath: String?, filter: FileFilter): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, false)
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * 不递归进子目录
     *
     * @param dir    目录
     * @param filter 过滤器
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(dir: File?, filter: FileFilter): List<File>? {
        return listFilesInDirWithFilter(dir, filter, false)
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * @param dirPath     目录路径
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter,
        isRecursive: Boolean
    ): List<File>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive)
    }

    /**
     * 获取目录下所有过滤的文件
     *
     * @param dir         目录
     * @param filter      过滤器
     * @param isRecursive 是否递归进子目录
     * @return 文件链表
     */
    fun listFilesInDirWithFilter(
        dir: File?,
        filter: FileFilter,
        isRecursive: Boolean
    ): List<File>? {
        if (!isDir(dir)) return null
        val list: MutableList<File> = ArrayList()
        val files = dir!!.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (filter.accept(file)) {
                    list.add(file)
                }
                if (isRecursive && file.isDirectory) {
                    val subFiles = listFilesInDirWithFilter(file, filter, true)
                    if (subFiles != null) {
                        list.addAll(subFiles)
                    }
                }
            }
        }
        return list
    }

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param filePath 文件路径
     * @return 文件最后修改的毫秒时间戳
     */
    fun getFileLastModified(filePath: String?): Long {
        return getFileLastModified(getFileByPath(filePath))
    }

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param file 文件
     * @return 文件最后修改的毫秒时间戳
     */
    fun getFileLastModified(file: File?): Long {
        return file?.lastModified() ?: -1
    }

    /**
     * 简单获取文件编码格式
     *
     * @param filePath 文件路径
     * @return 文件编码
     */
    fun getFileCharsetSimple(filePath: String?): String {
        return getFileCharsetSimple(getFileByPath(filePath))
    }

    /**
     * 简单获取文件编码格式
     *
     * @param file 文件
     * @return 文件编码
     */
    fun getFileCharsetSimple(file: File?): String {
        var p = 0
        try {
            BufferedInputStream(FileInputStream(file)).use { `is` ->
                p = (`is`.read() shl 8) + `is`.read()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return when (p) {
            0xefbb -> "UTF-8"
            0xfffe -> "Unicode"
            0xfeff -> "UTF-16BE"
            else -> "GBK"
        }
    }

    /**
     * 获取文件行数
     *
     * @param filePath 文件路径
     * @return 文件行数
     */
    fun getFileLines(filePath: String?): Int {
        return getFileLines(getFileByPath(filePath))
    }

    /**
     * 获取文件行数
     *
     * 比readLine要快很多
     *
     * @param file 文件
     * @return 文件行数
     */
    fun getFileLines(file: File?): Int {
        var count = 1
        try {
            BufferedInputStream(FileInputStream(file)).use { `is` ->
                val buffer = ByteArray(1024)
                var readChars: Int
                if (LINE_SEP!!.endsWith("\n")) {
                    while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                        for (i in 0 until readChars) {
                            if (buffer[i] == '\n'.code.toByte()) ++count
                        }
                    }
                } else {
                    while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                        for (i in 0 until readChars) {
                            if (buffer[i] == '\r'.code.toByte()) ++count
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return count
    }

    /**
     * 获取目录大小
     *
     * @param dirPath 目录路径
     * @return 文件大小
     */
    fun getDirSize(dirPath: String?): String {
        return getDirSize(getFileByPath(dirPath))
    }

    /**
     * 获取目录大小
     *
     * @param dir 目录
     * @return 文件大小
     */
    fun getDirSize(dir: File?): String {
        val len = getDirLength(dir)
        return if (len == -1L) "" else byte2FitMemorySize(len)
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    fun getFileSize(filePath: String?): String {
        return getFileSize(getFileByPath(filePath))
    }

    /**
     * 获取文件大小
     *
     * @param file 文件
     * @return 文件大小
     */
    fun getFileSize(file: File?): String {
        val len = getFileLength(file)
        return if (len == -1L) "" else byte2FitMemorySize(len)
    }

    /**
     * 获取目录长度
     *
     * @param dirPath 目录路径
     * @return 目录长度
     */
    fun getDirLength(dirPath: String?): Long {
        return getDirLength(getFileByPath(dirPath))
    }

    /**
     * 获取目录长度
     *
     * @param dir 目录
     * @return 目录长度
     */
    fun getDirLength(dir: File?): Long {
        if (!isDir(dir)) return -1
        var len: Long = 0
        val files = dir!!.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isDirectory) {
                    len += getDirLength(file)
                } else {
                    len += file.length()
                }
            }
        }
        return len
    }

    /**
     * 获取文件长度
     *
     * @param filePath 文件路径
     * @return 文件长度
     */
    fun getFileLength(filePath: String?): Long {
        return getFileLength(getFileByPath(filePath))
    }

    /**
     * 获取文件长度
     *
     * @param file 文件
     * @return 文件长度
     */
    fun getFileLength(file: File?): Long {
        return if (!isFile(file)) -1 else file!!.length()
    }

    /**
     * 获取文件的MD5校验码
     *
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    fun getFileMD5ToString(filePath: String?): String? {
        val file = if (isSpace(filePath)) null else File(filePath!!)
        return getFileMD5ToString(file)
    }

    /**
     * 获取文件的MD5校验码
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    fun getFileMD5ToString(file: File?): String? {
        return bytes2HexString(getFileMD5(file))
    }

    /**
     * 获取文件的MD5校验码
     *
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    fun getFileMD5(filePath: String?): ByteArray? {
        return getFileMD5(getFileByPath(filePath))
    }

    /**
     * 获取文件的MD5校验码
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    fun getFileMD5(file: File?): ByteArray? {
        if (file == null) return null
        var dis: DigestInputStream? = null
        try {
            val fis = FileInputStream(file)
            var md = MessageDigest.getInstance("MD5")
            dis = DigestInputStream(fis, md)
            val buffer = ByteArray(1024 * 256)
            while (true) {
                if (dis.read(buffer) <= 0) break
            }
            md = dis.messageDigest
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIO(dis)
        }
        return null
    }

    /**
     * 获取全路径中的最长目录
     *
     * @param file 文件
     * @return filePath最长目录
     */
    fun getDirName(file: File?): String? {
        return if (file == null) null else getDirName(file.path)
    }

    /**
     * 获取全路径中的最长目录
     *
     * @param filePath 文件路径
     * @return filePath最长目录
     */
    fun getDirName(filePath: String?): String? {
        if (isSpace(filePath)) return filePath
        val lastSep = filePath!!.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
    }

    /**
     * 获取全路径中的文件名
     *
     * @param file 文件
     * @return 文件名
     */
    fun getFileName(file: File?): String? {
        return if (file == null) null else getFileName(file.path)
    }

    /**
     * 获取全路径中的文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    fun getFileName(filePath: String?): String? {
        if (isSpace(filePath)) return filePath
        val lastSep = filePath!!.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }

    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param file 文件
     * @return 不带拓展名的文件名
     */
    fun getFileNameNoExtension(file: File?): String? {
        return if (file == null) null else getFileNameNoExtension(file.path)
    }

    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param filePath 文件路径
     * @return 不带拓展名的文件名
     */
    fun getFileNameNoExtension(filePath: String?): String? {
        if (isSpace(filePath)) return filePath
        val lastPoi = filePath!!.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        if (lastSep == -1) {
            return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
        }
        return if (lastPoi == -1 || lastSep > lastPoi) {
            filePath.substring(lastSep + 1)
        } else filePath.substring(lastSep + 1, lastPoi)
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param file 文件
     * @return 文件拓展名
     */
    fun getFileExtension(file: File?): String? {
        return if (file == null) null else getFileExtension(file.path)
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param filePath 文件路径
     * @return 文件拓展名
     */
    fun getFileExtension(filePath: String?): String? {
        if (isSpace(filePath)) return filePath
        val lastPoi = filePath!!.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }

    ///////////////////////////////////////////////////////////////////////////
    // copy from ConvertUtils
    ///////////////////////////////////////////////////////////////////////////
    private val hexDigits = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    )

    /**
     * byteArr转hexString
     *
     * 例如： bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes 字节数组
     * @return 16进制大写字符串
     */
    private fun bytes2HexString(bytes: ByteArray?): String? {
        if (bytes == null) return null
        val len = bytes.size
        if (len <= 0) return null
        val ret = CharArray(len shl 1)
        var i = 0
        var j = 0
        while (i < len) {
            ret[j++] = hexDigits[bytes[i].toInt() ushr 4 and 0x0f]
            ret[j++] = hexDigits[bytes[i].toInt() and 0x0f]
            i++
        }
        return String(ret)
    }

    /**
     * 字节数转合适内存大小
     *
     * 保留3位小数
     *
     * @param byteNum 字节数
     * @return 合适内存大小
     */
    private fun byte2FitMemorySize(byteNum: Long): String {
        return if (byteNum < 0) {
            "shouldn't be less than zero!"
        } else if (byteNum < 1024) {
            String.format(Locale.getDefault(), "%.3fB", byteNum.toDouble())
        } else if (byteNum < 1048576) {
            String.format(Locale.getDefault(), "%.3fKB", byteNum.toDouble() / 1024)
        } else if (byteNum < 1073741824) {
            String.format(Locale.getDefault(), "%.3fMB", byteNum.toDouble() / 1048576)
        } else {
            String.format(Locale.getDefault(), "%.3fGB", byteNum.toDouble() / 1073741824)
        }
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        for (i in 0 until s.length) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
        }
        return true
    }

    interface OnReplaceListener {
        fun onReplace(): Boolean
    }
}

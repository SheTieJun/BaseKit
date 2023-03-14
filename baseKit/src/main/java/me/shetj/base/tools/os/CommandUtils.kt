package me.shetj.base.tools.os

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 命令行工具
 */
object CommandUtils {

    fun exec(command: String?): String {
        val resultBuilder = StringBuilder()
        var pro: Process? = null
        var input: BufferedReader? = null
        val runTime = Runtime.getRuntime()
            ?: throw NullPointerException("reinforce task failed,Runtime is null")
        try {
            pro = runTime.exec(command)
            input = BufferedReader(InputStreamReader(pro.inputStream))
            var line: String?
            while (input.readLine().also { line = it } != null) {
                resultBuilder.append(line).append("\n")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        } finally {
            pro?.destroy()
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return resultBuilder.toString()
    }
}

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

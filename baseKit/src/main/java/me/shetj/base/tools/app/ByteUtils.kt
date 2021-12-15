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

import java.nio.ByteOrder
import kotlin.experimental.or

class ByteUtils {
    /**
     * 大端小端 问题
     */
    private fun thisCPU(): Boolean {
        return ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN
    }

    fun getBytes(s: Long, bBigEnding: Boolean): ByteArray {
        var s1 = s
        val buf = ByteArray(8)
        if (bBigEnding)
            for (i in buf.indices.reversed()) {
                buf[i] = (s1 and 0x00000000000000ff).toByte()
                s1 = s1 shr 8
            }
        else
            for (i in buf.indices) {
                buf[i] = (s1 and 0x00000000000000ff).toByte()
                s1 = s1 shr 8
            }
        return buf
    }

    fun getBytes(s: Short): ByteArray {
        return getBytes(s.toLong(), this.thisCPU())
    }

    fun getShort(buf: ByteArray): Short {
        return getShort(buf, this.thisCPU())
    }

    fun getShort(buf: ByteArray?, bBigEnding: Boolean): Short {
        requireNotNull(buf) { "byte array is null!" }
        require(buf.size <= 2) { "byte array size > 2 !" }
        var r: Short = 0
        if (bBigEnding) {
            for (aBuf in buf) {
                r = r.toInt().shl(8).toShort()
                r = r or (aBuf.toInt() and 0x00ff).toShort()
            }
        } else {
            for (i in buf.indices.reversed()) {
                r = r.toInt().shl(8).toShort()
                r = r or (buf[i].toInt() and 0x00ff).toShort()
            }
        }

        return r
    }
}

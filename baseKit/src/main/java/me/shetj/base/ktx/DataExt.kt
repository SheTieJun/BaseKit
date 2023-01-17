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
package me.shetj.base.ktx

import android.os.Message
import androidx.annotation.IntRange
import androidx.annotation.Px
import kotlin.random.Random
import me.shetj.base.BaseKit
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.json.EmptyUtils
import me.shetj.datastore.dataStoreKit
import timber.log.Timber

@JvmOverloads
fun Any?.toMessage(code: Int = 1): Message {
    return Message.obtain().apply {
        what = code
        obj = this@toMessage
    }
}

/**
 * 执行代码，并获取执行需要的时间
 */
inline fun runTimeMillis(crossinline run: () -> Unit): Long {
    val time = System.currentTimeMillis()
    run.invoke()
    return System.currentTimeMillis() - time
}

/**
 * 把1~9 之间加0
 */
fun Int.unitFormat(): String {
    return if (this in 0..9) "0$this" else "" + this
}

/**
 * 16进制 255 以内
 */
fun intToHStr(@IntRange(from = 0, to = 255) x: Int): String {
    return if (x > 15) {
        Integer.toHexString(x)
    } else {
        "0" + Integer.toHexString(x)
    }
}

fun convertHexColorString(color: Int): String {
    return String.format("#%06X", 0xFFFFFF and color)
}

val Float.dp2px
    @Px
    get() = ArmsUtils.dp2px(this)

val Float.px2dp
    get() = ArmsUtils.px2dp(this)

fun Any?.isEmpty() = EmptyUtils.isEmpty(this)

fun Throwable?.log() {
    Timber.i(this)
}

//region 获取随机数
fun getRandomString(num: Int): String {
    var linkNo = ""
    // 用字符数组的方式随机
    val model = "0aAbBc1CdDeE2fFgGh3HiIjJ4kKlLm5MnNoO6pPqQr7RsStT8uUvVw9WxXyY0zZ"
    val m = model.toCharArray()
    var j = 0
    while (j < num) {
        val c = m[Random.nextInt(62)]
        // 随机数之间没有重复的
        if (linkNo.contains(c.toString())) {
            continue
        }
        linkNo += c
        j++
    }
    return linkNo
}
//endregion

fun <E> ArrayList<E>.addIfNotNull(element: E?) {
    element?.let { this.add(it) }
}

/**
 * 获取一个带名字的 Thread
 */
fun getTagThread(tag: String, run: Runnable): Thread {
    val t = Thread(run)
    t.name = tag
    return t
}


val defDataStore by lazy { BaseKit.app.dataStoreKit() }

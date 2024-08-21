package me.shetj.base.ktx

import android.os.Message
import androidx.annotation.IntRange
import me.shetj.base.BaseKit
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.file.StringUtils
import me.shetj.base.tools.json.EmptyUtils
import me.shetj.datastore.dataStoreKit
import timber.log.Timber
import java.util.Locale
import kotlin.random.Random

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
 * int 转中文的个十百千万
 * @return
 */
fun Int?.toNumberCH(): String {
    val inputInt = this ?: 0
    return StringUtils.numberToCH(inputInt)
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
    get() = ArmsUtils.dp2px(this)

val Float.px2dp
    get() = ArmsUtils.px2dp(this)

val Int.dp2px
    get() = this.toFloat().dp2px

val Int.px2dp
    get() = this.toFloat().px2dp

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

/**
 * DataStore 非常适合存储键值对，例如用户设置，具体示例可能包括时间格式、通知偏好设置，以及是显示还是隐藏用户已阅读的新闻报道。
 * DataStore 还可以使用协议缓冲区来存储类型化对象。
 */
val defDataStore by lazy { BaseKit.app.dataStoreKit() }

/**
 * float 转成rmb
 */
fun Float.formatRMB() = String.format(Locale.getDefault(), "￥%.2f", this).replace(".00", "")

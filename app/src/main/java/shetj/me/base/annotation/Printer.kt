package shetj.me.base.annotation

import android.util.Log
import me.shetj.base.BaseKit
import me.shetj.base.ktx.logD
import me.shetj.base.ktx.logE
import me.shetj.base.ktx.logI
import me.shetj.base.ktx.logV
import me.shetj.base.ktx.logW
import me.shetj.base.ktx.logWtf
import java.util.*

object Printer {
    private const val TOP_LEFT_CORNER = '┌'
    private const val BOTTOM_LEFT_CORNER = '└'
    private const val HORIZONTAL_LINE = '├'
    private const val DOUBLE_DIVIDER = "─────────────────────────────────────────────────────────────"
    private const val TOP_BORDER = TOP_LEFT_CORNER.toString() + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private const val BOTTOM_BORDER = BOTTOM_LEFT_CORNER.toString() + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private const val METHOD_NAME_FORMAT = "%s method: %s"
    private const val ARGUMENT_FORMAT = "%s params: "
    private const val RESULT_FORMAT = "%s result: "
    private const val COST_TIME_FORMAT = "%s time: %dms"
    private const val RUN_THREAD_FORMAT = "%s thread: %s"
    private const val FIELD_NAME_FORMAT = "%s fields: %s"
    private const val STACK_FORMAT = "%s stack %s"
    private const val STACK_FORMAT_2 = "%s %s"

    /**
     * 此处添加同步，防止多线程调用是log打印错误
     */
    @JvmStatic
    @Synchronized
    fun printMethodInfo(
        methodInfo: MethodInfo,
        level: Int,
        enableTime: Boolean,
        tagName: String,
        fieldInfoNS: Vector<FieldInfoN>,
        watchStack: Boolean
    ) {
        buildString {
            append(TOP_BORDER + "\n")
            append(String.format(METHOD_NAME_FORMAT, HORIZONTAL_LINE, methodInfo.getShowMethodName()) + "\n")
            if (methodInfo.argumentList.isNotEmpty()) {
                append(String.format(ARGUMENT_FORMAT, HORIZONTAL_LINE) + methodInfo.argumentList + "\n")
            }
            if (methodInfo.result != null && methodInfo.result.toString() != "COROUTINE_SUSPENDED") {
                append(String.format(RESULT_FORMAT, HORIZONTAL_LINE) + methodInfo.result + "\n")
            }
            if (enableTime) {
                append(String.format(Locale.CHINA, COST_TIME_FORMAT, HORIZONTAL_LINE, methodInfo.cost) + "\n")
            }
            val fields = StringBuilder()
            for (infoN in fieldInfoNS) {
                fields.append(infoN.toString())
            }
            if (!fieldInfoNS.isEmpty()) {
                append(String.format(Locale.CHINA, FIELD_NAME_FORMAT, HORIZONTAL_LINE, fields.toString()) + "\n")
            }
            append(String.format(Locale.CHINA, RUN_THREAD_FORMAT, HORIZONTAL_LINE, Thread.currentThread().name) + "\n")
            if (watchStack) {
                append(String.format(Locale.CHINA, STACK_FORMAT, HORIZONTAL_LINE, "watchStack:") + "\n")
                var isStart = false
                Log.getStackTraceString(Throwable()).reader().readLines().forEach {
                    if (it.contains(methodInfo.methodName)) {
                        isStart = true
                    }
                    if (isStart) {
                        append(String.format(Locale.CHINA, STACK_FORMAT_2, HORIZONTAL_LINE, it) + "\n")
                    }
                }
            }
            append(BOTTOM_BORDER)
        }.let {
            val tag = tagName.ifEmpty { methodInfo.className } ?: BaseKit.TAG
            when (level) {
                Log.DEBUG -> {
                    it.logD(tag)
                }
                Log.INFO -> {
                    it.logI(tag)
                }
                Log.WARN -> {
                    it.logW(tag)
                }
                Log.ERROR -> {
                    it.logE(tag)
                }
                Log.VERBOSE -> {
                    it.logV(tag)
                }
                Log.ASSERT -> {
                    it.logWtf(tag)
                }
            }
        }
    }
}

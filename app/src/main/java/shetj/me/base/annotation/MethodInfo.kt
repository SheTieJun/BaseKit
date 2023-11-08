package shetj.me.base.annotation

import java.util.*

class MethodInfo internal constructor() {
    private var mClassName: String? = null // 类名
    private var mMethodName: String? = null // 方法名
    private var mMethodDesc: String? = null // 方法描述符
    /**
     * @return 返回方法执行结果
     */
    /**
     * @param result 设置方法执行结果
     */
    var result: Any? = null // 方法执行结果
    /**
     * @return 返回方法执行耗时
     */
    /**
     * @param cost 设置方法执行耗时
     */
    var cost: Long = 0 // 方法执行耗时
    private val mArgumentList // 方法参数列表
        : MutableList<AgNode>

    init {
        mArgumentList = ArrayList()
    }

    override fun toString(): String {
        return String.format(Locale.CHINA, OUTPUT_FORMAT, methodName, cost) + result
    }

    var className: String?
        /**
         * @return 返回类名
         */
        get() {
            mClassName = mClassName?.replace("/", ".")
            return mClassName
        }

        /**
         * @param className 设置类名
         */
        set(className) {
            mClassName = className
        }
    var methodName: String
        /**
         * @return 返回方法名
         */
        get() = "$className.$mMethodName"

        /**
         * @param methodName 设置方法名
         */
        set(methodName) {
            mMethodName = methodName
        }

    fun getShowMethodName(): String {
        return "$className : $mMethodName"
    }

    /**
     * @param methodDesc 设置方法描述符
     */
    fun setMethodDesc(methodDesc: String?) {
        mMethodDesc = methodDesc
    }

    /**
     * 添加方法参数
     *
     * @param argument 方法参数
     */
    fun addArgument(argument: AgNode) {
        mArgumentList.add(argument)
    }

    val argumentList: List<AgNode>
        /**
         * @return 得到方法参数列表
         */
        get() = mArgumentList

    /**
     * 参数的node节点，包含参数名，和参数值
     */
    class AgNode(private var type: String?, private var value: Any?) {
        override fun toString(): String {
            return "{" +
                "type='" + type + '\'' +
                ", value=" + value +
                '}'
        }
    }

    companion object {
        private const val OUTPUT_FORMAT = "The method's name is %s ,the cost is %dms and the result is "
    }
}

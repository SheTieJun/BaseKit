package shetj.me.base.contentprovider

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import me.shetj.base.ktx.logI
import shetj.me.base.R.id
import shetj.me.base.R.layout
import shetj.me.base.contentprovider.WidgetProvider.Companion.idsSet
import shetj.me.base.contentprovider.WidgetProvider.Companion.mIndex
import shetj.me.base.func.main.MainActivity

/**
 * 展示。每隔 N 秒/分钟，刷新一次数据；
 * 交互。点击操作 App 的数据；
 * 打开App。打开主页或指定页面。
 *
 *
 * 1. 先声明 Widget 的一些属性。 在 res 新建 xml 文件夹，创建 appwidget-provider 标签的 xml 文件。
 * 2. 创建桌面要显示的布局。 在 layout 创建 app_widget.xml。
 * 3. 然后来管理 Widget 状态。 实现一个继承 AppWidgetProvider 的类。
 * 4. 最后在 AndroidManifest.xml 里，将 AppWidgetProvider类 和 xml属性 注册到一块。
 * 5. 通常我们会加一个 Service 来控制 Widget 的更新时间，后面再讲为什么。
 *
 */
const val ACTION_UPDATE_ALL = "com.shetj.widget.UPDATE_ALL"


class WidgetProvider : AppWidgetProvider() {



    // 更新 widget 的广播对应的action


    /**
     * 接收窗口小部件点击时发送的广播
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        action.logI("WidgetProvider")
        if (ACTION_UPDATE_ALL == action) {
            // “更新”广播
            updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet)
        } else if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
            // “按钮点击”广播
            mIndex = 0
            updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet)
        }
    }

    // 更新所有的 widget
    private fun updateAllAppWidgets(context: Context, appWidgetManager: AppWidgetManager, set: Set<*>) {
        "updateAllAppWidgets".logI("WidgetProvider")
        // widget 的id
        var appID: Int
        // 迭代器，用于遍历所有保存的widget的id
        val it = set.iterator()

        // 要显示的那个数字，每更新一次 + 1
        mIndex++ // TODO:可以在这里做更多的逻辑操作，比如：数据处理、网络请求等。然后去显示数据
        while (it.hasNext()) {
            appID = it.next() as Int

            // 获取 example_appwidget.xml 对应的RemoteViews
            val remoteView = RemoteViews(context.packageName, layout.app_widget)

            // 设置显示数字
            remoteView.setTextViewText(id.widget_txt, mIndex.toString())

            // 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
            remoteView.setOnClickPendingIntent(id.widget_btn_reset, getResetPendingIntent(context))
            remoteView.setOnClickPendingIntent(id.widget_btn_open, getOpenPendingIntent(context))

            // 更新 widget
            appWidgetManager.updateAppWidget(appID, remoteView)
        }
    }

    /**
     * 获取 重置数字的广播
     */
    private fun getResetPendingIntent(context: Context): PendingIntent {
        "getResetPendingIntent".logI("WidgetProvider")
        val intent = Intent()
        intent.setClass(context, WidgetProvider::class.java)
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    /**
     * 获取 打开 MainActivity 的 PendingIntent
     */
    private fun getOpenPendingIntent(context: Context): PendingIntent {
        "getOpenPendingIntent".logI("WidgetProvider")
        val intent = Intent()
        intent.setClass(context, MainActivity::class.java)
        intent.putExtra("main", "这句话是我从桌面点开传过去的。")
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法，可添加多次但只第一次调用
     */
    override fun onEnabled(context: Context) {
        "onEnabled".logI()
        // 在第一个 widget 被创建时，开启服务
        val intent = Intent(context, WidgetService::class.java)
        context.startService(intent)
        Toast.makeText(context, "开始计数", Toast.LENGTH_SHORT).show()
        super.onEnabled(context)
    }

    // 当 widget 被初次添加 或者 当 widget 的大小被改变时，被调用
    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        "onAppWidgetOptionsChanged".logI("WidgetProvider")
    }

    /**
     * 当小部件从备份恢复时调用该方法
     */
    override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        "onRestored".logI("WidgetProvider")
    }

    /**
     * 每次窗口小部件被点击更新都调用一次该方法
     */
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        "onUpdate".logI("WidgetProvider")
        // 每次 widget 被创建时，对应的将widget的id添加到set中
        for (appWidgetId in appWidgetIds) {
            idsSet.add(appWidgetId)
        }
    }

    /**
     * 每删除一次窗口小部件就调用一次
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        "onDeleted".logI("WidgetProvider")
        // 当 widget 被删除时，对应的删除set中保存的widget的id
        for (appWidgetId in appWidgetIds) {
            idsSet.remove(Integer.valueOf(appWidgetId))
        }
        super.onDeleted(context, appWidgetIds)
    }

    /**
     * 当最后一个该窗口小部件删除时调用该方法，注意是最后一个
     */
    override fun onDisabled(context: Context) {
        "onDisabled".logI("WidgetProvider")
        // 在最后一个 widget 被删除时，终止服务
        val intent = Intent(context, WidgetService::class.java)
        context.stopService(intent)
        super.onDisabled(context)
    }

    companion object {
        // 保存 widget 的id的HashSet，每新建一个 widget 都会为该 widget 分配一个 id。
        private val idsSet: MutableSet<Int> = HashSet<Int>()
        var mIndex = 0

        fun registerReceiver(context: FragmentActivity) {
            val filter = IntentFilter()
            filter.addAction(ACTION_UPDATE_ALL)
            val provider = WidgetProvider()
            context.registerReceiver(provider, filter)
            context.lifecycle.addObserver(object :LifecycleEventObserver{
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        context.unregisterReceiver(provider)
                    }
                }
            })
        }



    }
}
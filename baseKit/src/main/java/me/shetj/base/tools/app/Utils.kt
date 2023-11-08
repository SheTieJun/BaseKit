package me.shetj.base.tools.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import java.util.LinkedList

class Utils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        private lateinit var sApplication: Application

        private var sTopActivityWeakRef: WeakReference<Activity>? = null
        val sActivityList: MutableList<Activity> = LinkedList()

        private val mCallbacks = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                sActivityList.add(activity)
                setTopActivityWeakRef(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                setTopActivityWeakRef(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                setTopActivityWeakRef(activity)
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                sActivityList.remove(activity)
            }
        }

        /**
         * 初始化工具类
         *
         * @param app 应用
         */
        @JvmStatic
        fun init(app: Application) {
            sApplication = app
            app.registerActivityLifecycleCallbacks(mCallbacks)
        }

        /**
         * 获取Application
         *
         * @return Application
         */
        @JvmStatic
        val app: Application
            get() {
                return sApplication
            }

        @JvmStatic
        fun getTopActivity() = sTopActivityWeakRef?.get()

        private fun setTopActivityWeakRef(activity: Activity) {
            if (sTopActivityWeakRef == null || activity != sTopActivityWeakRef!!.get()) {
                sTopActivityWeakRef = WeakReference(activity)
            }
        }
    }
}

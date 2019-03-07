package me.shetj.base.tools.app

import android.annotation.SuppressLint
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

        @SuppressLint("StaticFieldLeak")
        private var sApplication: Application? = null

        var sTopActivityWeakRef: WeakReference<Activity>? = null
        var sActivityList: MutableList<Activity> = LinkedList()

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

            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) {

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
        fun init(app: Application) {
            Utils.sApplication = app
            app.registerActivityLifecycleCallbacks(mCallbacks)
        }

        /**
         * 获取Application
         *
         * @return Application
         */
        val app: Application
            get() {
                if (sApplication != null) {
                    return sApplication!!
                }
                throw NullPointerException("u should init first")
            }

        private fun setTopActivityWeakRef(activity: Activity) {
            if (sTopActivityWeakRef == null || activity != sTopActivityWeakRef!!.get()) {
                sTopActivityWeakRef = WeakReference(activity)
            }
        }
    }
}
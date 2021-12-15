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

        private var sTopActivityWeakRef: WeakReference<Activity>? = null
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
                if (sApplication != null) {
                    return sApplication!!
                }
                throw NullPointerException("u should init first")
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

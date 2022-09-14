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


package shetj.me.base.func.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat.Type
import androidx.lifecycle.lifecycleScope
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState
import me.shetj.base.base.TaskExecutor
import me.shetj.base.ktx.hasNavigationBars
import me.shetj.base.ktx.hideNavigationBars
import me.shetj.base.ktx.launch
import me.shetj.base.ktx.logI
import me.shetj.base.ktx.openSetting
import me.shetj.base.ktx.openUri
import me.shetj.base.ktx.saverCreate
import me.shetj.base.ktx.saverDB
import me.shetj.base.ktx.selectFile
import me.shetj.base.ktx.sendEmailText
import me.shetj.base.ktx.setAppearance
import me.shetj.base.ktx.showNavigationBars
import me.shetj.base.ktx.startRequestPermissions
import me.shetj.base.ktx.toJson
import me.shetj.base.ktx.windowInsets
import me.shetj.base.ktx.withIO
import me.shetj.base.ktx.withMain
import me.shetj.base.model.NetWorkLiveDate
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.network_coroutine.observeChange
import me.shetj.base.tip.TipKit
import me.shetj.base.tools.app.KeyboardUtil
import me.shetj.base.tools.data.defDataStoreKit
import me.shetj.base.tools.file.FileQUtils
import me.shetj.base.tools.time.CodeUtil
import shetj.me.base.R
import shetj.me.base.annotation.Debug
import shetj.me.base.common.other.CommentPopup
import shetj.me.base.common.worker.DownloadWorker
import shetj.me.base.databinding.ActivityMainBinding
import shetj.me.base.databinding.ContentMainBinding
import shetj.me.base.test_lib.onYearMonthDay
import timber.log.Timber


class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var mContent: ContentMainBinding
    private var codeUtil: CodeUtil? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        KeyboardUtil.init(this)
        mContent = mViewBinding.content
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    @SuppressLint("MissingPermission")
    override fun initView() {
        //test
        packageManager.getInstalledPackages(
            PackageManager.GET_ACTIVITIES or
                    PackageManager.GET_SERVICES
        )


        kotlin.runCatching {
            val wifiManager: WifiManager? = getSystemService()
            val info = wifiManager?.connectionInfo
            val wifiMac = info?.bssid
            val phoneMac = info?.macAddress
        }


        if (!mViewModel.isAddJankStats) {
            "createAndTrack".logI()
            JankStats.createAndTrack(window) {
                if (it.isJank) {
                    ((it.frameDurationUiNanos / 1000000).toString() + "毫秒").logI("JankStats")
                }
//                it.toJson().logI("JankStats")
            }
            mViewModel.isAddJankStats = true
        }
        val hierarchy = PerformanceMetricsState.getHolderForHierarchy(mViewBinding.content.root)

        setAppearance(isBlack = true, Color.TRANSPARENT)
        findViewById<View>(R.id.test_download).setOnClickListener {
            DownloadWorker.startDownload(
                this@MainActivity,
                "https://dldir1.qq.com/wework/work_weixin/wxwork_android_3.0.31.13637_100001.apk",
                this@MainActivity.cacheDir.path,
                "wxwork_android_3.apk"
            )
        }

        dataStoreKit()


        findViewById<View>(R.id.btn_test_tip).setOnClickListener {
            TipKit.normal(this, "这是一个toast")
            TipKit.info(this, "这是一个toast")
            TipKit.warn(this, "这是一个toast")
            TipKit.success(this, "这是一个toast")
            TipKit.error(this, "这是一个toast")
        }


        findViewById<View>(R.id.btn_email).setOnClickListener {
            sendEmailText(addresses = "375105540@qq.com", title = "Base测试", content = "这是一个测试代码")
        }

        findViewById<View>(R.id.btn_select_image).setOnClickListener {
            selectFile {
                "url = ${it.toString()}".logI()
                ("url = ${it?.let { it1 -> FileQUtils.getFileByUri(this, it1) }}").logI()
            }
        }

        var i = 0
        findViewById<View>(R.id.btn_save).setOnClickListener {
            launch {
                defDataStoreKit.save("Test", i++)
            }
        }

        mContent.btnSetting.setOnClickListener {
            openSetting()
//            startActivityResult(intent = Intent(this,TestActivity::class.java)){
//                it.resultCode.toString().logI()
//            }
        }

        mContent.btnGoRouter.setOnClickListener {
            openUri(mContent.router.text.toString())
        }

        mContent.tvTestCode.setOnClickListener { codeUtil!!.start() }

        findViewById<View>(R.id.fab).setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(mViewModel.getNightModel())
        }

        //btn_test_keybord
        mContent.btnTestKeybord.setOnClickListener {
            hierarchy.state?.putState("CommentPopup", "show")
            CommentPopup.newInstance().show(supportFragmentManager)
        }

        mViewBinding.content.btnTestPicker.setOnClickListener {
            onYearMonthDay()
        }

        mContent.btnInsert.setOnClickListener {
            saverCreate(key = "测试key", value = "测试value").apply {
                launch {
                    saverDB.insert(this@apply)
                }
            }
        }

        launch {
            "111".withIO { i ->
                i.toInt()
            }.withMain {
                it + 1
            }.withMain {
                it.toString().logI("测试协程")
            }.let {

            }
        }
        mContent.btnCustomTab.post {
            windowInsets?.getInsets(Type.navigationBars()).toJson().logI("navigationBars")
            windowInsets?.getInsets(Type.statusBars()).toJson().logI("statusBars")
            windowInsets?.getInsets(Type.captionBar()).toJson().logI("captionBar")
            windowInsets?.isVisible(Type.navigationBars()).toJson().logI("navigationBars")
            windowInsets?.isVisible(Type.statusBars()).toJson().logI("statusBars")
            windowInsets?.isVisible(Type.captionBar()).toJson().logI("captionBar")
        }

        mContent.btnCustomTab.setOnClickListener {
            if (hasNavigationBars()) {
                hideNavigationBars()
            } else {
                showNavigationBars()
            }
        }

        mContent.btnFind.setOnClickListener {
            launch {
                saverDB.getAll(groupN = "base", isDel = false)
                    .collect {
                        it.toJson().logI()
                    }
            }
        }

        mContent.testEvent.setOnClickListener {
            startRequestPermissions(
                permissions = arrayOf(
                    Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR
                )
            ) {
                if (it.filter { !it.value }.isEmpty()) {
                    mViewModel.addEvent(this)
                }
            }
        }
        NetWorkLiveDate.getInstance().start(this)

        NetWorkLiveDate.getInstance().observe(this) {
            when (it?.netType) {
                NetWorkLiveDate.NetType.NONE -> ("hasNet = ${it.hasNet},netType = NONE").logI()
                NetWorkLiveDate.NetType.PHONE -> ("hasNet = ${it.hasNet},netType = PHONE").logI()
                NetWorkLiveDate.NetType.WIFI -> ("hasNet = ${it.hasNet},netType = WIFI").logI()
                else -> {}
            }
        }

        mViewBinding.content.testThread.setOnClickListener {
            TaskExecutor.executeOnIO {
                Timber.tag("TaskExecutor").i(Thread.currentThread().name)
            }
        }
        mViewBinding.content.testLoading.setOnClickListener {
            TipKit.loading(this) {
                netTest()
            }
        }

        mViewModel.liveDate.observeChange(this) {
            onSuccess = {}
            onFailure = {
                Timber.tag("getMusic").e(this)
            }
        }
        mViewBinding.content.btnDoNotDisturb.setOnClickListener {

        }
    }

    @Debug
    private fun dataStoreKit() {
        launch {
            defDataStoreKit.get("Test", -1)
                .collect {
                    it.toString().logI("DataStoreKit")
                }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
    }

    @Debug
    suspend fun netTest() {
        mViewModel.getMusicV2()
    }


    public override fun initData() {
        codeUtil = CodeUtil(mViewBinding.content.tvTestCode)
        codeUtil?.register(this.lifecycle)
    }
}
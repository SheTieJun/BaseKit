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
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import kotlinx.coroutines.delay
import me.shetj.base.base.TaskExecutor
import me.shetj.base.ktx.hideNavigationBars
import me.shetj.base.ktx.launch
import me.shetj.base.ktx.logI
import me.shetj.base.ktx.openSetting
import me.shetj.base.ktx.saverCreate
import me.shetj.base.ktx.saverDB
import me.shetj.base.ktx.sendEmailText
import me.shetj.base.ktx.setAppearance
import me.shetj.base.ktx.toJson
import me.shetj.base.ktx.windowInsetsCompat
import me.shetj.base.model.NetWorkLiveDate
import me.shetj.base.mvp.BaseBindingActivity
import me.shetj.base.network_coroutine.observeChange
import me.shetj.base.tip.TipKit
import me.shetj.base.tip.TipPopupWindow
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.app.ArmsUtils.Companion.paste
import me.shetj.base.tools.app.KeyboardUtil
import me.shetj.base.tools.image.ImageCallBack
import me.shetj.base.tools.image.ImageUtils
import me.shetj.base.tools.time.CodeUtil
import shetj.me.base.R
import shetj.me.base.common.other.CommentPopup
import shetj.me.base.common.worker.DownloadWorker
import shetj.me.base.databinding.ActivityMainBinding
import shetj.me.base.databinding.ContentMainBinding
import shetj.me.base.test_lib.defSet
import shetj.me.base.test_lib.onYearMonthDay
import timber.log.Timber

class MainActivity : BaseBindingActivity<MainPresenter, ActivityMainBinding>() {
    private lateinit var mContent: ContentMainBinding
    private var codeUtil: CodeUtil? = null
    private var isKeep = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KeyboardUtil.init(this)
        mContent = mViewBinding.content
    }

    public override fun initView() {
         setAppearance(isBlack =  true)

        findViewById<View>(R.id.test_download).setOnClickListener {
            DownloadWorker.startDownload(
                this@MainActivity,
                "https://dldir1.qq.com/wework/work_weixin/wxwork_android_3.0.31.13637_100001.apk",
                this@MainActivity.cacheDir.path,
                "wxwork_android_3.apk"
            )
        }

        findViewById<View>(R.id.btn_test_tip).setOnClickListener {
            TipPopupWindow.showTip(this, tipMsg = "测试一下INFO")
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
            ImageUtils.selectLocalImage(this)
        }

        mContent.btnSetting.setOnClickListener {
            openSetting()
        }

        mContent.tvTestCode.setOnClickListener { codeUtil!!.start() }

        findViewById<View>(R.id.fab).setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(mPresenter.getNightModel())
        }

        //btn_test_keybord
        mContent.btnTestKeybord.setOnClickListener {
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


        mContent.btnCustomTab.setOnClickListener {
            windowInsetsCompat?.getInsets(Type.navigationBars()).toJson().logI("navigationBars")
            windowInsetsCompat?.getInsets(Type.statusBars()).toJson().logI("statusBars")
            windowInsetsCompat?.getInsets(Type.captionBar()).toJson().logI("captionBar")

            windowInsetsCompat?.isVisible(Type.navigationBars()).toJson().logI("navigationBars")
            windowInsetsCompat?.isVisible(Type.statusBars()).toJson().logI("statusBars")
            windowInsetsCompat?.isVisible(Type.captionBar()).toJson().logI("captionBar")
            hideNavigationBars()
        }

        mContent.btnFind.setOnClickListener {
            launch {
                saverDB.getAll(groupN = "base", isDel = false)
                    .collect {
                        Timber.i(it.toJson())
                    }
            }
        }

        mContent.testEvent.setOnClickListener {

            if (requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_CALENDAR,
                        Manifest.permission.READ_CALENDAR
                    )
                )
            ) {
                mPresenter.addEvent(this)
            }
        }
        NetWorkLiveDate.getInstance().start(this)
        NetWorkLiveDate.getInstance().observe(this) {
            when (it?.netType) {
                NetWorkLiveDate.NetType.NONE -> Timber.tag("requestNetWork")
                    .i("hasNet = ${it.hasNet},netType = NONE")
                NetWorkLiveDate.NetType.PHONE -> Timber.tag("requestNetWork")
                    .i("hasNet = ${it.hasNet},netType = PHONE")
                NetWorkLiveDate.NetType.WIFI -> Timber.tag("requestNetWork")
                    .i("hasNet = ${it.hasNet},netType = WIFI")
                else -> {}
            }
        }

        mViewBinding.content.testThread.setOnClickListener {
            TaskExecutor.exit()
            TaskExecutor.executeOnIO {
                Timber.tag("TaskExecutor").i(Thread.currentThread().name)
            }
        }
        mViewBinding.content.testLoading.setOnClickListener {
            TipKit.loading(this) {
                netTest()
            }
        }

        mPresenter.liveDate.observeChange(this) {
            onSuccess = {
                Timber.tag("getMusic").i(this.toJson())
            }
            onFailure = {
                Timber.tag("getMusic").e(this)
            }
        }

        mViewBinding.content.start.defSet()
        mViewBinding.content.end.defSet()
    }

    override fun isPermissionGranted(permissions: Map<String, Boolean>) {
        super.isPermissionGranted(permissions)
        val empty = permissions.filter { !it.value }.isEmpty()
        if (empty) {
            mPresenter.addEvent(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        launch {
            delay(500)
            paste(this@MainActivity).logI()
        }
    }

    suspend fun netTest() {
        mPresenter.getMusicV2()
    }

    public override fun initData() {
        codeUtil = CodeUtil(mViewBinding.content.tvTestCode)
        codeUtil?.register(this.lifecycle)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImageUtils.onActivityResult(
            this,
            requestCode,
            resultCode,
            data,
            object : ImageCallBack {
                override fun onSuccess(key: Uri) {
                    Timber.i("url = $key")
                }

                override fun onFail() {

                }

                override fun isNeedCut(): Boolean {
                    return false
                }
            })
    }

}
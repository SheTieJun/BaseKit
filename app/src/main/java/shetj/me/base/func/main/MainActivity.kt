package shetj.me.base.func.main

import android.Manifest
import android.app.ActivityManager
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.health.SystemHealthManager
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState
import androidx.metrics.performance.PerformanceMetricsState.Holder
import com.google.android.material.sidesheet.SideSheetDialog
import java.util.*
import me.shetj.base.base.TaskExecutor
import me.shetj.base.ktx.defDataStore
import me.shetj.base.ktx.launch
import me.shetj.base.ktx.logI
import me.shetj.base.ktx.openSetting
import me.shetj.base.ktx.openUri
import me.shetj.base.ktx.selectFile
import me.shetj.base.ktx.sendEmailText
import me.shetj.base.ktx.setAppearance
import me.shetj.base.ktx.start
import me.shetj.base.ktx.startIgnoreBatteryOpt
import me.shetj.base.ktx.startRequestPermissions
import me.shetj.base.ktx.toJson
import me.shetj.base.ktx.windowInsets
import me.shetj.base.model.GrayThemeLiveData
import me.shetj.base.model.NetWorkLiveDate
import me.shetj.base.mvvm.viewbind.BaseBindingActivity
import me.shetj.base.network_coroutine.observeChange
import me.shetj.base.tip.TipKit
import me.shetj.base.tools.app.KeyboardUtil
import me.shetj.base.tools.app.LanguageKit
import me.shetj.base.tools.file.FileQUtils
import shetj.me.base.R
import shetj.me.base.annotation.Debug
import shetj.me.base.common.other.CommentPopup
import shetj.me.base.common.worker.DownloadWorker
import shetj.me.base.databinding.ActivityMainBinding
import shetj.me.base.databinding.ContentMainBinding
import shetj.me.base.func.md3.Main2Activity
import timber.log.Timber


class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var mContent: ContentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        KeyboardUtil.init(this)

        val healthStats = getSystemService(SystemHealthManager::class.java).takeMyUidSnapshot()
        healthStats.measurementKeyCount

        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            getSystemService(ActivityManager::class.java).getHistoricalProcessExitReasons(packageName, 0, 0)
                .takeIf { it.isNotEmpty() }?.get(0)?.let {
                    //上一次应用结束的原因说明
                    it.toString().logI("APP")
                }
        }
    }

    override fun setUpClicks() {
        mContent = mBinding.content
        val hierarchy = addJankStats()
        findViewById<View>(R.id.test_download).setOnClickListener {
            DownloadWorker.startDownload(
                this@MainActivity,
                "https://",
                this@MainActivity.cacheDir.path,
                "wxwork_android_3.apk"
            )
        }
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
                ("url = ${it?.let { it1 -> FileQUtils.getFileAbsolutePath(this, it1) }}").logI()
            }
        }

        mContent.btnDoc.setOnClickListener {
            selectFile("*/*") {}
        }

        mContent.btnSetting.setOnClickListener {
            openSetting()
        }

        mContent.startAc2.setOnClickListener {
            start<Main2Activity>()
        }

        mContent.btnGoRouter.setOnClickListener {
            openUri(mContent.router.text.toString())
        }


        findViewById<View>(R.id.fab).setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(mViewModel.getNightModel())
        }

        mContent.btnTestKeybord.setOnClickListener {
            hierarchy.state?.putState("CommentPopup", "show")
            CommentPopup.newInstance().show(supportFragmentManager)
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

        mBinding.content.testThread.setOnClickListener {
            TaskExecutor.executeOnIO {
                Timber.tag("TaskExecutor").i(Thread.currentThread().name)
            }
        }
        mBinding.content.testLoading.setOnClickListener {
            TipKit.loading(this) {
                netTest()
            }
        }

        mBinding.content.btnGrayModel.setOnClickListener {
            mViewModel.isGrayTheme = !mViewModel.isGrayTheme
            GrayThemeLiveData.getInstance().postValue(mViewModel.isGrayTheme)
        }


        mBinding.content.changeLanguage.setOnClickListener {
            if (!isEn) {
                LanguageKit.changeLanguage(this, Locale.ENGLISH)
            } else {
                LanguageKit.changeLanguage(this, Locale.CHINA)
            }

        }
        mContent.startPower.setOnClickListener {
            startIgnoreBatteryOpt()
        }
        mContent.showSideDialog.setOnClickListener {
            val sideSheetDialog = SideSheetDialog(this)
            sideSheetDialog.setContentView(R.layout.fragment_first)
            sideSheetDialog.setOnShowListener {
                sideSheetDialog.window?.let {
                    WindowCompat.setDecorFitsSystemWindows(it, false)
                }
            }
            sideSheetDialog.show()
        }
    }

    override fun onInitialized() {
        super.onInitialized()
        NetWorkLiveDate.getInstance().start(this)
    }

    override fun addObservers() {
        super.addObservers()
        NetWorkLiveDate.getInstance().observe(this) {
            when (it?.netType) {
                NetWorkLiveDate.NetType.NONE -> ("hasNet = ${it.hasNet},netType = NONE").logI()
                NetWorkLiveDate.NetType.PHONE -> ("hasNet = ${it.hasNet},netType = PHONE").logI()
                NetWorkLiveDate.NetType.WIFI -> ("hasNet = ${it.hasNet},netType = WIFI").logI()
                else -> {}
            }
        }
        mViewModel.liveDate.observeChange(this) {
            onSuccess = {
                this?.toJson().toString().logI()
            }
            onFailure = {
                Timber.tag("getMusic").e(this)
            }
        }
    }


    override fun initView() {
        setAppearance(isBlack = true, Color.TRANSPARENT)
        runOnUiThread {
            windowInsets?.getInsets(Type.navigationBars()).toJson().logI("navigationBars")
            windowInsets?.getInsets(Type.statusBars()).toJson().logI("statusBars")
            windowInsets?.getInsets(Type.captionBar()).toJson().logI("captionBar")
            windowInsets?.isVisible(Type.navigationBars()).toJson().logI("navigationBars")
            windowInsets?.isVisible(Type.statusBars()).toJson().logI("statusBars")
            windowInsets?.isVisible(Type.captionBar()).toJson().logI("captionBar")
        }
        dataStoreKit()
    }

    private fun addJankStats(): Holder {
        if (!mViewModel.isAddJankStats) {
            JankStats.createAndTrack(window) {
                if (it.isJank) {
                    ((it.frameDurationUiNanos / 1000000).toString() + "毫秒").logI("JankStats")
                    it.toJson().logI("JankStats")
                }
            }
            mViewModel.isAddJankStats = true
        }
        val hierarchy = PerformanceMetricsState.getHolderForHierarchy(mBinding.content.root)
        return hierarchy
    }

    val isEn: Boolean
        get() {
            return LanguageKit.getAppLocale(this).let {
                it.country == Locale.ENGLISH.country && it.language == Locale.ENGLISH.language
            }
        }

    @Debug
    private fun dataStoreKit() {
        launch {
            defDataStore.get("Test", -1)
                .collect {
                    it.toString().logI("DataStoreKit")
                }
        }
    }

    override fun isEnableGrayTheme(): Boolean {
        return true
    }

    @Debug
    suspend fun netTest() {
        mViewModel.getMusicV2()
    }

}
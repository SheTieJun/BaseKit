package shetj.me.base.func.main

import android.Manifest
import android.app.ActivityManager
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.health.SystemHealthManager
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.lifecycle.asLiveData
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState
import androidx.metrics.performance.PerformanceMetricsState.Holder
import androidx.tracing.trace
import com.google.android.material.sidesheet.SideSheetDialog
import me.shetj.base.BaseKit
import me.shetj.base.fix.FixPermission
import me.shetj.base.ktx.defDataStore
import me.shetj.base.ktx.hasPermission
import me.shetj.base.ktx.launch
import me.shetj.base.ktx.logE
import me.shetj.base.ktx.logI
import me.shetj.base.ktx.openSetting
import me.shetj.base.ktx.selectFile
import me.shetj.base.ktx.setAppearance
import me.shetj.base.ktx.showToast
import me.shetj.base.ktx.start
import me.shetj.base.ktx.startIgnoreBatteryOpt
import me.shetj.base.ktx.startRequestPermissions
import me.shetj.base.ktx.startWithTransition
import me.shetj.base.ktx.toJson
import me.shetj.base.ktx.windowInsets
import me.shetj.base.model.GrayThemeLiveData
import me.shetj.base.model.NetWorkLiveDate
import me.shetj.base.mvvm.viewbind.BaseBindingActivity
import me.shetj.base.netcoroutine.observeChange
import me.shetj.base.tip.TipKit
import me.shetj.base.tools.app.KeyboardUtil
import me.shetj.base.tools.app.KoogAgentKit
import me.shetj.base.tools.app.KoogAgentKit.Provider
import shetj.me.base.func.koog.KoogActivity
import me.shetj.base.tools.app.LanguageKit
import me.shetj.base.tools.app.MDThemeKit
import me.shetj.base.tools.app.ScreenshotKit
import me.shetj.base.tools.app.WindowKit
import me.shetj.base.tools.app.WindowKit.posturesCollector
import me.shetj.base.tools.debug.DebugFunc
import me.shetj.base.tools.file.FileQUtils
import shetj.me.base.R
import shetj.me.base.common.other.CommentPopup
import shetj.me.base.contentprovider.WidgetProvider
import shetj.me.base.databinding.ActivityMainBinding
import shetj.me.base.databinding.ContentMainBinding
import shetj.me.base.func.browser.GeckoBrowserActivity
import shetj.me.base.func.compose.ComposeTestActivity
import shetj.me.base.func.md3.Main2Activity
import shetj.me.base.func.preference.SettingActivity
import shetj.me.base.utils.KeyStoreKit
import timber.log.Timber
import java.util.Locale
import java.util.function.Consumer


class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var mContent: ContentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enabledOnBack = true

        KeyboardUtil.init(this)
        val healthStats = getSystemService(SystemHealthManager::class.java).takeMyUidSnapshot()
        healthStats.measurementKeyCount

        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            getSystemService(ActivityManager::class.java).getHistoricalProcessExitReasons(packageName, 0, 0)
                .takeIf { it.isNotEmpty() }?.get(0)?.let {
                    // 上一次应用结束的原因说明
                    it.toString().logI("APP-Exit")
                }
        }
        
        // Koog AI Agent 测试示例
        testKoogAgent()
    }

    /**
     * 测试 Koog AI Agent
     * 注意：需要设置对应的 API Key 环境变量
     */
    private fun testKoogAgent() {
        // 示例：使用 Ollama 本地模型（不需要 API Key）
        // 需要先在本地运行 Ollama: ollama run llama3.2
        val result = KoogAgentKit.quickRun(
            provider = Provider.OLLAMA,
            prompt = "你好，请用一句话介绍自己"
        )
        result?.let {
            "Koog Agent 回复: $it".logI("KoogTest")
        }

        // 示例：使用 OpenAI（需要设置 OPENAI_API_KEY 环境变量）
        // val result = KoogAgentKit.quickRun(
        //     provider = Provider.OPENAI,
        //     prompt = "你好，请用一句话介绍自己"
        // )
    }

    private fun initScreenshot() {
        ScreenshotKit.initActivity(this)
        ScreenshotKit.setScreenshotListener(object : ScreenshotKit.ScreenshotListener {
            override fun onScreenShot(path: String?) {
                "截屏了:$path".showToast()
                "截屏了:$path".logI()
            }
        })

        hasPermission("android.permission.POST_NOTIFICATIONS")
        WidgetProvider.registerReceiver(this)
        BaseKit.androidID.logI("androidID")
        //只有没有android:configChanges="orientation|keyboardHidden|screenSize" 才会多次触发
        WindowKit.addWinLayoutListener(this, posturesCollector(onTable = {
            "onTable".logI("WinLayout")
        }, onBook = {
            "onBook".logI("WinLayout")
        }, onNormal = {
            "onNormal".logI("WinLayout")
        }))
        WindowKit.windowSizeStream(this).observe(this) {
            it.toJson().logI("windowSizeStream")
        }
        defDataStore.get<String>(":").asLiveData().observe(this) {

        }
    }

    override fun setUpClicks() {
        mContent = mBinding.content
        val hierarchy = addJankStats()
        hierarchy.state?.putState("Activity", javaClass.simpleName)
        mContent.btnTestGecko.setOnClickListener {
            start<GeckoBrowserActivity>()
        }
        mBinding.content.btnSelectImage.setOnClickListener {
            selectFile {
                "url = $it".logI()
                (
                        "url = ${
                            it?.let { it1 ->
                                FileQUtils.getFileAbsolutePath(this@MainActivity, it1)?.also { p ->
                                    launch {
                                        defDataStore.save("TestPath", p)
                                    }
                                }
                            }
                        }"
                        ).logI()
            }
        }

        mContent.KeyStore.setOnClickListener {
            val originalText = "Hello, World!"
            val encryptedText = KeyStoreKit.encryptWithKeyStore(originalText)
            if (encryptedText == null) {
                "加密失败".showToast()
                return@setOnClickListener
            }
            "加密后的数据：${encryptedText.first.toString(Charsets.UTF_8)}".logI("KeyStore")
            val decryptedText = KeyStoreKit.decryptWithKeyStore(encryptedText)
            "解密后的数据：$decryptedText".logI("KeyStore")
            ("originalText: $originalText,  ${originalText == decryptedText}").logI("KeyStore")
        }
        mContent.btnDoc.setOnClickListener {
            selectFile("*/*") {}
        }
        mContent.Preference.setOnClickListener {
            start<SettingActivity>()
        }

        mContent.btnSetting.setOnClickListener {
            openSetting()
        }

        mContent.startAc2.setOnClickListener {
            start<Main2Activity>()
        }

        mContent.btnChangeTheme.setOnClickListener {
            MDThemeKit.showChangeThemeDialog(this)
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

        mBinding.content.testLoading.setOnClickListener {
            TipKit.loading(this) {
                // do nothing for test
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
            trace("showSideDialog") {
                val sideSheetDialog = SideSheetDialog(this)
                if (VERSION.SDK_INT >= VERSION_CODES.S) {
                    sideSheetDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    sideSheetDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    val windowBlurEnabledListener: Consumer<Boolean> = object : Consumer<Boolean> {
                        override fun accept(t: Boolean) {
                            sideSheetDialog.window?.let { it1 -> updateWindowForBlurs(it1, t) }
                        }
                    }
                    sideSheetDialog.window?.decorView?.addOnAttachStateChangeListener(
                        object : OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(v: View) {
                                windowManager.addCrossWindowBlurEnabledListener(
                                    windowBlurEnabledListener
                                )
                            }

                            override fun onViewDetachedFromWindow(v: View) {
                                windowManager.removeCrossWindowBlurEnabledListener(
                                    windowBlurEnabledListener
                                )
                            }
                        })
                }
                sideSheetDialog.setContentView(R.layout.fragment_first)
                sideSheetDialog.setOnShowListener {
                    sideSheetDialog.window?.let {
                        WindowCompat.setDecorFitsSystemWindows(it, false)
                    }
                }
                sideSheetDialog.show()
            }
        }

        mContent.btnPerm.setOnClickListener {
            FixPermission.requestExternalFile(this)
        }
        mContent.Compose.setOnClickListener {
            startWithTransition<ComposeTestActivity>()
        }
        mContent.Debug.setOnClickListener {
            if (BaseKit.isDebug()) {
                BaseKit.isDebug.postValue(false)
            } else {
                BaseKit.isDebug.postValue(true)
            }
        }
        
        mContent.btnSpeechRecognition.setOnClickListener {
            start<shetj.me.base.func.speech.SpeechRecognitionEntryActivity>()
        }
        
        mContent.btnSelectTrackerTest.setOnClickListener {
            start<shetj.me.base.func.SelectTrackerTestActivity>()
        }

        mContent.btnDebugSettings.setOnClickListener {
            DebugFunc.getInstance().openDebugSettings(this)
        }
        
        mContent.btnKoogAi.setOnClickListener {
            start<KoogActivity>()
        }
    }


    /**
     * 弹窗背景高斯模糊
     */
    private fun updateWindowForBlurs(window: Window, blursEnabled: Boolean) {
        val mBackgroundBlurRadius = 80
        val mBlurBehindRadius = 20


        // We set a different dim amount depending on whether window blur is enabled or disabled
        val mDimAmountWithBlur = 0.1f
        val mDimAmountNoBlur = 0.4f
        window.setDimAmount(if (blursEnabled && mBlurBehindRadius > 0) mDimAmountWithBlur else mDimAmountNoBlur)

        if (buildIsAtLeastS()) {
            // Set the window background blur and blur behind radii
            window.setBackgroundBlurRadius(mBackgroundBlurRadius)
            window.attributes.blurBehindRadius = mBlurBehindRadius
            window.attributes = window.attributes
        }
    }

    private fun buildIsAtLeastS(): Boolean {
        return VERSION.SDK_INT >= VERSION_CODES.S
    }

    override fun onInitialized() {
        super.onInitialized()
        NetWorkLiveDate.getInstance().start(this)
    }

    override fun addObservers() {
        super.addObservers()
        NetWorkLiveDate.getInstance().observe(this) {
            when (it?.netType) {
                NetWorkLiveDate.NetType.UNKNOWN -> ("hasNet = ${it.hasNet},netType = NONE").logI()
                NetWorkLiveDate.NetType.PHONE -> ("hasNet = ${it.hasNet},netType = PHONE").logI()
                NetWorkLiveDate.NetType.WIFI -> ("hasNet = ${it.hasNet},netType = WIFI").logI()
                else -> {}
            }
        }
        mViewModel.liveDate.observeChange(this) {
            onSuccess = {
            }
            onFailure = {
                Timber.tag("getMusic").e(this)
            }
        }
    }

    override fun initBaseView() {
        super.initBaseView()
        setAppearance(isBlackText = true, Color.TRANSPARENT)
        mBinding.root.post {
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
                    ((it.frameDurationUiNanos / 1000000).toString() + "毫秒").logE("JankStats")
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

    //    @Debug
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
}

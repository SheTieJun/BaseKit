package shetj.me.base.func.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.delay
import me.shetj.base.base.TaskExecutor
import me.shetj.base.ktx.*
import me.shetj.base.model.NetWorkLiveDate
import me.shetj.base.mvp.BaseBindingActivity
import me.shetj.base.network.RxHttp
import me.shetj.base.network.callBack.SimpleNetCallBack
import me.shetj.base.tip.TipKit
import me.shetj.base.tip.TipPopupWindow
import me.shetj.base.tools.app.ArmsUtils.Companion.paste
import me.shetj.base.tools.file.EnvironmentStorage
import me.shetj.base.tools.image.ImageCallBack
import me.shetj.base.tools.image.ImageUtils
import me.shetj.base.tools.time.CodeUtil
import shetj.me.base.R
import shetj.me.base.api.BApi
import shetj.me.base.bean.MusicBean
import shetj.me.base.common.worker.DownloadWorker
import shetj.me.base.databinding.ActivityMainBinding
import shetj.me.base.databinding.ContentMainBinding
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

class MainActivity : BaseBindingActivity<MainPresenter, ActivityMainBinding>()  {
    private lateinit var mContent: ContentMainBinding
    private var codeUtil: CodeUtil? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContent = mViewBinding.content
    }

    public override fun initView() {

        findViewById<View>(R.id.test_download).setOnClickListener {
            DownloadWorker.startDownload(
                this,
                "https://dldir1.qq.com/wework/work_weixin/wxwork_android_3.0.31.13637_100001.apk",
                EnvironmentStorage.getExternalFilesDir(),
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

        mContent.btnInsert.setOnClickListener {
            saverCreate(key = "测试key", value = "测试value").apply {
                saverDB.insert(this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError {
                        Timber.e(it)
                    }
                    .subscribe {
                        Timber.i("测试koin")
                    }
            }
        }

        mContent.btnFind.setOnClickListener {
            saverDB.getAll(groupN = "base", isDel = false)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    Timber.i(it.toJson())
                }.subscribe()
        }

        mContent.testEvent.setOnClickListener {
            if (hasPermission(
                    Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR,
                    isRequest = true
                )
            ) {
                mPresenter.addEvent(this)
            }
        }
        NetWorkLiveDate.getInstance().start(this)
        NetWorkLiveDate.getInstance().observe(this, {
            when (it?.netType) {
                NetWorkLiveDate.NetType.NONE -> Timber.tag("requestNetWork")
                    .i("hasNet = ${it.hasNet},netType = NONE")
                NetWorkLiveDate.NetType.PHONE -> Timber.tag("requestNetWork")
                    .i("hasNet = ${it.hasNet},netType = PHONE")
                NetWorkLiveDate.NetType.WIFI -> Timber.tag("requestNetWork")
                    .i("hasNet = ${it.hasNet},netType = WIFI")
            }
        })

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

        mViewBinding.content.btnTestNet.setOnClickListener {
            mPresenter.getMusicByRxHttp(object :
                SimpleNetCallBack<List<MusicBean>>(this) {
                override fun onSuccess(data:List<MusicBean>) {
                    super.onSuccess(data)
                    Timber.tag("getMusicByRxHttp").i(data.toJson())
                }

                override fun onError(e: Exception) {
                    super.onError(e)
                    Timber.e(e)
                }
            })
        }

        //设置本地dns 解析
        RxHttp.getInstance().addDnsMap(HashMap<String, String>().apply {
            put("baidy1.com", "127.0.0.1")
        })
        RxHttp.getInstance().getApiManager(BApi::class.java, baseUrl = "http://baidy1.com").change("jwt1", HashMap()).subscribe()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        launch {
            delay(500)
            paste(this@MainActivity).logi()
        }
    }

    protected suspend fun netTest() {
        val music = mPresenter.getMusicV2()
        Timber.tag("getMusic").i(music.toJson())
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
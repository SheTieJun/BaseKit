package shetj.me.base.func.main

import android.animation.ValueAnimator
import android.animation.ValueAnimator.REVERSE
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.paging.Pager
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import me.shetj.base.base.TaskExecutor
import me.shetj.base.ktx.*
import me.shetj.base.model.NetWorkLiveDate
import me.shetj.base.mvp.BaseActivity
import me.shetj.base.mvp.IView
import me.shetj.base.network.callBack.SimpleNetCallBack
import me.shetj.base.saver.Saver
import me.shetj.base.saver.SaverDao
import me.shetj.base.sim.SimpleCallBack
import me.shetj.base.tools.app.ArmsUtils.Companion.paste
import me.shetj.base.tools.file.EnvironmentStorage
import me.shetj.base.tools.image.ImageUtils
import me.shetj.base.tools.time.CodeUtil
import me.shetj.base.view.TipPopupWindow
import org.koin.android.ext.android.get
import org.koin.androidx.scope.lifecycleScope
import org.koin.core.parameter.parametersOf
import shetj.me.base.R
import shetj.me.base.bean.ApiResult1
import shetj.me.base.bean.MusicBean
import shetj.me.base.common.worker.DownloadWorker
import shetj.me.base.di_hilttest.main1
import shetj.me.base.mvvmtest.MVVMTestActivity
import shetj.me.base.view.SimLoadingDialog
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : BaseActivity<MainPresenter>(), View.OnClickListener {
    private var mBtnTest: Button? = null
    private var mTvTestCode: TextView? = null
    private var codeUtil: CodeUtil? = null
    private var viewpage2: ViewPager2? = null

    @Inject
    lateinit var musicBean1: MusicBean

    @Inject
    lateinit var musicBean2: MusicBean

    @Inject
    lateinit var saverDao: SaverDao

    val view2: IView = lifecycleScope.get()

    @main1
    @Inject
    lateinit var view3: IView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // 框架默认会通过反射创建 MainPresenter ，
    override fun initPresenter(): MainPresenter {
        return lifecycleScope.get { parametersOf(this) }
    }

    @SuppressLint("SetTextI18n")
    public override fun initView() {
        val publishSubject = PublishSubject.create<Int>()
        publishSubject.buffer(20)
                .filter { it.isNotEmpty() }
                .map (Collections::max)
                .doOnNext {
                    it.toString().logi()
                }.subscribe()

        mBtnTest = findViewById<View>(R.id.btn_test) as Button
        mBtnTest!!.setOnClickListener(this)
        mTvTestCode = findViewById<View>(R.id.tv_test_code) as TextView
        mTvTestCode!!.setOnClickListener(this)
        viewpage2 = findViewById(R.id.viewPager2)
        viewpage2?.orientation = ViewPager2.ORIENTATION_VERTICAL
        val list = arrayListOf<Fragment>().apply {
            repeat(100) {
                add(if (it % 2 == 0) {
                    BlankMVVMkFragment.newInstance(it)
                } else {
                    BlankFragment.newInstance(it)
                })
            }
        }
        Timber.tag("koin").i(view2.rxContext.toString())
        Timber.tag("hilt").i(view3.rxContext.toString())
        test_download.setOnClickListener {
            DownloadWorker.startDownload(this, "https://dldir1.qq.com/wework/work_weixin/wxwork_android_3.0.31.13637_100001.apk",
                    EnvironmentStorage.getExternalFilesDir(), "wxwork_android_3.apk")
        }
        viewpage2?.adapter = AFragmentStateAdapter(this, list)
        viewpage2?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

        btn_test_tip.setOnClickListener {
            TipPopupWindow.showTipMsg(this, view = toolbar, tipMsg = "测试一下INFO")
            Timber.tag("DL").i(musicBean1.toJson())
            Timber.tag("DL").i(musicBean2.toJson())
        }

        btn_email.setOnClickListener {
            sendEmailText(addresses = "375105540@qq.com", title = "Base测试", content = "这是一个测试代码")
        }
        tv_test_number.setOnClickListener { text ->

            val va = ValueAnimator.ofInt(0, 50).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
                this.addUpdateListener {
                    (text as TextView).text = "X${(it.animatedValue as Int)}"
                }
                addListener(onStart = {
                    tv_test_number.animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.zoom_in)?.apply {
                        interpolator = OvershootInterpolator()
                        repeatCount = -1
                        repeatMode = REVERSE
                    }
                }, onEnd = {
                    tv_test_number.clearAnimation()
                })
            }
            va.start()

        }

        btn_select_image.setOnClickListener {
            ImageUtils.openLocalImage(this)
        }

        btn_mvvm.setOnClickListener {
            start<MVVMTestActivity>()
        }

        btn_setting.setOnClickListener {
            openSetting()
        }
        netTest()
        imgTest()
        findViewById<View>(R.id.fab).setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(mPresenter.getNightModel())
        }
//        testExecutor()

        btn_insert.setOnClickListener {

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

            saverCreate(key = "测试Hilt", value = "测试Hilt").apply {
                saverDao.insert(this)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError {
                            Timber.e(it)
                        }
                        .subscribe {
                            Timber.i("测试Hilt:saverDao = saved")
                        }
            }
        }

        btn_find.setOnClickListener {
            saverDB.getAll(groupN = "base", isDel = false)
                    .subscribeOn(Schedulers.io())
                    .doOnNext {
                        Timber.i(it.toJson())
                    }.subscribe()
        }
        requestNetWork()
        NetWorkLiveDate.getInstance().observe(this, {
            when (it?.netType) {
                NetWorkLiveDate.NetType.NONE -> Timber.tag("requestNetWork").i("hasNet = ${it.hasNet},netType = NONE")
                NetWorkLiveDate.NetType.PHONE -> Timber.tag("requestNetWork").i("hasNet = ${it.hasNet},netType = PHONE")
                NetWorkLiveDate.NetType.WIFI -> Timber.tag("requestNetWork").i("hasNet = ${it.hasNet},netType = WIFI")
            }
        })

        val adapter = SimPageAdapter().also { adapter ->

            launch {
                get<Pager<Int, Saver>>().flow.collectLatest {
                    doOnMain {
                        adapter.submitData(it)
                    }
                }
            }

        }
        val footer = SimPageLoadAdapter("footer")
        val header = SimPageLoadAdapter("header")
        recycle.adapter = adapter.withLoadStateHeaderAndFooter(header, footer)

        test_thread.setOnClickListener {
            TaskExecutor.exit()
            TaskExecutor.executeOnIO {
                Timber.tag("TaskExecutor").i(Thread.currentThread().name)
            }
        }
        test_loading.setOnClickListener {
            //测试带携程的loading
            SimLoadingDialog().showWithAction(this) {
                doOnIO {
                    "开始".logi()
                    delay(5000)
                    "结束".logi()
                }
            }
            repeat(40){
                publishSubject.onNext(it)
            }
        }


    }

    private fun imgTest() {
//        iv_test.loadImage("https://staticqc.lycheer.net/account3/static/media/levelrule.45f3b2f1.png")
//        downloadImage(this,url ="https://staticqc.lycheer.net/account3/static/media/levelrule.45f3b2f1.png"){
//
//        }
    }

    override fun onResume() {
        super.onResume()
        launch {
            delay(500)
            paste(this@MainActivity).logi()
        }
    }

    private fun netTest() {
        btn_test_net.setOnClickListener {

            mPresenter.getMusicByRxHttp(object : SimpleNetCallBack<ApiResult1<List<MusicBean>>>(this) {
                override fun onSuccess(data: ApiResult1<List<MusicBean>>) {
                    super.onSuccess(data)
                    Timber.tag("getMusicByRxHttp").i(data.toJson())
                }

                override fun onError(e: Exception) {
                    super.onError(e)
                    Timber.e(e)
                }
            })

            launch {

                doOnMain {
                    try {
                        val music = mPresenter.getMusic()
                        Timber.tag("getMusic").i(music.toJson())
                        music
                    } catch (e: Exception) {
                        Timber.i(e)
                        null
                    }
                }

                doOnIO {

                    try {
                        val music = mPresenter.getMusic()
                        Timber.tag("getMusic").i(music.toJson())
                        music
                    } catch (e: Exception) {
                        Timber.i(e)
                        null
                    }
                }


            }
        }
    }

    private fun testExecutor() {
        for (i in 0..100) {
            TaskExecutor.getInstance().executeOnDiskIO {
                Thread.sleep(100)
                Timber.i("executeOnDiskIO:${Thread.currentThread().name}")
            }
        }
        for (i in 0..9) {
            TaskExecutor.run {
                getInstance().executeOnMainThread {
                    Timber.i("executeOnMainThread:${Thread.currentThread().name}")
                }
            }
        }
    }

    public override fun initData() {
        codeUtil = CodeUtil(mTvTestCode!!)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_test -> startActivity(Intent(this, KtTestActivity::class.java))
            R.id.tv_test_code -> codeUtil!!.start()
            else -> {
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onActivityStart() {
        Timber.i("onActivityStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onActivityStop() {
        Timber.i("onActivityStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onActivityResume() {
        Timber.i("onActivityResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onActivityPause() {
        Timber.i("onActivityPause")
    }

    override fun onActivityCreate() {
        super.onActivityCreate()
        Timber.i("onActivityCreate")
    }

    override fun onActivityDestroy() {
        super.onActivityDestroy()
        codeUtil!!.stop()
        Timber.i("onActivityDestroy")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun updateView(message: Message) {
        super.updateView(message)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        ImageUtils.onActivityResult(this, requestCode, resultCode, data, object : SimpleCallBack<Uri>() {
            override fun onSuccess(key: Uri) {
                super.onSuccess(key)
                Timber.i("url = $key")
            }

            override fun onFail() {
                super.onFail()

            }
        })
    }
}
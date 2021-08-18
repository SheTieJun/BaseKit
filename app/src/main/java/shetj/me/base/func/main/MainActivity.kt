package shetj.me.base.func.main

import android.Manifest
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
import android.widget.EdgeEffect
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.paging.Pager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import me.shetj.base.base.TaskExecutor
import me.shetj.base.ktx.*
import me.shetj.base.model.NetWorkLiveDate
import me.shetj.base.mvp.BaseBindingActivity
import me.shetj.base.network.RxHttp
import me.shetj.base.network.callBack.SimpleNetCallBack
import me.shetj.base.saver.Saver
import me.shetj.base.tip.TipKit
import me.shetj.base.tip.TipPopupWindow
import me.shetj.base.tools.app.ArmsUtils.Companion.paste
import me.shetj.base.tools.file.EnvironmentStorage
import me.shetj.base.tools.image.ImageCallBack
import me.shetj.base.tools.image.ImageUtils
import me.shetj.base.tools.time.CodeUtil
import me.shetj.base.view.edge.SpringEdgeEffect
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import shetj.me.base.R
import shetj.me.base.api.BApi
import shetj.me.base.bean.ApiResult1
import shetj.me.base.bean.MusicBean
import shetj.me.base.common.worker.DownloadWorker
import shetj.me.base.databinding.ActivityMainBinding
import shetj.me.base.databinding.ContentMainBinding
import shetj.me.base.mvvmtest.MVVMTestActivity
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

class MainActivity : BaseBindingActivity<MainPresenter, ActivityMainBinding>(),
    View.OnClickListener {
    private lateinit var mContent: ContentMainBinding
    private var toolbar: Toolbar? = null
    private var mBtnTest: Button? = null
    private var mTvTestCode: TextView? = null
    private var codeUtil: CodeUtil? = null
    private var viewpage2: ViewPager2? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContent = mViewBinding.content
    }


    override fun initViewBinding(): ActivityMainBinding {
        return super.initViewBinding()
    }

    // 框架默认会通过反射创建 MainPresenter ，
    override fun initPresenter(): MainPresenter {
        return get { parametersOf(this) }
    }

    @SuppressLint("SetTextI18n")
    public override fun initView() {
        val publishSubject = PublishSubject.create<Int>()
        publishSubject.buffer(20)
            .filter { it.isNotEmpty() }
            .map(Collections::max)
            .doOnNext {
                it.toString().logi()
            }.subscribe()
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        mBtnTest = findViewById<View>(R.id.btn_test) as Button
        mBtnTest!!.setOnClickListener(this)
        mTvTestCode = findViewById<View>(R.id.tv_test_code) as TextView
        mTvTestCode!!.setOnClickListener(this)
        viewpage2 = findViewById(R.id.viewPager2)
        viewpage2?.orientation = ViewPager2.ORIENTATION_VERTICAL
        val list = arrayListOf<Fragment>().apply {
            repeat(100) {
                add(
                    if (it % 2 == 0) {
                        BlankMVVMkFragment()
                    } else {
                        BlankFragment()
                    }
                )
            }
        }
        findViewById<View>(R.id.test_download).setOnClickListener {
            DownloadWorker.startDownload(
                this,
                "https://dldir1.qq.com/wework/work_weixin/wxwork_android_3.0.31.13637_100001.apk",
                EnvironmentStorage.getExternalFilesDir(),
                "wxwork_android_3.apk"
            )
        }
        viewpage2?.adapter = AFragmentStateAdapter(this, list)
        viewpage2?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

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
        findViewById<View>(R.id.tv_test_number).setOnClickListener { text ->

            val va = ValueAnimator.ofInt(0, 50).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
                this.addUpdateListener {
                    (text as TextView).text = "X${(it.animatedValue as Int)}"
                }
                addListener(onStart = {
                    text.animation =
                        AnimationUtils.loadAnimation(this@MainActivity, R.anim.zoom_in)?.apply {
                            interpolator = OvershootInterpolator()
                            repeatCount = -1
                            repeatMode = REVERSE
                        }
                }, onEnd = {
                    text.clearAnimation()
                })
            }
            va.start()

        }

        findViewById<View>(R.id.btn_select_image).setOnClickListener {
            ImageUtils.selectLocalImage(this)
        }

        mContent.btnMvvm.setOnClickListener {
            start<MVVMTestActivity>()
        }

        mContent.btnSetting.setOnClickListener {
            openSetting()
        }
        netTest()
        findViewById<View>(R.id.fab).setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(mPresenter.getNightModel())
        }
        testExecutor()

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

//            Flowable.just("1")
//                    .lift(MyOperator())
//                    .compose(MyTransformer())
//                    .map { it.toString() }
//                    .subscribe()
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
        mViewBinding.content.recycle.adapter = adapter.withLoadStateHeaderAndFooter(header, footer)
        mViewBinding.content.recycle.apply {
            edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return SpringEdgeEffect(view, direction)
                }
            }

        }
        mViewBinding.content.testThread.setOnClickListener {
            TaskExecutor.exit()
            TaskExecutor.executeOnIO {
                Timber.tag("TaskExecutor").i(Thread.currentThread().name)
            }
        }
        mViewBinding.content.testLoading.setOnClickListener {
            TipKit.showLoadingWithCoroutine(this) {
                doOnIO {
                    "开始".logi()
                    delay(5000)
                    "结束".logi()
                }
            }
            repeat(40) {
                publishSubject.onNext(it)
            }
        }
        //设置本地dns 解析
        RxHttp.getInstance().addDnsMap(HashMap<String, String>().apply {
            put("baidy1.com", "127.0.0.1")
            put("baidy2.com", "127.0.0.1")
            put("baidy3.com", "127.0.0.1")
        })
        RxHttp.get("http://baidy1.com").executeCus(object : SimpleNetCallBack<String>(this) {

        })
        RxHttp.getInstance().getApiManager(BApi::class.java, baseUrl = "http://baidy1.com")
            .change("jwt1", HashMap()).subscribe()
        RxHttp.getInstance().getApiManager(BApi::class.java, baseUrl = "http://baidy2.com")
            .change("jwt1", HashMap()).subscribe()
    }

    private fun imgTest() {
//        iv_test.loadImage("https://staticqc.lycheer.net/account3/static/media/levelrule.45f3b2f1.png")
        launch {
            saveImage(shareCardUrl ="https://staticqc.lycheer.net/account3/static/media/levelrule.45f3b2f1.png")
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        "onBackPressed".logi()
    }

    override fun onResume() {
        super.onResume()
        launch {
            delay(500)
            paste(this@MainActivity).logi()
        }
    }

    private fun netTest() {
        mViewBinding.content.btnTestNet.setOnClickListener {

            mPresenter.getMusicByRxHttp(object :
                SimpleNetCallBack<ApiResult1<List<MusicBean>>>(this) {
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
                    } catch (e: Exception) {
                        Timber.i(e)
                    }
                }

                doOnIO {
                    try {
                        val music = mPresenter.getMusicV2()
                        Timber.tag("getMusicV2").i(music.toJson())
                    } catch (e: Exception) {
                        Timber.i(e)
                    }
                }

            }
        }
    }

    private fun testExecutor() {

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
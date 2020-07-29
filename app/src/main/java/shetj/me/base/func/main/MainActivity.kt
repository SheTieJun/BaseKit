package shetj.me.base.func.main

import android.animation.ValueAnimator
import android.animation.ValueAnimator.REVERSE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import me.shetj.base.mvp.BaseActivity
import me.shetj.base.base.TaskExecutor
import me.shetj.base.ktx.*
import me.shetj.base.model.NetWorkLiveDate
import me.shetj.base.mvp.IView
import me.shetj.base.network.RxHttp
import me.shetj.base.network.callBack.SimpleNetCallBack
import me.shetj.base.saver.SaverDao
import me.shetj.base.sim.SimpleCallBack
import me.shetj.base.tools.image.ImageUtils
import me.shetj.base.tools.time.CodeUtil
import me.shetj.base.view.TipPopupWindow
import org.koin.androidx.scope.lifecycleScope
import shetj.me.base.R
import shetj.me.base.bean.ApiResult1
import shetj.me.base.bean.MusicBean
import shetj.me.base.hilttest.main1
import shetj.me.base.mvvmtest.MVVMTestActivity
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor(): BaseActivity<MainPresenter>(), View.OnClickListener {
    private var mBtnTest: Button? = null
    private var mTvTestCode: TextView? = null
    private var codeUtil: CodeUtil? = null
    private var viewpage2: ViewPager2? = null
    @Inject  lateinit var musicBean1: MusicBean
    @Inject  lateinit var musicBean2: MusicBean
    @Inject  lateinit var saverDao:SaverDao

    val view2: IView = lifecycleScope.get()
    @main1 @Inject lateinit var view3: IView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

  // 框架默认会通过反射创建 MainPresenter ，
//    override fun initPresenter(): MainPresenter {
//        return lifecycleScope.get()
//    }

    public override fun initView() {
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
        NetWorkLiveDate.getInstance().observe(this, Observer {
            when (it.netType) {
                NetWorkLiveDate.NetType.AUTO -> Timber.tag("requestNetWork").i("hasNet = ${it.hasNet},netType = AUTO")
                NetWorkLiveDate.NetType.PHONE -> Timber.tag("requestNetWork").i("hasNet = ${it.hasNet},netType = PHONE")
                NetWorkLiveDate.NetType.WIFI -> Timber.tag("requestNetWork").i("hasNet = ${it.hasNet},netType = WIFI")
            }
        })
        requestNetWork()
    }

    private fun imgTest() {
//        iv_test.loadImage("https://staticqc.lycheer.net/account3/static/media/levelrule.45f3b2f1.png")
//        downloadImage(this,url ="https://staticqc.lycheer.net/account3/static/media/levelrule.45f3b2f1.png"){
//
//        }
    }

    private fun netTest() {
        btn_test_net.setOnClickListener {
            //            RxHttp.get("https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json")
            //                    .executeCus(object : SimpleNetCallBack<ResultMusic>(this) {
            //                        override fun onSuccess(data: ResultMusic) {
            //                            super.onSuccess(data)
            //                            Timber.i(data.toJson())
            //                        }
            //
            //                        override fun onError(e: Exception) {
            //                            super.onError(e)
            //                            Timber.e(e)
            //                        }
            //                    })

            //            RxHttp.get("https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json")
            //                    .executeCus(ResultMusic::class.java)
            //                    .map { it.data }
            //                    .subscribe ({
            //                        Timber.i(it.toJson())
            //                    },{
            //                        Timber.e(it)
            //                    })

            RxHttp.get("https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json")
                    .executeCus(object : SimpleNetCallBack<ApiResult1<List<MusicBean>>>(this) {
                        override fun onSuccess(data: ApiResult1<List<MusicBean>>) {
                            super.onSuccess(data)
                            Timber.i(data.toJson())
                        }

                        override fun onError(e: Exception) {
                            super.onError(e)
                            Timber.e(e)
                        }
                    })


            //            RxHttp.get("https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json")
            //                    .execute(object : SimpleNetCallBack<List<MusicBean>>(this) {
            //                        override fun onSuccess(data: List<MusicBean>) {
            //                            super.onSuccess(data)
            //                            Timber.i(data.toJson())
            //                        }
            //
            //                        override fun onError(e: Exception) {
            //                            super.onError(e)
            //                            Timber.e(e)
            //                        }
            //                    })

        }
    }

    private fun testExecutor() {
        for (i in 0..100) {
            TaskExecutor.getInstance().executeOnDiskIO(Runnable {
                Thread.sleep(100)
                Timber.i("executeOnDiskIO:${Thread.currentThread().name}")
            })
        }
        for (i in 0..9) {
            TaskExecutor.getInstance().executeOnMainThread(Runnable {
                Timber.i("executeOnMainThread:${Thread.currentThread().name}")
            })
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
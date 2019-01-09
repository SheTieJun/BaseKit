# Base
   自用

#### 可能使用的库介绍
1. 事件总线 [androideventbus](https://github.com/hehonghui/AndroidEventBus)
2. 数据库  [xUtils3（Db](https://github.com/wyouflf/xUtils3)）
3. recycleview[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/)
3. 图片加载 [Glide](https://github.com/bumptech/glide)
4. webView  [AgentWeb](https://github.com/Justson/AgentWeb)
4. Gson解析  [Gson](https://github.com/google/gson)
5. 6.0权限管理 [RxPermissions](https://github.com/tbruyelle/RxPermissions)
```
RxView.clicks(findViewById(R.id.enableCamera))
    .compose(rxPermissions.ensure(Manifest.permission.CAMERA))
    .subscribe(granted -> {
        // R.id.enableCamera has been clicked
    });
```
6. 侧滑退出 [swipebacklayout](https://github.com/qmdx/SwipeBackLayout)
7. 视频缓存管理 [AndroidVideoCache](https://github.com/danikula/AndroidVideoCache)
8. 通用的popupwindow [BasePopup](https://github.com/razerdp/BasePopup)
10. 网络请求 -----（[xUtils3](https://github.com/wyouflf/xUtils3)）[Retrofit2+RxJava2=RxEasyHttp](https://github.com/zhou-you/RxEasyHttp) ）
12. Android内存泄漏检测工具 [LeakCanary](https://github.com/square/leakcanary)
13. 多状态视图 [MultipleStatusView](https://github.com/qyxxjd/MultipleStatusView)
14. 点赞+1效果 [GoodView](https://github.com/venshine/GoodView)
15. 屏幕适配  [AndroidAutoLayout](https://github.com/hongyangAndroid/AndroidAutoLayout)
16. 图片浏览 [ZoomPreviewPicture](https://github.com/yangchaojiang/ZoomPreviewPicture)
17. 仿照微信录制视频[JCameraView](https://github.com/CJT2325/CameraView)
18. 九图展示[NineGridImageView](https://jaeger.itscoder.com/android/2016/03/06/nine-grid-iamge-view-libaray.html)
19. Adding Components to your Project [Android Architecture Components](https://developer.android.google.cn/topic/libraries/architecture/index.html)
20. 二维码
21. Dialog [MaterialDialog](https://github.com/afollestad/material-dialogs)
```
	MaterialDialog materialdialog = new MaterialDialog.Builder(getRxContext())
								.title("版本更新：V"+info.getAppVersionName())
								.titleColorRes(R.color.themColor)
								.content(HtmlCompat.fromHtml(info.getAppChangeLog()))
								.positiveText("更新")
								.positiveColorRes(R.color.themColor)
								.neutralColorRes(R.color.gray)
								.onPositive((dialog, which) -> {
									dialog.dismiss();
								})
								.onNeutral((dialog, which) -> {
									dialog.dismiss();
								})
								.neutralText("暂不更新").build();
				materialdialog.show();
```
22. 视频播放[GSYVideoPlayer](https://github.com/CarGuo/GSYVideoPlayer)
23. Android-SpinKit 加载进度条（登录，网络请求）[SpinKit](https://github.com/ybq/Android-SpinKit)
```
<com.github.ybq.android.spinkit.SpinKitView
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/spin_kit"
    style="@style/SpinKitView.Large.Circle"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    app:SpinKit_Color="@color/colorAccent" />
```
24. 一步集成侧滑(删除)菜单[SwipeDelMenuLayout](https://github.com/mcxtzhang/SwipeDelMenuLayout)
```
<com.mcxtzhang.swipemenulib.SwipeMenuLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="100dp"
    android:clickable="true"
    android:paddingBottom="1dp">
    <TextView
        android:id="@+id/content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="?android:attr/selectableItemBackground"
        android:gravity="center"
        android:text="项目中我是任意复杂的原ContentItem布局"/>
    <Button
        android:id="@+id/btnDelete"
        android:layout_width="60dp"
        android:layout_height="match_parent"
        android:background="@color/red_ff4a57"
        android:text="删除"
        android:textColor="@android:color/white"/>
</com.mcxtzhang.swipemenulib.SwipeMenuLayout>
```
25. Rx套餐
```
    api 'com.trello.rxlifecycle2:rxlifecycle:2.2.1'
    api 'com.trello.rxlifecycle2:rxlifecycle-components:2.2.0'
    api 'io.reactivex.rxjava2:rxjava:2.1.8'
    api 'io.reactivex.rxjava2:rxandroid:2.0.1'
    api 'com.jakewharton.rxbinding2:rxbinding:2.1.1'
    api 'io.reactivex.rxjava2:rxkotlin:2.2.0'
```
26. 日志
```
    api 'com.jakewharton.timber:timber:4.7.1'

       /**
         * 设置log自动在debug打开，在release关闭，可以在Application的onCreate中设置
         * @param isDebug
         */
        public static void setLogAuto(boolean isDebug){
            //打印关，同时gradle中的release的debuggable要设置为false
            if (isDebug) {
                Timber.plant(new Timber.DebugTree());
            } else {//release版本
                Timber.plant(new CrashReportingTree());
            }
        }
```
27. 图片查看

```

    api 'com.github.iielse:ImageWatcher:1.1.5'
Activity.onCreate()

ImageWatcherHelper iwHelper = ImageWatcherHelper.with(this, new SimpleLoader());  // SimpleLoader demo中有简单实现
Activity.onBackPressed()

if (!iwHelper.handleBackPressed()) {
    super.onBackPressed();
}

```


#### 第三方平台 ####

9. 推送 [极光推送](https://docs.jiguang.cn/jpush/guideline/intro/)
10. 分享 [极光分享](https://docs.jiguang.cn/jshare/guideline/intro/)或者[友盟分享](http://mobile.umeng.com)
11. 云存储 七牛 [阿里云OSS](https://help.aliyun.com/document_detail/32042.html?spm=5176.87240.400427.45.sHMUpt)
```
 api 'com.aliyun.dpa:oss-android-sdk:2.4.3'
   api 'com.qiniu:qiniu-android-sdk:7.3.11'//7牛
```
12. 统计分析  目前用的[极光统计](https://docs.jiguang.cn/janalytics/guideline/intro/) 或者[友盟统计](http://mobile.umeng.com/analytics) 还有 腾讯的统计
13. 通讯验证码 [阿里大于](https://dayu.aliyun.com/product/sms?spm=a3142.7791109.0.0.1d6d73c4oGtKxk)----后台负责
14. 录制视频 [Android短视频SDK](https://help.aliyun.com/document_detail/53421.html?spm=5176.doc54832.6.688.5hqB17)
15. 即时通讯  [融云SDK](http://www.rongcloud.cn/docs/)
16. [腾讯云统计](http://mta.qq.com/?ADTAG=EXTERNAL.MEDIA.ANALYSIS_122)
17. 微信
```
com.tencent.mm.opensdk:wechat-sdk-android-with-mta:5.1.4
```
## 梨花声音大学APP
###  问题总结

### 前期准备
1. 软著必须的

### 技术
1. webView 播放视频的问题
   1. 检测暂停困难
   2. 重新加载，高度变化，只能重新创建
2. 视频播放器
   1. 腾讯的视频不是很好，demo 也写的很不好，最后通过各种移除父布局，添加到其他部件完成了
   2. 保存进度问题，最后快结束的默认结束，把进度保存为0 ，防止，进入课程就自动跳到下一节了
3. MotionLayout 使用问题
   1. 被使用的id ,无法通过代码进行修改显示隐藏
4. ViewBinding新用法
   1. 不是全能，因为先加在父类，子类使用viewBinding 在一些方法可能出现空异常
5. 30 存储兼容还是没有解决
6. recycleView 有圆角点击效果：最后view切换圆角方式
```
   fun View.clipRound(radius: Float = 10f.dp.toFloat()) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {

                outline.setRoundRect(
                        0,
                        0,
                        view.width,
                        view.height,
                        radius
                )
            }
        }
        clipToOutline = true
    }
}
```

8. 录音组件，预制过程没有结束，但是被用户结束反馈不及时的问题
9. Toast Loading写法修改
10. 网络请求库，使用协程
11. 手机区号展示，忘记混淆了
12. 混淆问题 * 和**
13. liveData 使用，liveData 如果是fragment ，分页会存在问题
14. ViewModel 使用
15. 动画  postInvalidateOnAnimation() ：防止主线程太多需要处理的东西，activity结束会延迟10s
16. X5 白屏问题：重新全屏点击事件
17. 视频播放被劫持：腾讯云的获取视频地址的接口，需要配置才可以返回https

### 发版
1. Android隐私协议弹窗，最后才注意
2. 上应用市场，需要准备应市场免责函
3. 简介中不可以有特殊字，比如首创、最这些
4. QQ分享问题：应用宝的应用虽然会自动到QQ互联，但是如果你在QQ互联用了同样的名字，应用宝的应用就不会自动过来
5. 微信分享问题：发布后要需要修改上架状态，否则无法进行分享（每天有100次）
6. 个性化通知：隐私协议中如果有，就需要有这个设置，否则华为市场会警告，甚至下架


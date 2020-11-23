package shetj.me.base.di_hilttest

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.shetj.base.mvp.IView
import shetj.me.base.func.main.KtTestActivity
import shetj.me.base.func.main.MainActivity
import javax.inject.Qualifier

/**
 * 抽象实
 * Qualifier 用来标记 不同的 为同一类型提供多个绑定
 */


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class main1

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class main2


@InstallIn(value = [ApplicationComponent::class])
@Module
abstract class AbstractModule {

    //KtTestActivity @Inject constructor()    + @AndroidEntryPoint
    @main1
    @Binds  //抽象实例 （括号里面是具体实现）
    abstract fun getIView(iView: KtTestActivity): IView


    @main2
    @Binds
    abstract fun getIView2(iView: MainActivity): IView
}
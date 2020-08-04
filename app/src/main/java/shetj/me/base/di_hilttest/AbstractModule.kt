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
 */

@Qualifier
annotation class main1

@Qualifier
annotation class main2


@InstallIn(value = [ApplicationComponent::class])
@Module
abstract class AbstractModule {

    //KtTestActivity @Inject constructor()    + @AndroidEntryPoint
    @main1
    @Binds
    abstract fun getIView(iView: KtTestActivity):IView


    @main2
    @Binds
    abstract fun getIView2(iView: MainActivity):IView
}
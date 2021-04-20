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
}
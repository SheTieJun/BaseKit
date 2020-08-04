package shetj.me.base.hilttest

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.shetj.base.ktx.getRandomString
import shetj.me.base.bean.MusicBean
import javax.inject.Singleton

/**
 * A @Module may not contain both non-static and abstract binding
 * 抽象的和 非静态绑定 不能再一个类中
 */
@InstallIn(value = [ApplicationComponent::class])
@Module
class TestModel {

    @Singleton //单例，必须放在ApplicationComponent
    @Provides
    fun getMusicBean(): MusicBean {
        return MusicBean().apply {
            url = getRandomString(10)
        }
    }


}
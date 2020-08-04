package shetj.me.base.di_hilttest

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.shetj.base.saver.SaverDao
import me.shetj.base.saver.SaverDatabase
import javax.inject.Singleton


/**
 * 用来数据提供者
 */
@InstallIn(value = [ApplicationComponent::class])
@Module
object DataBaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext:Context): SaverDatabase {
        return SaverDatabase.getInstance(context = appContext)
    }

    @Provides
    fun provideSaverDao(database: SaverDatabase):SaverDao{
        return database.saverDao()
    }
}
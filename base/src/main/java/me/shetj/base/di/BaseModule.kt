package me.shetj.base.di

import me.shetj.base.saver.SaverDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dbModule = module() {

    single { SaverDatabase.getInstance(androidApplication()) }

    //try to override existing definition.
    // Please use override option or check for definition '[Single:'me.shetj.base.saver.SaverDao']'
    single(override = true) { get<SaverDatabase>().saverDao() }

}
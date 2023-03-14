

package shetj.me.base.di_kointest

import org.koin.dsl.module
import shetj.me.base.common.bean.UpToken


val appModule = module() {
    single { UpToken() } //单例

}


val mvvmModule = module {
}

/**
 * Scope就是一个实例的作用范围，一旦该作用范围结束，该实例就是从容器中移除。
 *  single： 单实例scope，该scope下的实例不会被移除
 *  factory： 每次都是创建新实例
 *  scoped： 自定义scope
 */
val mvpModule = module {

}

val scopeModule = module {

}

val allModules = appModule + mvpModule + mvvmModule + scopeModule
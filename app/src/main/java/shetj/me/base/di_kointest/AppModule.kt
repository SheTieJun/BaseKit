package shetj.me.base.di_kointest

import me.shetj.base.mvp.IView
import org.koin.dsl.module
import shetj.me.base.common.bean.UpToken
import shetj.me.base.common.manager.CommonModel
import shetj.me.base.common.manager.CommonPresenter
import shetj.me.base.func.main.BlankFragment
import shetj.me.base.func.main.BlankMVVMkFragment
import shetj.me.base.func.main.MainActivity
import shetj.me.base.func.main.MainPresenter
import shetj.me.base.mvvmtest.MVVMViewModel


val appModule = module() {
    single { UpToken() } //单例

}


val mvvmModule = module {

    single { MVVMViewModel() }
}

/**
 * Scope就是一个实例的作用范围，一旦该作用范围结束，该实例就是从容器中移除。
 *  single： 单实例scope，该scope下的实例不会被移除
 *  factory： 每次都是创建新实例
 *  scoped： 自定义scope
 */
val mvpModule = module {
    //工程创建？  val presenter : Presenter by inject { parametersOf(view) }
    //单例
    single { CommonModel() }

    // // Inject presenter from MVPActivity's scope
    //    val scopedPresenter: MainPresenter by lifecycleScope.inject()
    factory { (view: IView) -> MainPresenter(view) }  //使用参数创建
    factory { (view: IView) -> CommonPresenter(view) }

    scope<BlankFragment> {

    }

    scope<BlankMVVMkFragment> {

    }
}

val scopeModule = module {

}

val allModules = appModule + mvpModule + mvvmModule + scopeModule
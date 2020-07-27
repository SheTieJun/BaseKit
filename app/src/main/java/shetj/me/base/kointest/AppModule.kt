package shetj.me.base.kointest

import androidx.lifecycle.ViewModel
import me.shetj.base.base.IView
import me.shetj.base.ktx.saverDB
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import shetj.me.base.`fun`.main.BlankFragment
import shetj.me.base.`fun`.main.MainActivity
import shetj.me.base.`fun`.main.MainPresenter
import shetj.me.base.common.bean.UpToken
import shetj.me.base.common.manager.CommonModel
import shetj.me.base.common.manager.CommonPresenter


val appModule = module() {
    single { UpToken() } //单例

    single { saverDB }
}



val mvvmModule = module {
    viewModel (named("test")){ object :ViewModel(){} }
    fragment { BlankFragment() }
}

/**
 * Scope就是一个实例的作用范围，一旦该作用范围结束，该实例就是从容器中移除。
 *  single： 单实例scope，该scope下的实例不会被移除
 *  factory： 每次都是创建新实例
 *  scoped： 自定义scope
 */
val mvpModule = module {
    //工程创建？  val presenter : Presenter by inject { parametersOf(view) }
    factory { (view:IView) -> CommonPresenter(view) }
    //单例
    single { CommonModel() }

    factory {  }

    // // Inject presenter from MVPActivity's scope
    //    val scopedPresenter: MainPresenter by lifecycleScope.inject()
    scope<MainActivity> {
        scoped { MainPresenter(get()) }
    }
}

val scopeModule = module {

}

val allModules = appModule + mvpModule + mvvmModule + scopeModule
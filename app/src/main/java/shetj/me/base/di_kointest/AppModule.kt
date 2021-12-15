/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package shetj.me.base.di_kointest

import me.shetj.base.mvp.IView
import org.koin.dsl.module
import shetj.me.base.common.bean.UpToken
import shetj.me.base.common.manager.CommonModel
import shetj.me.base.common.manager.CommonPresenter
import shetj.me.base.func.main.MainPresenter


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
    //工程创建？  val presenter : Presenter by inject { parametersOf(view) }
    //单例
    single { CommonModel() }

    // // Inject presenter from MVPActivity's scope
    //    val scopedPresenter: MainPresenter by lifecycleScope.inject()
    factory { (view: IView) -> MainPresenter(view) }  //使用参数创建
    factory { (view: IView) -> CommonPresenter(view) }
}

val scopeModule = module {

}

val allModules = appModule + mvpModule + mvvmModule + scopeModule
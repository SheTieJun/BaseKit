package me.shetj.base.mvvm


import androidx.annotation.Keep
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope

/**
 *  TODO 还未测试
 *
 *  Koin Activity : 需要在提前在module{
 *   scope< S:BaseKoinBindingActivity > {
 *   }
 *   }
 */
@Keep
abstract class BaseKoinBindingActivity<VM : ViewModel, VB : ViewBinding> : BaseBindingActivity<VM, VB>(), LifecycleObserver, AndroidScopeComponent {

    override val scope: Scope by activityScope()

}

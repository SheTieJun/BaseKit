package me.shetj.base.mvvm.viewbind

import androidx.annotation.Keep
import androidx.lifecycle.LifecycleObserver
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
abstract class BaseKoinBindingActivity<VB : ViewBinding, VM : BaseViewModel> :
    BaseBindingActivity<VB, VM>(), LifecycleObserver, AndroidScopeComponent {

    override val scope: Scope by activityScope()
}

package me.shetj.base.mvvm.viewbind

import androidx.annotation.Keep
import androidx.lifecycle.LifecycleObserver
import androidx.viewbinding.ViewBinding
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.core.scope.Scope

/**
/**TODO 还未测试

 *  Koin Activity : need
 *   scope< S:BaseKoinBindingActivity > {
 *   }
*/
 */
@Keep
abstract class BaseKoinBindingFragment<VB : ViewBinding, VM : BaseViewModel> :
    BaseBindingFragment<VB, VM>(), LifecycleObserver, AndroidScopeComponent {

    override val scope: Scope by fragmentScope()
}

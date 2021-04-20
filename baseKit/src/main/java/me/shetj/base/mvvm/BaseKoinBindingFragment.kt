package me.shetj.base.mvvm

import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.viewbinding.ViewBinding
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.core.scope.Scope

/**
/**
 *  Koin Activity : need
 *   scope< S:BaseKoinBindingActivity > {
 *   }
*/
 */
@Keep
abstract class BaseKoinBindingFragment<VM : BaseViewModel, VB : ViewBinding> : BaseBindingFragment<VM, VB>(), LifecycleObserver, AndroidScopeComponent {

    override val scope: Scope by fragmentScope()

}

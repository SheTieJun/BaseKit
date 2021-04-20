package me.shetj.base.mvvm


import androidx.annotation.Keep
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope

/**
 *  Koin Activity : need
 *   scope< S:BaseKoinBindingActivity > {
 *   }
 */
@Keep
abstract class BaseKoinBindingActivity<VM : BaseViewModel, VB : ViewBinding> : BaseBindingActivity<VM, VB>(), LifecycleObserver, AndroidScopeComponent {

    override val scope: Scope by activityScope()

}

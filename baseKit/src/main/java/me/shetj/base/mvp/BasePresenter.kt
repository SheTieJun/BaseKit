package me.shetj.base.mvp

import android.content.Intent
import androidx.annotation.CallSuper
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import me.shetj.base.coroutine.DispatcherProvider
import me.shetj.base.ktx.getObjByClassArg
import me.shetj.base.ktx.toMessage
import kotlin.coroutines.CoroutineContext

/**
 *
 * 在model中获取数据，在view（Activity）中展示数据
 * @author shetj
 */
@Keep
open class BasePresenter<T : BaseModel>(protected var view: IView) : CoroutineScope {

    protected val model: T by lazy { initModel() }

    override val coroutineContext: CoroutineContext
        get() = DispatcherProvider.main() + SupervisorJob()

    internal val rxContext: AppCompatActivity
        get() = view.rxContext

    init {
        onStart()
    }

    open fun initModel(): T {
        return getObjByClassArg(this)
    }

    open fun onStart() {
    }

    /**
     * //解除订阅
     * [BaseActivity.onDestroy] 调用[Presenter.onDestroy]
     */

    fun onDestroy() {
        coroutineContext.cancelChildren()
        model.onDestroy()
    }

    fun startActivity(intent: Intent) {
        view.rxContext.startActivity(intent)
    }

    fun updateView(code: Int, msg: Any) {
        view.updateView(msg.toMessage(code))
    }
}

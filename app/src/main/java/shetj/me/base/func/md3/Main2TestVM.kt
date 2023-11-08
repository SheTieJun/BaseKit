package shetj.me.base.func.md3

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import me.shetj.base.ktx.isTrue
import me.shetj.base.model.GrayThemeLiveData
import me.shetj.base.mvvm.viewbind.BaseViewModel
import shetj.me.base.R

/**
 *
 */
class Main2TestVM : BaseViewModel() {
    var isGrayTheme = GrayThemeLiveData.getInstance().isTrue()

    val themInfo = MutableLiveData("正常模式")

    fun changeThem() {
        isGrayTheme = !isGrayTheme
        GrayThemeLiveData.getInstance().postValue(isGrayTheme)
        themInfo.value = (
            if (isGrayTheme) {
                "正常模式"
            } else {
                "灰色模式"
            }
            )
    }

    fun testFab(view: View) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAnchorView(R.id.fab)
            .setAction("Action", null).show()
    }
}



package shetj.me.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Field
import shetj.me.base.R


class FixRecyclerView  :
    RecyclerView {
    constructor(context: Context):super(context){
        change()
    }

    constructor(context: Context, attrs: AttributeSet?):super(context, attrs){
        change()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):super(context, attrs, defStyleAttr){
        change()
    }

    private fun change() {

        try {
            val field: Field = (this::class.java.genericSuperclass as Class<*>).getDeclaredField("mRecyclerView")
            field.isAccessible = true
            val recyclerView = field.get(this) as RecyclerView
            recyclerView.id = R.id.viewpager2_rv
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }


    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(e)
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return super.onTouchEvent(e)
    }
}
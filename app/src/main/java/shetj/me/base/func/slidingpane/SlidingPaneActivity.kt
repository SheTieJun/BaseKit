package shetj.me.base.func.slidingpane

import androidx.recyclerview.widget.GridLayoutManager
import me.shetj.base.ktx.logI
import me.shetj.base.mvvm.viewbind.BaseBindingActivity
import me.shetj.base.mvvm.viewbind.BaseViewModel
import shetj.me.base.databinding.ActivitySlidingPaneBinding
import shetj.me.base.day.Month
import shetj.me.base.day.MonthAdapter

class SlidingPaneActivity : BaseBindingActivity<ActivitySlidingPaneBinding,BaseViewModel>() {

    override fun initBaseView() {
        super.initBaseView()
        addMonthAdapter()
    }

    private fun addMonthAdapter() {
        val current = Month.current()

        """daysInMonth:${current.daysInMonth}
            |month:${current.month}
            |year:${current.year}
            |timeInMillis:${current.timeInMillis}
            |daysInWeek:${current.daysInWeek}
        """.trimMargin().logI()
        mBinding.recyclerView.layoutManager = GridLayoutManager(this, 7)
        mBinding.recyclerView.adapter = MonthAdapter(current)

    }
}
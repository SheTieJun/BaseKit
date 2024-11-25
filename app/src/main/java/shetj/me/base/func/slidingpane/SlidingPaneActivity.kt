package shetj.me.base.func.slidingpane

import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import me.shetj.base.ktx.logI
import me.shetj.base.mvvm.viewbind.BaseBindingActivity
import me.shetj.base.mvvm.viewbind.BaseViewModel
import shetj.me.base.BuildConfig
import shetj.me.base.databinding.ActivitySlidingPaneBinding
import shetj.me.base.day.Month
import shetj.me.base.day.MonthAdapter
import shetj.me.base.rv.MyItemDetailsLookup
import shetj.me.base.rv.MySelectionPredicate

class SlidingPaneActivity : BaseBindingActivity<ActivitySlidingPaneBinding, BaseViewModel>() {

    override fun initBaseView() {
        super.initBaseView()
        addMonthAdapter()
    }

    private fun addMonthAdapter() {
        onBackPressedDispatcher.addCallback(this, TwoPaneOnBackPressedCallback(mBinding.root))
        val current = Month.current()
        BuildConfig.APPLICATION_ID
        """daysInMonth:${current.daysInMonth}
            |month:${current.month}
            |year:${current.year}
            |timeInMillis:${current.timeInMillis}
            |daysInWeek:${current.daysInWeek}
        """.trimMargin().logI()
        mBinding.recyclerView.layoutManager = GridLayoutManager(this, 7)

        val myAdapter = MonthAdapter(current)
        mBinding.recyclerView.adapter = myAdapter

        val tracker = SelectionTracker.Builder<Long>(
            "mySelection",
            mBinding.recyclerView,
            StableIdKeyProvider(mBinding.recyclerView),
            MyItemDetailsLookup(mBinding.recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(MySelectionPredicate()).build()

        myAdapter.setSelectionTracker(tracker)
        tracker.selection.forEach { id ->

        }

    }
}

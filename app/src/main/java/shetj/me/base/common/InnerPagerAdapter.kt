package shetj.me.base.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

open class InnerPagerAdapter : FragmentStateAdapter {


    private var fragments = ArrayList<Fragment>()

    constructor(fragmentActivity: FragmentActivity, fragments: ArrayList<Fragment>) : super(fragmentActivity) {
        this.fragments = fragments
    }

    constructor(fragment: Fragment, fragments: ArrayList<Fragment>) : super(fragment) {
        this.fragments = fragments
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
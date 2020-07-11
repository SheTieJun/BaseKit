package me.shetj.base.base

import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * @author shetj
 */
@Keep
class AdapterViewPager : FragmentStatePagerAdapter {
    private var mList: MutableList<Fragment>? = null
    private var mTitles: MutableList<String>? = null
    private var mFragmentManager: FragmentManager? = null

    constructor(fragmentManager: FragmentManager, list: MutableList<Fragment>) : super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        mFragmentManager = fragmentManager
        this.mList = list
    }


    constructor(fragmentManager: FragmentManager, list: MutableList<Fragment>, titles: MutableList<String>) : super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        mFragmentManager = fragmentManager
        this.mList = list
        this.mTitles = titles
    }

    fun setData(list: List<Fragment>, titles: List<String>) {
        this.mList?.clear()
        this.mTitles?.clear()
        this.mList?.addAll(list)
        this.mTitles?.addAll(titles)
        notifyDataSetChanged()
    }


    override fun getItem(position: Int): Fragment {
        return mList!![position]
    }

    override fun getCount(): Int {
        return mList!!.size
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val instantiateItem = super.instantiateItem(container, position) as Fragment
        val item = mList!![position]
        return if (instantiateItem === item) {
            instantiateItem
        } else {
            //如果集合中对应下标的fragment和fragmentManager中的对应下标的fragment对象不一致，那么就是新添加的，所以自己add进入；这里为什么不直接调用super方法呢，因为fragment的mIndex搞的鬼，以后有机会再补一补。
            mFragmentManager!!.beginTransaction().add(container.id, item).commitNowAllowingStateLoss()
            item
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as Fragment
        //如果getItemPosition中的值为PagerAdapter.POSITION_NONE，就执行该方法。
        if (mList!!.contains(fragment)) {
            super.destroyItem(container, position, fragment)
            return
        }
        //自己执行移除。因为mFragments在删除的时候就把某个fragment对象移除了，所以一般都得自己移除在fragmentManager中的该对象。
        mFragmentManager!!.beginTransaction().remove(fragment).commitNowAllowingStateLoss()

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (mTitles != null) {
            mTitles!![position]
        } else super.getPageTitle(position)
    }

}
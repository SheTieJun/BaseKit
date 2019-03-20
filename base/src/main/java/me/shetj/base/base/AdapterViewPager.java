package me.shetj.base.base;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author shetj
 */
@Keep
public class AdapterViewPager extends FragmentStatePagerAdapter {
    private List<BaseFragment> mList;
    private  List<String> mTitles;
    private   FragmentManager mFragmentManager;

    public AdapterViewPager(FragmentManager fragmentManager, List<BaseFragment> list) {
        super(fragmentManager);
        mFragmentManager = fragmentManager;
        this.mList = list;
    }


    public AdapterViewPager(FragmentManager fragmentManager, List<BaseFragment> list, List<String> titles) {
        super(fragmentManager);
        mFragmentManager = fragmentManager;
        this.mList = list;
        this.mTitles = titles;
    }

    public void setData(List<BaseFragment> list, List<String> titles){
        this.mList.clear();
        this.mTitles.clear();
        this.mList.addAll(list);
        this.mTitles.addAll(titles);
        notifyDataSetChanged();
    }


    @Override
    public BaseFragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null) {
            return mTitles.get(position);
        }
        return super.getPageTitle(position);
    }
    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Fragment instantiateItem = ((Fragment) super.instantiateItem(container, position));
        Fragment item = mList.get(position);
        if (instantiateItem == item) {
            return instantiateItem;
        } else {
            //如果集合中对应下标的fragment和fragmentManager中的对应下标的fragment对象不一致，那么就是新添加的，所以自己add进入；这里为什么不直接调用super方法呢，因为fragment的mIndex搞的鬼，以后有机会再补一补。
            mFragmentManager.beginTransaction().add(container.getId(), item).commitNowAllowingStateLoss();
            return item;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        //如果getItemPosition中的值为PagerAdapter.POSITION_NONE，就执行该方法。
        if (mList.contains(fragment)) {
            super.destroyItem(container, position, fragment);
            return;
        }
        //自己执行移除。因为mFragments在删除的时候就把某个fragment对象移除了，所以一般都得自己移除在fragmentManager中的该对象。
        mFragmentManager.beginTransaction().remove(fragment).commitNowAllowingStateLoss();

    }
}
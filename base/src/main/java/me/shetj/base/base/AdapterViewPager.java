package me.shetj.base.base;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author shetj
 */
@Keep
public class AdapterViewPager extends FragmentPagerAdapter {
    private List<BaseFragment> mList;
    private  List<String> mTitles;

    public AdapterViewPager(FragmentManager fragmentManager, List<BaseFragment> list) {
        super(fragmentManager);
        this.mList = list;
    }


    public AdapterViewPager(FragmentManager fragmentManager, List<BaseFragment> list, List<String> titles) {
        super(fragmentManager);
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


    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BaseFragment f = (BaseFragment) super.instantiateItem(container, position);
        View view = f.getView();
        if (view != null) {
            container.addView(view);
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null) {
            return mTitles.get(position);
        }
        return super.getPageTitle(position);
    }

}
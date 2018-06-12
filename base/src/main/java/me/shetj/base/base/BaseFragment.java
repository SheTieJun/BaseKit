package me.shetj.base.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.components.support.RxFragment;

import org.simple.eventbus.EventBus;

import java.lang.reflect.Field;

import me.shetj.base.tools.app.ArmsUtils;
import me.shetj.base.view.LoadingDialog;

/**
 * fragment基类
 * @author shetj
 */
@Keep
public abstract class BaseFragment<T extends BasePresenter> extends RxFragment implements IView {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    /**
     * The M activity.
     */
    protected Context mActivity;
    /**是否可见状态*/
    protected boolean isVisible;
    /**View已经初始化完成*/
    private boolean isPrepared;
    /**是否第一次加载完*/
    private boolean isFirstLoad = true;

    protected T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        //绑定View
        isPrepared = true;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEventAndData();
        lazyLoad();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        //如果要使用eventbus请将此方法返回true
        if (useEventBus())
        {
	        EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (useEventBus())
        {
	        EventBus.getDefault().unregister(this);
        }
        this.mActivity = null;
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return boolean
     */
    protected boolean useEventBus() {
        return true;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    @Override
    public void onAttach(Context context) {
        this.mActivity = context;
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            isVisible = true;
            onVisible();
        }else {
            isVisible = false;
            onInvisible();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            isVisible = true;
            onVisible();
        }else {
            isVisible = false;
            onInvisible();
        }
    }
    /**
     * On visible.
     */
    protected void onVisible(){
        lazyLoad();
    }

    /**
     * On invisible.
     */
    protected void onInvisible(){}

    /**
     * Lazy load.
     */
    protected void lazyLoad(){
        if(!isPrepared || !isVisible || !isFirstLoad) {
	        return;
        }
        isFirstLoad = false;
        lazyLoadData();
    }

    /**
     * Init event and data.
     */
    protected abstract void initEventAndData();

    /**
     * Lazy load data.
     */
    public abstract void lazyLoadData();


    @Override
    public void showLoading(String msg) {

        LoadingDialog.showLoading(getRxContext(), msg, true);
    }
    @Override
    public void hideLoading() {
       LoadingDialog.hideLoading();
    }

    @Override
    public void showMessage(@NonNull String message) {
        ArmsUtils.makeText(message);
    }

    /**
     * 返回当前的activity
     * @return
     */
    @Override
    public RxAppCompatActivity getRxContext(){
        return (RxAppCompatActivity) mActivity;
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        super.onDestroyView();
    }
    @SuppressLint("unchecked")
    @Override
    public void updateView(BaseMessage message) {

    }
}

package me.shetj.base.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.simple.eventbus.EventBus;
import org.xutils.common.util.LogUtil;

import me.shetj.base.R;
import me.shetj.base.tools.app.ArmsUtils;
import me.shetj.base.tools.app.HideUtil;
import me.shetj.base.tools.json.EmptyUtils;
import me.shetj.base.view.LoadingDialog;

import static me.shetj.base.tools.app.ThirdViewUtil.convertAutoView;

/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
public abstract class BaseActivity<T extends BasePresenter> extends RxAppCompatActivity implements IView {

    protected T mPresenter ;
    protected RxPermissions rxPermissions = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果要使用eventbus请将此方法返回true
        if (useEventBus()) {
            //注册到事件主线
            EventBus.getDefault().register(this);
        }
        HideUtil.init(this);
        rxPermissions = new RxPermissions(this);
        startAnimation();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 连接view
     */
    protected abstract void initView();

    /**
     * 连接数据
     */
    protected abstract void initData();

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = convertAutoView(name, context, attrs);
        return view == null ? super.onCreateView(name, context, attrs) : view;
    }
    /**
     * 针对6.0动态请求权限问题
     * 判断是否允许此权限
     *
     * @param permissions  权限
     * @return hasPermission
     */
    protected boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return useEventBus
     */
    protected boolean useEventBus() {
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (useEventBus()){
            //如果要使用eventbus请将此方法返回true
            EventBus.getDefault().unregister(this);
        }
        if (null != mPresenter) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void showLoading(String msg) {

       LoadingDialog.showLoading(this, msg, true);
    }
    @Override
    public void hideLoading() {
        LoadingDialog.hideLoading();
    }

    /**
     * 界面开始动画 (此处输入方法执行任务.)
     */
    protected void startAnimation() {
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    /**
     * 界面回退动画 (此处输入方法执行任务.)
     */
    protected void endAnimation() {// 开始动画
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }




    @Override
    public void finish() {// 设置回退动画
        super.finish();
    }

    /**
     * 返回
     */
    public void back() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }




    @Override
    public void showMessage(@NonNull String message) {
        ArmsUtils.makeText(message);
    }

    @Override
    public void onBackPressed() {
        HideUtil.hideSoftKeyboard(getRxContext());
        super.onBackPressed();
        endAnimation();
        back();
    }

    @Override
    public RxAppCompatActivity getRxContext() {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateView(BaseMessage message){
        if (EmptyUtils.isNotEmpty(message)) {
            LogUtil.i(message.obj.toString());
        }
    }
}

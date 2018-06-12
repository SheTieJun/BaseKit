package me.shetj.base.base;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;


/**
 *
 * @author shetj
 * @date 16/4/22
 */
@Keep
public interface IView {

    /**
     * 显示加载
     * @param msg 展示的信息
     */
    void showLoading(String msg);

    /**
     * 隐藏加载
     */
    void hideLoading();

    /**
     * 显示信息
     * @param message toast信息
     */
    void showMessage(@NonNull String message);
    /**
     * 返回当前的activity
     * @return RxAppCompatActivity
     */
    RxAppCompatActivity getRxContext();

    /**
     * (唯一更新view的方法)
     * 在每次接到信息时,把信息显示view上,---更新view
     * @param message 接收到的信息
     */
    void updateView(BaseMessage message);

}

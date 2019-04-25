package me.shetj.base.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;


import java.util.concurrent.TimeUnit;

import androidx.annotation.ColorInt;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import me.shetj.base.R;

/**
 * 消息提示框
 */
public class TipPopupWindow extends PopupWindow {
    public enum Tip{
        DEFAULT,
        INFO,
        ERROR,
        SUCCESS,
        WARNING
    }
    @ColorInt
    private static int ERROR_COLOR = Color.parseColor("#ff0000");
    @ColorInt
    private static int INFO_COLOR = Color.parseColor("#1CD67C");
    @ColorInt
    private static int SUCCESS_COLOR = Color.parseColor("#FFFF5A31");
    @ColorInt
    private static int WARNING_COLOR = Color.parseColor("#FFBB22");
    @ColorInt
    private static int NORMAL_COLOR = Color.parseColor("#CCCCCC");

    private static TipPopupWindow tipPopupWindow;
    private Context context;
    private TextView tvTip;
    private PublishSubject<TipPopupWindow> publishSubject;

    public TipPopupWindow(Context context) {
        super(context);
        this.context = context;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setAnimationStyle(R.style.tip_pop_anim_style);
        // 设置点击窗口外边窗口消失
        setOutsideTouchable(false);
        setFocusable(false);
        // 加载布局
        initUI();
    }

    private void initUI( ) {
        View rootView = View.inflate(context, R.layout.base_popupwindow_tip, null);
        tvTip = rootView.findViewById(R.id.tv_tip);
        setContentView(rootView);

        publishSubject = PublishSubject.create();

        publishSubject
                .debounce(1000, TimeUnit.MILLISECONDS)
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TipPopupWindow>() {
                    @Override
                    public void accept(TipPopupWindow tipPopupWindow) throws Exception {
                        if (tipPopupWindow != null){
                            tipPopupWindow.dismiss();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        TipPopupWindow.tipDismiss();
                    }
                });
    }


    /**
     * 展示
     * @param tip
     * @param tipMsg
     */
    public void showTip(Tip tip , View view, String tipMsg){
        //设置背景
        switch (tip){
            case INFO:
                tvTip.setBackground(new ColorDrawable(INFO_COLOR));
                break;
            case ERROR:
                tvTip.setBackground(new ColorDrawable(ERROR_COLOR));
                break;
            case DEFAULT:
                tvTip.setBackground(new ColorDrawable(NORMAL_COLOR));
                break;
            case SUCCESS:
                tvTip.setBackground(new ColorDrawable(SUCCESS_COLOR));
                break;
            case WARNING:
                tvTip.setBackground(new ColorDrawable(WARNING_COLOR));
                break;
            default:
                break;
        }
        //设置文子
        tvTip.setText(tipMsg);
        showAsDropDown(view);
        publishSubject.onNext(this);
    }


    /**
     * 展示信息
     */
    public static void showTipMsg(Context context, Tip tip, View view, String tipMsg){
        if (tipPopupWindow != null && tipPopupWindow.isShowing()){
            tipPopupWindow.showTip(tip, view, tipMsg);
        }else {
            tipPopupWindow = new TipPopupWindow(context);
            tipPopupWindow.showTip(tip, view, tipMsg);
        }
    }

    /**
     * 在展示的{@link android.app.Activity#onDestroy()} 中调用
     */
    public static void tipDismiss(){
        if (tipPopupWindow != null){
            tipPopupWindow.dismiss();
            tipPopupWindow = null;
        }
    }
}

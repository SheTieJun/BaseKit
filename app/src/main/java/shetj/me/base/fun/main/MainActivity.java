package shetj.me.base.fun.main;

import android.os.Bundle;

import me.shetj.base.base.BaseActivity;
import shetj.me.base.R;

public class MainActivity extends BaseActivity<MainPresenter> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}

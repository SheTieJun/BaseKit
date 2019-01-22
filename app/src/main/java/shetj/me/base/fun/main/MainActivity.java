package shetj.me.base.fun.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import me.shetj.base.base.BaseSwipeBackActivity;
import shetj.me.base.R;

public class MainActivity extends BaseSwipeBackActivity<MainPresenter> implements View.OnClickListener {


	/**
	 * 测试_swipe
	 */
	private Button mBtnTest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}


	@Override
	protected void initView() {

		mBtnTest = (Button) findViewById(R.id.btn_test);
		mBtnTest.setOnClickListener(this);
	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			default:
				break;
			case R.id.btn_test:
				showLoading("测试哦");
				break;
		}
	}
}

package shetj.me.base.fun.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.shetj.base.base.BaseSwipeBackActivity;
import me.shetj.base.tools.time.CodeUtil;
import shetj.me.base.R;

public class MainActivity extends BaseSwipeBackActivity<MainPresenter> implements View.OnClickListener {


	/**
	 * 测试_swipe
	 */
	private Button mBtnTest;
	/**
	 * 测试codeutils
	 */
	private TextView mTvTestCode;
	private CodeUtil codeUtil;

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
		mTvTestCode = (TextView) findViewById(R.id.tv_test_code);
		mTvTestCode.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		codeUtil = new CodeUtil(mTvTestCode);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			default:
				break;
			case R.id.btn_test:
				showLoading("测试哦");
				break;
			case R.id.tv_test_code:
				codeUtil.start();
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		codeUtil.stop();
	}
}

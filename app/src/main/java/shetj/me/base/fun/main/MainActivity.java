package shetj.me.base.fun.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.viewpager2.widget.ViewPager2;

import me.shetj.base.base.BaseActivity;
import me.shetj.base.kt.ActivityExtKt;
import me.shetj.base.tools.app.ArmsUtils;
import me.shetj.base.tools.time.CodeUtil;
import shetj.me.base.R;
import timber.log.Timber;

public class MainActivity extends BaseActivity<MainPresenter> implements View.OnClickListener {


	/**
	 * 测试_swipe
	 */
	private Button mBtnTest;
	/**
	 * 测试codeutils
	 */
	private TextView mTvTestCode;
	private CodeUtil codeUtil;
	private ViewPager2 viewpage2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}


	@Override
	public void initView() {

		mBtnTest = (Button) findViewById(R.id.btn_test);
		mBtnTest.setOnClickListener(this);
		mTvTestCode = (TextView) findViewById(R.id.tv_test_code);
		mTvTestCode.setOnClickListener(this);
		viewpage2 = findViewById(R.id.viewPager2);
		viewpage2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
		viewpage2.setAdapter(new AFragmentStateAdapter(this));
		viewpage2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				super.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}

			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				super.onPageScrollStateChanged(state);
			}
		});

	}

	@Override
	public void initData() {
		codeUtil = new CodeUtil(mTvTestCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			default:
				break;
			case R.id.btn_test:
				startActivity(new Intent(this,KtTestActivity.class));
				break;
			case R.id.tv_test_code:
				codeUtil.start();
				break;
		}
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	public void onActivityStart() {
		Timber.i("onActivityStart");

	}

	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	public void onActivityStop() {
		Timber.i("onActivityStop");
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
	public void onActivityResume() {
		Timber.i("onActivityResume");
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
	public void onActivitypause() {
		Timber.i("onActivitypause");
	}

	@Override
	public void onActivityCreate() {
		super.onActivityCreate();
		Timber.i("onActivityCreate");
	}

	@Override
	public void onActivityDestroy() {
		super.onActivityDestroy();
		codeUtil.stop();
		Timber.i("onActivityDestroy");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}

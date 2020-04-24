package shetj.me.base;

import android.app.Application;
import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import androidx.multidex.MultiDex;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import me.shetj.base.kt.ActivityExtKt;
import me.shetj.base.kt.DataExtKt;
import me.shetj.base.network.RxHttp;
import me.shetj.base.s;
import me.shetj.base.tools.app.ArmsUtils;

/**
 * <b>@packageName：</b> com.ebu.master<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/2/26<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */

public class APP extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		s.init(this,BuildConfig.LOG_DEBUG);
		RxHttp.getInstance()
				.debug(true)
				.setBaseUrl("https://baidu.com");
	}


	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

}
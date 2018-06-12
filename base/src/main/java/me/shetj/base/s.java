package me.shetj.base;

import android.app.Application;
import android.support.annotation.Keep;

import com.bumptech.glide.request.target.ViewTarget;

import me.shetj.base.http.rxEasyHttp.EasyHttpUtils;
import me.shetj.base.http.xutils.XUtil;
import me.shetj.base.tools.app.Utils;

/**
 * <b>@packageName：</b> me.shetj.base<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/2/24<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */

@Keep
public final class s {
	/**
	 * 初始化
	 * @param application
	 * @param isDebug
	 * @param baseUrl
	 */
	public static void init(Application application, boolean isDebug,String baseUrl){
		XUtil.init(application,isDebug);
		EasyHttpUtils.init(application,isDebug,baseUrl);
		Utils.init(application);
		ViewTarget.setTagId(R.id.glide_tag);
	}
}

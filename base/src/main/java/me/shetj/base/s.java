package me.shetj.base;

import android.app.Application;
import android.support.annotation.Keep;

import com.bumptech.glide.request.target.ViewTarget;

import me.shetj.base.http.easyhttp.EasyHttpUtils;
import me.shetj.base.tools.app.TimberUtil;
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
	 * @param application 初始
	 * @param isDebug 是否是Debug
	 * @param baseUrl http的baseUrl
	 */
	public static void init(Application application, boolean isDebug,String baseUrl){
		EasyHttpUtils.init(application,isDebug,baseUrl);
		Utils.init(application);
		ViewTarget.setTagId(R.id.base_glide_tag);
		TimberUtil.setLogAuto(isDebug);
	}

	public static Application getApp(){
		return Utils.getApp();
	}


}

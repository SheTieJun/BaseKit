package me.shetj.base.base;

import android.content.Context;
import android.widget.ImageView;

/**
 * <b>@packageName：</b> me.shetj.base.base<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/9/4 0004<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b> 图片加载<br>
 */
public interface BaseImageLoader {
	/**
	 * 加载普通的图片
	 * @param context
	 * @param url
	 * @param view
	 */
	void disPlayImage(Context context, String url, ImageView view);

	/**
	 * 加载用户头像
	 * @param context
	 * @param url
	 * @param view
	 */
	void disPlayUserImage(Context context, String url, ImageView view);

	/**
	 * 预加载
	 * @param context
	 * @param url
	 * @param view
	 */
	void preLoad(Context context, String url, ImageView view);

}

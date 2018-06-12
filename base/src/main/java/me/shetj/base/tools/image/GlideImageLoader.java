package me.shetj.base.tools.image;

import android.content.Context;

import com.bumptech.glide.Glide;

import org.xutils.x;

/**
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/5/26<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b> glide <br>
 */
public class GlideImageLoader {

	public static void pauseRequests(Context context){
		Glide.with(context).pauseRequests();
	}

	public static void resumeRequests(Context context){
		Glide.with(context).resumeRequests();
	}

	/**
	 * 预下载
	 * @param url
	 */
	public static void previewLoad(final String url){
		x.task().run(new Runnable() {
			@Override
			public void run() {
				Glide.with(x.app().getApplicationContext())
								.load(url)
								.submit();
			}
		});
	}



}

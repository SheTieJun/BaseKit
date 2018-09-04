package me.shetj.base.tools.image;

import android.content.Context;
import android.support.annotation.Keep;

import com.bumptech.glide.Glide;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/5/26<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b> glide <br>
 */
@Keep
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
	public static void previewLoad(final Context context, final String url){
		Flowable.just(1).observeOn(Schedulers.newThread())
						.subscribe(new Consumer<Integer>() {
							@Override
							public void accept(Integer integer) throws Exception {
								Glide.with(context)
												.load(url)
												.submit();
							}
						});
	}


}

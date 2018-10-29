package shetj.me.base.common.other;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import me.shetj.base.base.ImageLoader;

/**
 * <b>@packageName：</b> me.shetj.base.base<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/10/29 0029<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b> 具体用法 {@https://muyangmin.github.io/glide-docs-cn/doc/transitions.html}<br>
 *   加载图片三步走，1,with; 2,load; 3 into;
 *   取消图片也是三步走，1,with; 2,load; 3 clear;
 */
public class GlideImageLoader implements ImageLoader {
	@Override
	public void load(@NonNull ImageView simpleView,@NonNull String url) {

	}

	@Override
	public void load(@NonNull ImageView simpleView,@NonNull String url,@NonNull boolean hasProgress) {

	}

	@Override
	public void loadProgressive(@NonNull ImageView mSimpleView, @NonNull String url) {

	}

	@Override
	public void loadGif(@NonNull ImageView simpleView,@NonNull String url,@NonNull boolean isAuto) {

	}

	@Override
	public void prefetchImage(final Context context,@NonNull final String url, boolean isDiskCacheOrBitmapCache) {
		Flowable.just(url)
						.isEmpty()
						.observeOn(Schedulers.io())
						.subscribe(it ->
										Glide.with(context)
										.load(it)
										.submit());
	}

	@Override
	public ImageView getSimpleView(Context context, String url) {
		ImageView imageView = new ImageView(context);
		load(imageView,url);
		return   imageView;
	}

	@Override
	public void clearMemCache() {

	}

	@Override
	public void clearCacheFiles() {

	}
}

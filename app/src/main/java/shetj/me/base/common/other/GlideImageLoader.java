package shetj.me.base.common.other;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Flowable;
import io.reactivex.SingleObserver;
import io.reactivex.schedulers.Schedulers;
import me.shetj.base.tools.app.Utils;
import shetj.me.base.view.LoadingDialog;
import shetj.me.base.utils.ImageLoader;

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
		GlideApp.with(simpleView.getContext())
						.load(url)
						.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
						.into(simpleView);
	}

	@Override
	public void load(@NonNull ImageView simpleView,@NonNull String url,@NonNull boolean hasProgress) {
			if (hasProgress){
				GlideApp.with(simpleView.getContext())
								.load(url)
								.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
								.into(new SimpleTarget<Drawable>() {
									@Override
									public void onStart() {
										super.onStart();
										LoadingDialog.showLoading((Activity) simpleView.getContext());
									}

									@Override
									public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
										simpleView.setImageDrawable(resource);
										LoadingDialog.hideLoading();
									}
								});
			}else {
				load(simpleView,url);
			}
	}

	/**
	 * 未写
	 * @param mSimpleView 容器
	 * @param url url地址
	 */
	@Override
	public void loadProgressive(@NonNull ImageView mSimpleView, @NonNull String url) {
		GlideApp.with(mSimpleView.getContext())
						.load(url)
						.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
						.into(mSimpleView);
	}

	@Override
	public void loadGif(@NonNull ImageView simpleView,@NonNull String url,@NonNull boolean isAuto) {
		GlideApp.with(simpleView.getContext())
						.load(url)
						.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
						.into(simpleView);
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
		Glide.get(Utils.Companion.getApp().getApplicationContext()).clearMemory();
	

	}

	@Override
	public void clearCacheFiles() {
		Flowable.just(1).observeOn(Schedulers.newThread())
						.subscribe(integer -> {
							//清理磁盘缓存 需要在子线程中执行
							Glide.get(Utils.Companion.getApp().getApplicationContext()).clearDiskCache();
						});
	}

	@Override
	public void displayImage(@NotNull Context context, @NotNull String url, @NotNull ImageView view) {

	}

	@Override
	public void displayUserImage(@NotNull Context context, @NotNull String url, @NotNull ImageView view) {

	}

	@Override
	public void preLoad(@NotNull Context context, @NotNull String url, @NotNull ImageView view) {

	}
}

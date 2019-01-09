package shetj.me.base.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.ielse.imagewatcher.ImageWatcher;
import com.github.ielse.imagewatcher.ImageWatcherHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>@packageName：</b> shetj.me.base.utils<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2019/1/9 0009<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */
public class ImageWatcherUtils {
	private ImageWatcherHelper iwHelper;

	public ImageWatcherUtils(Activity activity) {
		iwHelper =  ImageWatcherHelper.with(activity, new ImageWatcher.Loader() {
			@Override
			public void load(Context context, Uri uri, ImageWatcher.LoadCallback loadCallback) {
				Glide.with(context).load(uri)
								.into(new SimpleTarget<Drawable>() {
									@Override
									public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
										loadCallback.onResourceReady(resource);
									}
								});
			}
		});
	}

	public void showPic(ImageView imageView, List<String> dataList,int   position){
		SparseArray mapping = new   SparseArray<ImageView>();
		mapping.put(position, imageView);
		iwHelper.show(imageView, mapping, convert(dataList));
	}

	public List<Uri> convert(List<String>   data  ){
		ArrayList<Uri> list =  new  ArrayList<Uri>();
		for (String datum : data) {
			list.add(Uri.parse(datum));
		}
		return list;
	}

	public boolean onBackPressed() {
		return !iwHelper.handleBackPressed() ;
	}

}

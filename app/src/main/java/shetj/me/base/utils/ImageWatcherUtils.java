package shetj.me.base.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.SparseArray;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.ielse.imagewatcher.ImageWatcherHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
		iwHelper =  ImageWatcherHelper.with(activity, (context, uri, loadCallback) -> Glide.with(context).load(uri)
						.into(new SimpleTarget<Drawable>() {
							@Override
							public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
								loadCallback.onResourceReady(resource);
							}
						}));
	}

	public void showPic(ImageView imageView, String url ){
		List<String> dataList =new ArrayList<>();
		dataList.add(url);
		showPic(imageView,dataList,0);
	}

	public void showPic(ImageView imageView, List<String> dataList,int   position){
		SparseArray<ImageView> mapping = new SparseArray<>();
		mapping.put(position, imageView);
		iwHelper.show(imageView, mapping, convert(dataList));
	}

	public void showPic(ImageView imageView, List<String> dataList, SparseArray<ImageView> mapping){
		iwHelper.show(imageView, mapping, convert(dataList));
	}


	private List<Uri> convert(List<String> data){
		ArrayList<Uri> list = new ArrayList<>();
		for (String datum : data) {
			list.add(Uri.parse(datum));
		}
		return list;
	}

	public boolean onBackPressed() {
		return !iwHelper.handleBackPressed() ;
	}

}

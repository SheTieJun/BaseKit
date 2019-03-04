package me.shetj.base.tools.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;

import me.shetj.base.R;

/**
 * <b>@packageName：</b> shetj.me.base.utils<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/10/29 0029<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b> 为空时，展示的界面<br>
 */
@SuppressLint("InflateParams")
public class EmptyViewUtils {


	public static View getLoadingView(Activity context ) {
		return LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null);
	}

	public static View getLoadingView(Activity context,Drawable drawable ) {
		View emptyView = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null);
		SpinKitView spinKitView = emptyView.findViewById(R.id.spin_kit);
		spinKitView.setIndeterminateDrawable(drawable);
		return emptyView;
	}

	public static View getLoadingView(Activity context,Sprite sprite ) {
		View emptyView = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null);
		SpinKitView spinKitView = emptyView.findViewById(R.id.spin_kit);
		spinKitView.setIndeterminateDrawable(sprite);
		return emptyView;
	}

	public static View  getEmptyView(Activity context){
		View emptyView = LayoutInflater.from(context).inflate(R.layout.base_empty_date_view, null);
		TextView emptyTextView = emptyView.findViewById(R.id.tv_msg_foot);
		emptyTextView.setText("");
		return emptyView;
	}

	public static View  getEmptyView(Activity context, @NonNull String msg, @DrawableRes int image){
		View emptyView = LayoutInflater.from(context).inflate(R.layout.base_empty_date_view, null);
		TextView emptyTextView = emptyView.findViewById(R.id.tv_msg_foot);
		emptyTextView.setText(msg);
		ImageView emptyImage = emptyView.findViewById(R.id.iv_empty_view);
		emptyImage.setImageResource(image);
		return emptyView;
	}

	public static View  getEmptyView(Activity context, String msg ){
		View emptyView = LayoutInflater.from(context).inflate(R.layout.base_empty_date_view, null);
		TextView emptyTextView = emptyView.findViewById(R.id.tv_msg_foot);
		emptyTextView.setText(msg);
		return emptyView;
	}
}

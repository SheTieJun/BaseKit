package shetj.me.base.utils;

import android.app.Activity;
import androidx.annotation.Keep;
import android.view.ViewGroup;

import com.irozon.sneaker.Sneaker;
import com.irozon.sneaker.interfaces.OnSneakerClickListener;
import com.irozon.sneaker.interfaces.OnSneakerDismissListener;

import shetj.me.base.R;

/**
 * <b>@packageName：</b> me.shetj.base.tools.app<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/2/28<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b> 顶部信息提示<br>
 */

@Keep
public class SneakerUtils {

	public static  void sneakError(Activity activity, String title, String message){
		Sneaker.with(activity)
						.setTitle(title)
						.setMessage(message)
						.sneakError();
	}

	public static  void sneakSuccess(Activity activity, String title, String message){
		Sneaker.with(activity)
						.setTitle(title)
						.setMessage(message)
						.sneakSuccess();
	}

	public static  void sneakWarning(Activity activity, String title, String message){
		Sneaker.with(activity)
						.setTitle(title)
						.setMessage(message)
						.sneakWarning();
	}
	public static  void sneakCus(Activity activity,
	                             String title,
	                             String message,
	                             int color,
	                             int bgColor,
	                             OnSneakerClickListener listener,
	                             OnSneakerDismissListener dismissListener){
		Sneaker.with(activity)
						.setTitle(title, color)
						// Title and title color
						.setMessage(message, color)
						// Message and message color
						.setDuration(4000)
						// Time duration to show
						.autoHide(true)
						// Auto hide Sneaker view
						.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
						// Height of the Sneaker layout
						.setIcon(R.drawable.ic_error,  R.color.white, false)
						// Icon, icon tint color and circular icon view
						.setOnSneakerClickListener(listener)
						// Click listener for Sneaker
						.setOnSneakerDismissListener(dismissListener)
						// Dismiss listener for Sneaker. - Version 1.0.2
						.sneak(bgColor);
						// Sneak with background color
	}
}

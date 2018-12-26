package me.shetj.base.tools.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.shetj.base.R;
import me.shetj.base.tools.file.SPUtils;
import me.shetj.base.tools.time.TimeUtil;

@Keep
public class MobileInfoUtils {


	private static Method mSetStopAutoStart = null;
	private static Method mgetStopAutoStart = null;

	/**
	 * Get Mobile Type
	 *
	 * @return
	 */
	private static String getMobileType() {
		return Build.MANUFACTURER;
	}

	/**
	 * GoTo Open Self Setting Layout
	 * Compatible Mainstream Models 兼容市面主流机型
	 *
	 * @param context 上下文
	 */
	private static void jumpStartInterface(Context context) {
		Intent intent = new Intent();
		try {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.e("HLQ_Struggle", "******************当前手机型号为：" + getMobileType());
			ComponentName componentName = null;
			String brand = android.os.Build.BRAND;
			switch (brand.toLowerCase()) {
				case "samsung":
					componentName = new ComponentName("com.samsung.android.sm",
									"com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
					break;
				case "huawei":
					componentName = new ComponentName("com.huawei.systemmanager",
									"com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
					break;
				case "xiaomi":
					componentName = new ComponentName("com.miui.securitycenter",
									"com.miui.permcenter.autostart.AutoStartManagementActivity");
					break;
				case "vivo":
					componentName = new ComponentName("com.iqoo.secure",
									"com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
					break;
				case "oppo":
					componentName = new ComponentName("com.coloros.oppoguardelf",
									"com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
					break;
				case "360":
					componentName = new ComponentName("com.yulong.android.coolsafe",
									"com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity");
					break;
				case "meizu":
					componentName = new ComponentName("com.meizu.safe",
									"com.meizu.safe.permission.SmartBGActivity");
					break;
				case "oneplus":
					componentName = new ComponentName("com.oneplus.security",
									"com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");
					break;
				default:
					break;
			}
			if (componentName != null) {
				intent.setComponent(componentName);
			} else {
				intent.setAction(Settings.ACTION_SETTINGS);
			}
			intent.setComponent(componentName);
			context.startActivity(intent);
		} catch (Exception e) {//抛出异常就直接打开设置页面
			intent = new Intent(Settings.ACTION_SETTINGS);
			context.startActivity(intent);
		}
	}


	public static void jumpStartInterface(final Activity activity,boolean isSelf) {
		if (isOpenAuto(activity)) {
			//一天提醒一次
			SPUtils.put(activity,"AutoStart"+TimeUtil.getYMDime(),false);
			try {
				MaterialDialog materialdialog = new MaterialDialog.Builder(activity)
								.title("推送管理")
								.titleColorRes(R.color.colorPrimary)
								.content("由于安卓系统设置，为获取最新的信息推送，请手动开启自启动权限！")
								.positiveText("立即设置")
								.positiveColorRes(R.color.colorPrimary)
								.neutralColorRes(R.color.blackHintText)
								.onPositive((dialog, which) -> {
									SPUtils.put(activity,"AutoStart",false);
									if (isSelf){
										toSelfSetting(activity);
									}else {
										jumpStartInterface((Context) activity);
									}
									dialog.dismiss();
								})
								.onNeutral((dialog, which) -> {
									dialog.dismiss();
									SPUtils.put(activity,"AutoStart"+AppUtils.getAppVersionCode(),false);
								})
								.neutralText("暂不设置").build();
				materialdialog.show();
			} catch (Exception ignored) {
			}
		}

	}

	/**
	 * 是否开启提醒
	 *
	 * 默认一天打开一次
	 * 如果点击暂不设置，一个版本提醒一次
	 * 如果点击设置，则默认打开了自启动，以后不再提醒
	 * @param activity
	 * @return
	 */
	private static  boolean isOpenAuto(Activity activity){
		return (boolean) SPUtils.get(activity,"AutoStart",true) &&
						//根据版本来，这个版本不提醒
						(boolean)SPUtils.get(activity,"AutoStart"+AppUtils.getAppVersionCode(),true)
						&& (boolean)SPUtils.get(activity,"AutoStart"+TimeUtil.getYMDime(),true);
	}


	//需要root
	public static void fobidAutoRun(Context context, String pkg, boolean isFobid) {

		if (isForceStopAutoStartMethodExist(context)) {
			try {
				ActivityManager am = (ActivityManager) context
								.getSystemService(Context.ACTIVITY_SERVICE);
				mSetStopAutoStart.invoke(am, pkg,
								isFobid);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	public static boolean isForceStopAutoStartMethodExist(Context context) {
		synchronized (MobileInfoUtils.class) {
			if (mSetStopAutoStart == null) {
				try {
					ActivityManager am = (ActivityManager) context
									.getSystemService(Context.ACTIVITY_SERVICE);
					mSetStopAutoStart = am.getClass().getMethod(
									"setForbiddenAutorunPackages", String.class,
									boolean.class);
					mgetStopAutoStart = am.getClass()
									.getMethod("getForbiddenAutorunPackages");
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
			if (mSetStopAutoStart == null || mgetStopAutoStart == null) {
				return false;
			} else {
				return true;
			}
		}
	}

	public static void toSelfSetting(Context context) {
		Intent mIntent = new Intent();
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
		context.startActivity(mIntent);
	}
}

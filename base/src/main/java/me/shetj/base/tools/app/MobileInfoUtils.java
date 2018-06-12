package me.shetj.base.tools.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import me.shetj.base.tools.file.SPUtils;

@Keep
public class MobileInfoUtils {

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
            switch (getMobileType()) {
                case "Xiaomi":
                	// 红米Note4测试通过
                    componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
                    break;
                case "samsung":
                	// 三星Note5测试通过
                    componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.ram.AutoRunActivity");
                    break;
                case "HUAWEI":
                	// 华为测试通过
                    componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
                    break;
                case "vivo":
                	// VIVO测试通过
                    componentName = ComponentName.unflattenFromString("com.iqoo.secure/.ui.phoneoptimize.AddWhiteListActivity");
                    break;
                case "Meizu":
                	//万恶的魅族
                    // 通过测试，发现魅族是真恶心，也是够了，之前版本还能查看到关于设置自启动这一界面，系统更新之后，完全找不到了，心里默默Fuck！
                    // 针对魅族，我们只能通过魅族内置手机管家去设置自启动，所以我在这里直接跳转到魅族内置手机管家界面，具体结果请看图
                    componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity");
                    break;
                case "OPPO":
                	// OPPO R8205测试通过
                    componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
                    Intent intentOppo = new Intent();
                    intentOppo.setClassName("com.oppo.safe/.permission.startup", "StartupAppListActivity");
                    if (context.getPackageManager().resolveActivity(intentOppo, 0) == null) {
                        componentName = ComponentName.unflattenFromString("com.coloros.safecenter/.startupapp.StartupAppListActivity");
                    }
                    break;
                case "yulong":
                	// 360手机 未测试
                    componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity");
                    break;
                default:
                    // 以上只是市面上主流机型，由于公司你懂的，所以很不容易才凑齐以上设备
                    // 针对于其他设备，我们只能调整当前系统app查看详情界面
                    // 在此根据用户手机当前版本跳转系统设置界面
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                    break;
            }
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {//抛出异常就直接打开设置页面
            intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }

    public static void jumpStartInterface(final Activity activity) {
        if (isOpenAuto(activity)) {
	        try {
		        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		        builder.setMessage("\n由于安卓系统设置，为获取最新的消息推送，请手动开启自启动权限！");
		        builder.setPositiveButton("立即设置",
						        new DialogInterface.OnClickListener() {
							        @Override
							        public void onClick(DialogInterface dialog, int which) {
								        SPUtils.put(activity, "AutoStart", false);
								        jumpStartInterface((Context) activity);
							        }
						        });
		        builder.setNegativeButton("暂不设置",
						        new DialogInterface.OnClickListener() {
							        @Override
							        public void onClick(DialogInterface dialog, int which) {
								        SPUtils.put(activity, "AutoStart", false);
								        dialog.dismiss();
							        }
						        });
		        builder.setCancelable(false);
		        builder.create().show();
	        } catch (Exception ignored) {
	        }
        }

    }

    private static  boolean isOpenAuto(Activity activity){
        return (boolean) SPUtils.get(activity,"AutoStart",true);
    }
}

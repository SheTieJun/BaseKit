package me.shetj.base.tools.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * @author shetj
 */
@Keep
public class ThirdViewUtil {
    private static int USE_AUTOLAYOUT = -1;
    //0 说明 AndroidManifest 里面没有使用 AutoLayout 的Meta,即不使用 AutoLayout,1 为有 Meta ,即需要使用

    public static String LAYOUT_LINEARLAYOUT = "LinearLayout";
    public static String LAYOUT_FRAMELAYOUT = "FrameLayout";
    public static String LAYOUT_RELATIVELAYOUT = "RelativeLayout";

    private ThirdViewUtil() {
        throw new IllegalStateException("you can't instantiate me!");
    }


    @Nullable
    public static View convertAutoView(String name, Context context, AttributeSet attrs) {
        //本框架并不强制你使用 AutoLayout
        //如果你不想使用 AutoLayout ,就不要在 AndroidManifest 中声明, AutoLayout 的 Meta属性(design_width,design_height)
        if (USE_AUTOLAYOUT == -1) {
            USE_AUTOLAYOUT = 1;
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = packageManager.getApplicationInfo(context
                        .getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo == null || applicationInfo.metaData == null
                        || !applicationInfo.metaData.containsKey("design_width")
                        || !applicationInfo.metaData.containsKey("design_height")) {
                    USE_AUTOLAYOUT = 0;
                }
            } catch (PackageManager.NameNotFoundException e) {
                USE_AUTOLAYOUT = 0;
            }
        }

        if (USE_AUTOLAYOUT == 0) {
            return null;
        }

        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT)) {
            view = new AutoFrameLayout(context, attrs);
        } else if (name.equals(LAYOUT_LINEARLAYOUT)) {
            view = new AutoLinearLayout(context, attrs);
        } else if (name.equals(LAYOUT_RELATIVELAYOUT)) {
            view = new AutoRelativeLayout(context, attrs);
        }
        return view;
    }
}
package shetj.me.base.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2023/12/14<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b>  <br>
 */

class AppUtils {
   /**
    * 是否是平板
    *
    * @param context 上下文
    * @return 是平板则返回true，反之返回false
    */
   public static boolean isPad(Context context) {
      boolean isPad = (context.getResources().getConfiguration().screenLayout
              & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
      DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
      double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
      double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
      double screenInches = Math.sqrt(x + y); // 屏幕尺寸
      return isPad || screenInches >= 7.0;
   }
}

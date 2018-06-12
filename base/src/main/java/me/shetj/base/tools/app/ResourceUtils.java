package me.shetj.base.tools.app;

import android.content.Context;
import android.support.annotation.Keep;

/**
 * @author shetj
 */
@Keep
public class ResourceUtils {

    public static int getIdByName(Context context, String className, String resName) {
        context = context.getApplicationContext();
        String packageName = context.getPackageName();
        return context.getResources().getIdentifier(resName, className, packageName);
    }
}

/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.shetj.base.tools.qmui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

import java.lang.reflect.Method;

import timber.log.Timber;

public class QMUINotchHelper {

    private static final String TAG = "QMUINotchHelper";

    private static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;
    private static final String MIUI_NOTCH = "ro.miui.notch";
    private static Boolean sHasNotch = null;

    public static boolean hasNotchInVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class ftFeature = cl.loadClass("android.util.FtFeature");
            Method[] methods = ftFeature.getDeclaredMethods();
            if (methods != null) {
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (method.getName().equalsIgnoreCase("isFeatureSupport")) {
                        ret = (boolean) method.invoke(ftFeature, NOTCH_IN_SCREEN_VOIO);
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            Timber.i("hasNotchInVivo ClassNotFoundException");
        } catch (Exception e) {
            Timber.e("hasNotchInVivo Exception");
        }
        return ret;
    }


    public static boolean hasNotchInHuawei(Context context) {
        boolean hasNotch = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            hasNotch = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Timber.i("hasNotchInHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Timber.e("hasNotchInHuawei NoSuchMethodException");
        } catch (Exception e) {
            Timber.e("hasNotchInHuawei Exception");
        }
        return hasNotch;
    }

    public static boolean hasNotchInOppo(Context context) {
        return context.getPackageManager()
                .hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    @SuppressLint("PrivateApi")
    public static boolean hasNotchInXiaomi(Context context) {
        try {
            Class spClass = Class.forName("android.os.SystemProperties");
            Method getMethod = spClass.getDeclaredMethod("getInt", String.class, int.class);
            getMethod.setAccessible(true);
            int hasNotch = (int) getMethod.invoke(null, MIUI_NOTCH, 0);
            return hasNotch == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasNotch(View view){
        if (sHasNotch == null) {
            if(isNotchOfficialSupport()){
                if(!attachHasOfficialNotch(view)){
                    return false;
                }
            }else {
                sHasNotch = has3rdNotch(view.getContext());
            }
        }
        return sHasNotch;
    }


    public static boolean hasNotch(Activity activity) {
        if (sHasNotch == null) {
            if(isNotchOfficialSupport()){
                Window window = activity.getWindow();
                if(window == null){
                    return false;
                }
                View decorView = window.getDecorView();
                if(decorView == null){
                    return false;
                }
                if(!attachHasOfficialNotch(decorView)){
                    return false;
                }
            }else {
                sHasNotch = has3rdNotch(activity);
            }
        }
        return sHasNotch;
    }

    /**
     *
     * @param view
     * @return false indicates the failure to get the result
     */
    @TargetApi(28)
    private static boolean attachHasOfficialNotch(View view){
        WindowInsets windowInsets = view.getRootWindowInsets();
        if(windowInsets != null){
            DisplayCutout displayCutout = windowInsets.getDisplayCutout();
            sHasNotch = displayCutout != null;
            return true;
        }else{
            // view not attached, do nothing
            return false;
        }
    }

    public static boolean has3rdNotch(Context context){
        if (QMUIDeviceHelper.isHuawei()) {
            return hasNotchInHuawei(context);
        } else if (QMUIDeviceHelper.isVivo()) {
            return hasNotchInVivo(context);
        } else if (QMUIDeviceHelper.isOppo()) {
            return hasNotchInOppo(context);
        } else if (QMUIDeviceHelper.isXiaomi()) {
            return hasNotchInXiaomi(context);
        }
        return false;
    }

    public static boolean isNotchOfficialSupport(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /**
     * fitSystemWindows 对小米、vivo挖孔屏横屏挖孔区域无效
     * @return boolean
     */
    public static boolean needFixLandscapeNotchAreaFitSystemWindow(View view){
       return (QMUIDeviceHelper.isXiaomi() || QMUIDeviceHelper.isVivo()) && QMUINotchHelper.hasNotch(view);
    }

}

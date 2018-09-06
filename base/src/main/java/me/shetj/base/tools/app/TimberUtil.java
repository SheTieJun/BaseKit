package me.shetj.base.tools.app;

import android.os.Environment;
import android.util.Log;

import java.io.File;

import me.shetj.base.BuildConfig;
import timber.log.Timber;

public class TimberUtil {
 
    private final static String LOG_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "log.test";
 
    /**
     * 设置log自动在debug打开，在release关闭，可以在Application的onCreate中设置
     * @param isDebug
     */
    public static void setLogAuto(boolean isDebug){
        //打印关，同时gradle中的release的debuggable要设置为false
        if (isDebug) {
            Timber.plant(new Timber.DebugTree());
        } else {//release版本
            Timber.plant(new CrashReportingTree());
        }
    }
 
    /**
     * 设置log自动，并且想在release时仅在测试时有打印，
     * 在release版本时增加判断磁盘目录下是否存在文件 log.test，
     * 测试时让测试人员在磁盘目录下建立这么个文件。
     * 注意，如果读取存储需要权限申请的话，需要先获得权限，才能调用
     */
    public static void setLogAutoEx(boolean isDebug){
        if (isDebug) {//debug版本
            Timber.plant(new Timber.DebugTree());
        } else {//release版本
            File logFile = new File(LOG_FILE_PATH);
            if(logFile.exists()){
                //打印开
                Timber.plant(new Timber.DebugTree());
            }else {
                //打印关，同时gradle中的release的debuggable要设置为false
                Timber.plant(new CrashReportingTree());
            }
        }
    }
 
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            if (t != null) {
                if (priority == Log.ERROR) {
                    //FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    // FakeCrashLibrary.logWarning(t);
                } else {
 
                }
            }
        }
    }
 
}
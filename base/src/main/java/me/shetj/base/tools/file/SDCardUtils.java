package me.shetj.base.tools.file;

import android.os.Environment;
import androidx.annotation.Keep;
import me.shetj.base.tools.app.Utils;

import java.io.File;

/**
 * SD卡相关的辅助类
 * @author shetj
 */
@Keep
public class SDCardUtils
{
	private SDCardUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}
	/**
	 *
	 * @param packagePath 包的路径
	 * @return
	 */
	public static String getPath(String packagePath){
		StringBuilder path = new StringBuilder(getFilesDir());
		String  f[]=packagePath.split("/");
		for (String aF : f) {
			File dirFile = new File(path + "/" + aF);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
			path.append("/").append(aF);
		}
		return path.toString();
	}

	/**
	 * 判断SDCard是否可用
	 *
	 * @return
	 */
	public static boolean isSDCardEnable()
	{
		return Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED);

	}

	/**
	 * 获取SD卡路径
	 *
	 * @return  mnt/sdcard/
	 */
	public static String getSDCardPath()
	{
		if (isSDCardEnable()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			throw new RuntimeException("sdcard is unmounted");
		}
	}


	/**
	 * 获取系统存储路径
	 *
	 * @return     /data/data/包名/cache/
	 */
	public static String getRootDirectoryPath()
	{
		return Environment.getRootDirectory().getAbsolutePath() ;
	}


	/**
	 * data/data/< package name >/files/
	 */
	public static String getFilesDir(){
		return Utils.getApp().getFilesDir().getAbsolutePath();
	}


	/**
	 * mnt/sdcard/Android/data/< package name >/files/type
	 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
	 */
	public static String getExternalFilesDir(String type){
		return Utils.getApp().getExternalFilesDir(type).getAbsolutePath();
	}

	/**
	 * data/data/packagename/cache
	 * @return
	 */
	public static String getCache(){
		return Utils.getApp().getCacheDir().getAbsolutePath();
	}


}

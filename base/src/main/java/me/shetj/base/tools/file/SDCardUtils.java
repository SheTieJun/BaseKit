package me.shetj.base.tools.file;

import android.os.Environment;

import java.io.File;

//SD卡相关的辅助类
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
		StringBuilder path;
		if (isSDCardEnable()){
			path = new StringBuilder(getSDCardPath());
		}else {
			path = new StringBuilder(getRootDirectoryPath());
		}
		//如何路径下的文件（夹）不存在，就创建
		//可能是多层次的 要创建多次
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
	 * @return   /storage/emulated/0/DESIGN_TEST/
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


}

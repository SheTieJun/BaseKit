package me.shetj.base.base;

/**
 * <b>@packageName：</b> com.aycm.dsy.function.combo<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/3/7<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */

public interface CommonCaseCallBack {

	/**
	 * 成功
	 * @param key 成功
	 */
	void onSuccess(String key);

	/**
	 * 结束
	 */
	void onClose();
}

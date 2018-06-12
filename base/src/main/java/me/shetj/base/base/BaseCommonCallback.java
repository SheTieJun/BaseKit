package me.shetj.base.base;

/**
 * <b>@packageName：</b> com.aycm.dsy.common<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/3/8<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */

public interface BaseCommonCallback<T> {
	/**
	 * 成功
	 */
	void onSuccess();

	/**
	 * 成功带有结果
	 * @param result 成功结果
	 */
	void onSuccess(T result);

	/**
	 * 失败
	 */
	void onFail();

	/**
	 * 失败,并且带上失败信息
	 * @param result 失败结果
	 */
	void onFail(T result);
}

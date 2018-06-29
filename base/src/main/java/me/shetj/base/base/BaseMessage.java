package me.shetj.base.base;

import android.support.annotation.Keep;

/**
 * <b>@packageName：</b> me.shetj.base.base<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/3/28<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */
@Keep
public class BaseMessage <T>{
	public int type;
	public int position;
	public T obj;
	public String msg;
}

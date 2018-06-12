package me.shetj.base.http.rxEasyHttp;

import android.support.annotation.Keep;

import com.zhouyou.http.interceptor.BaseDynamicInterceptor;

import java.util.TreeMap;


/**
 */
@Keep
public class CustomSignInterceptor extends BaseDynamicInterceptor<CustomSignInterceptor> {

	@Override
	public boolean isAccessToken() {
		return true;
	}

	@Override
	public boolean isTimeStamp() {
		return true;
	}

	@Override
	public TreeMap<String, String> dynamic(TreeMap<String, String> dynamicMap) {
		//dynamicMap:是原有的全局参数+局部参数
		if (isTimeStamp()) {
			//是否添加时间戳，因为你的字段key可能不是timestamp,这种动态的自己处理
			dynamicMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
		}
		if (isAccessToken()) {
			//是否添加token
		}
		if (isSign()) {
			//是否签名,因为你的字段key可能不是sign，这种动态的自己处理
		}
		//dynamicMap:是原有的全局参数+局部参数+新增的动态参数
		return dynamicMap;
	}

}

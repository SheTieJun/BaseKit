package me.shetj.base.base;

import android.support.annotation.Keep;

import com.zhouyou.http.model.HttpParams;

import java.util.Map;

import io.reactivex.annotations.NonNull;
import me.shetj.base.http.easyhttp.EasyHttpUtils;

/**
 * <b>@packageName：</b> me.shetj.base.base<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/2/28<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */

@Keep
public abstract class BaseModel implements IModel {

	@Override
	@NonNull
	public BaseMessage getMessage(int code ,Object obj) {
		BaseMessage message = new BaseMessage();
		message.obj = obj;
		message.type = code;
		return message;
	}

	@NonNull
	public HttpParams getParamsFromMap(@NonNull Map<String,String > map ){
		return EasyHttpUtils.getParamsFromMap(map);
	}

	@Override
	public void onDestroy() {

	}
}

package me.shetj.base.base;

import android.support.annotation.Keep;

import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.model.HttpParams;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import me.shetj.base.http.rxEasyHttp.EasyHttpUtils;

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


	public Observable<String> doPost(String url, Map<String, String> map){

		return	EasyHttp.post(url)
						.params(getParamsFromMap(map))
						.execute(String.class);
	}



	public Observable<String> doGet(String url, Map<String, String> map){

		return	EasyHttp.get(url)
						.params(getParamsFromMap(map))
						.execute(String.class);
	}

	public void doPost(String url, Map<String, String> map, SimpleCallBack callBack){
		EasyHttp.post(url)
						.params(getParamsFromMap(map))
						.execute(callBack);
	}
}

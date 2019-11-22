package shetj.me.base.common.manager;


import org.simple.eventbus.EventBus;

import me.shetj.base.s;
import me.shetj.base.tools.file.SPUtils;
import me.shetj.base.tools.json.EmptyUtils;

import static shetj.me.base.common.tag.SPKey.SAVE_TOKEN;


/**
 *
 * @author shetj
 * @date 2017/10/16
 */

public class TokenManager {


	private static TokenManager instance = null;
	private TokenManager() {
			EventBus.getDefault().register(this);
	}

	public static TokenManager getInstance() {
		if (instance == null) {
			synchronized (TokenManager.class) {
				if (instance == null) {
					instance = new TokenManager();
				}
			}
		}
		return instance;
	}


	public  String  getToken() {
			String token = (String) SPUtils.get(s.getApp().getApplicationContext(), SAVE_TOKEN, "");
			if (EmptyUtils.Companion.isEmpty(token)){
				return "";
			}
			return token;

	}
	public  boolean isLogin(){
		String token= (String) SPUtils.get(s.getApp().getApplicationContext(), SAVE_TOKEN,"");
		return EmptyUtils.Companion.isNotEmpty(token);
	}


	public void setToken(String token) {
		SPUtils.put(s.getApp().getApplicationContext(), SAVE_TOKEN,token);
	}


}

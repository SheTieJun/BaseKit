package shetj.me.base.common;

import org.xutils.x;

import me.shetj.base.tools.file.SPUtils;
import me.shetj.base.tools.json.EmptyUtils;

import static shetj.me.base.configs.tag.SPKey.SAVE_TOKEN;


/**
 *
 * @author shetj
 * @date 2017/10/16
 */

public class TokenManager {


	private static TokenManager instance = null;
	private TokenManager() {
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
			String token = (String) SPUtils.get(x.app().getApplicationContext(), SAVE_TOKEN, "");
			if (EmptyUtils.isEmpty(token)){
				return "";
			}
			return token;

	}
	public  boolean isLogin(){
		String token= (String) SPUtils.get(x.app().getApplicationContext(), SAVE_TOKEN,"");
		return EmptyUtils.isNotEmpty(token);
	}


	public void setToken(String token) {
		SPUtils.put(x.app().getApplicationContext(), SAVE_TOKEN,token);
	}


}

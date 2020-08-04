package shetj.me.base.common.manager;


import me.shetj.base.S;
import me.shetj.base.tools.file.SPUtils;
import me.shetj.base.tools.json.EmptyUtils;



/**
 * @author shetj
 * @date 2017/10/16
 */

public class TokenManager {


    private static final String SAVE_TOKEN = "save_token";
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


    public String getToken() {
        String token = (String) SPUtils.get(S.getApp().getApplicationContext(), SAVE_TOKEN, "");
        if (EmptyUtils.Companion.isEmpty(token)) {
            return "";
        }
        return token;

    }

    public boolean isLogin() {
        String token = (String) SPUtils.get(S.getApp().getApplicationContext(), SAVE_TOKEN, "");
        return EmptyUtils.Companion.isNotEmpty(token);
    }


    public void setToken(String token) {
        SPUtils.put(S.getApp().getApplicationContext(), SAVE_TOKEN, token);
    }


}

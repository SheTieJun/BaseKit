package shetj.me.base.common.manager;

import android.content.Context;

import me.shetj.base.s;
import me.shetj.base.sim.SimpleCallBack;
import me.shetj.base.tools.file.SPUtils;
import me.shetj.base.tools.json.EmptyUtils;
import me.shetj.base.tools.json.GsonKit;
import shetj.me.base.common.bean.UserInfo;
import shetj.me.base.common.tag.SPKey;

/**
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/3/2<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b>用户管理<br>
 */

public class UserManager {
    private static final UserManager OUR_INSTANCE = new UserManager();
    private Context context = s.getApp().getApplicationContext();
    private UserInfo custom;

    public static UserManager getInstance() {
        return OUR_INSTANCE;
    }

    private UserManager() {
    }


    public boolean isLoginNow() {
        return TokenManager.getInstance().isLogin();
    }

    public boolean isLogin(Context context) {
        return true;
    }

    private void loginIn(Context context) {

    }

    public void loginOut() {
        custom = null;
        TokenManager.getInstance().setToken("");
        cleanUserInfo();
    }

    public UserInfo getUserInfo() {
        if (isLoginNow() && custom == null) {
            String userInfo = (String) SPUtils.Companion.get(context, SPKey.SAVE_USER, "");
            if (EmptyUtils.isNotEmpty(userInfo)) {
                custom = GsonKit.jsonToBean(userInfo, UserInfo.class);
            }
        }
        return custom;
    }

    public void saveUserInfo(String userInfo) {
        SPUtils.Companion.put(context, SPKey.SAVE_USER, userInfo);
    }

    public void saveUserInfo(UserInfo userInfo, SimpleCallBack commonCallback) {
        custom = userInfo;
        TokenManager.getInstance().setToken(custom.getToken());
        String toJson = GsonKit.objectToJson(userInfo);
        if (toJson != null) {
            SPUtils.Companion.put(context, SPKey.SAVE_USER, toJson);
        }

    }


    private void cleanUserInfo() {
        custom = null;
        TokenManager.getInstance().setToken("");
        SPUtils.Companion.put(context, SPKey.SAVE_USER, "");
    }


    public String getUid() {
        if (isLoginNow()) {
            return getUserInfo().getUid();
        } else {
            return "";
        }
    }

    public String getVip() {
        return getUserInfo().is_vip();
    }
}

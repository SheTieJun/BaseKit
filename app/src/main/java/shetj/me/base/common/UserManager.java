package shetj.me.base.common;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.qcshendeng.toyo.common.bean.UserInfo;

import me.shetj.base.base.SimBaseCallBack;
import me.shetj.base.s;
import me.shetj.base.tools.file.SPUtils;
import me.shetj.base.tools.json.EmptyUtils;
import me.shetj.base.tools.json.GsonKit;
import shetj.me.base.R;
import shetj.me.base.configs.tag.SPKey;

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

	public boolean isLogin(Context context){
		if (!isLoginNow()) {
			MaterialDialog materialdialog = new MaterialDialog.Builder(context)
							.title("登录提示")
							.content("您还没有登录,无法继续操作！")
							.positiveText("取消")
							.positiveColorRes(R.color.actionSheet_blue)
							.negativeColorRes(R.color.actionSheet_blue)
							.onPositive((dialog, which) -> dialog.dismiss())
							.onNegative((dialog, which) -> {
								loginIn(context);
							})
							.negativeText("确定").build();
			materialdialog.show();
			return false;
		}
		return true;
	}

	private void loginIn(Context context){

	}

	public void loginOut(){
		custom = null;
		TokenManager.getInstance().setToken("");
		cleanUserInfo();
	}

	public UserInfo getUserInfo(){
		if (isLoginNow() &&  custom == null ){
			String userInfo = (String) SPUtils.get(context, SPKey.SAVE_USER, "");
			if (EmptyUtils.isNotEmpty(userInfo)){
				custom = GsonKit.jsonToBean(userInfo,UserInfo.class);
			}
		}
		return custom;
	}

	public void saveUserInfo(String userInfo) {
		SPUtils.put(context, SPKey.SAVE_USER,userInfo);
	}

	public void saveUserInfo(UserInfo userInfo, SimBaseCallBack commonCallback) {
		custom = userInfo;
		TokenManager.getInstance().setToken(custom.getToken());
		SPUtils.put(context, SPKey.SAVE_USER,GsonKit.objectToJson(userInfo));
	}


	private void cleanUserInfo(){
		custom = null;
		TokenManager.getInstance().setToken("");
		SPUtils.put(context, SPKey.SAVE_USER,"");
	}


	public  String  getUid(){
		if (isLoginNow()) {
			return getUserInfo().getUid();
		}else {
			return "";
		}
	}

	public String getVip() {
		return getUserInfo().is_vip();
	}
}

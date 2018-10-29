package shetj.me.base.common.tag;

/**
 *
 * @author shetj
 * @date 2017/9/14
 */

public interface EventTags {
  /**
   * 登录
   */
	interface LoginState{
    String SEND_LOGIN = "send_login";
    String SEND_LOGOUT = "send_logout";
  }
  /**
   * 支付
   */
  interface PayState{
    String SUCCESS = "success";
    String FAIL = "fail";
  }
  /**
   * 刷新
   */
  interface Refresh{

  }
}

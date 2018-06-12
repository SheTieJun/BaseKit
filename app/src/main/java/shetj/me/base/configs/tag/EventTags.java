package shetj.me.base.configs.tag;

/**
 *
 * @author shetj
 * @date 2017/9/14
 */

public interface EventTags {

  String SEND_CHANGE_TEAM = "send_change_team";
  String SEND_TREAD = "send_trend";
	int TAB_SIZE = 3;

	interface LoginState{
    String SEND_LOGIN = "send_login";
    String SEND_LOGOUT = "send_logout";
    String ORDER_REFRESH = "order_refresh";
    String CUSTOM_REFRESH = "custom_refresh";
  }


  String SEND_POSITION = "send_position";
  String SEND_MAP = "send_map";

  /**
   * 1套餐2定制3艺人
   */
  interface OrderType{

    String COMBO = "1";
    String CUSTOM = "2";
    String ART = "3";
  }
  /**
   * 1套餐2定制3艺人
   */
  interface Custom{
    String COMBO = "1";
    String ART = "2";
  }
}

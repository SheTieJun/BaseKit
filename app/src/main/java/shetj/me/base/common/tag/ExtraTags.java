package shetj.me.base.common.tag;


/**
 * 内容
 * 用activity ，fragment 区分
 * 方便快速查找事件
 *
 * @author shetj
 */
public interface ExtraTags {

    interface Event {
        String EXTRA_EVENT_URL = "extra_event_url";
        String EXTRA_EVENT_ID = "extra_event_menu_id";
        String EXTRA_EVENT_TITLE = "extra_event_title";
        String EXTRA_EVENT_TYPE = "extra_event_type";
        String EXTRA_EVENT_INFO = "extra_event_info";
    }

}

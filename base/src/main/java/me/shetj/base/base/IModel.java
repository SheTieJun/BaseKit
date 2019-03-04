package me.shetj.base.base;

import androidx.annotation.Keep;

/**
 * @author shetj
 */
@Keep
public interface IModel {

    /**
     * 转化成message
     * @param code 类型
     * @param obj 数据
     * @return BaseMessage
     */
    BaseMessage getMessage(int code, Object obj);

    /**
     * 结束
     */
    void onDestroy();
}

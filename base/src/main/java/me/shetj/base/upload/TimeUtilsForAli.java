package me.shetj.base.upload;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2017/9/6.
 */

public class TimeUtilsForAli {
    /**
     * 传入Data类型日期，返回字符串类型时间（ISO8601标准时间）
     * @return
     */
    public static String getISO8601Timestamp(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String nowAsISO = df.format(new Date());
        return nowAsISO;
    }
}

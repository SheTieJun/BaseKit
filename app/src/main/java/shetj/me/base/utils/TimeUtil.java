package shetj.me.base.utils;

import androidx.annotation.Keep;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类名称：TimeUtil.java <br>
 *
 * @author shetj<br>
 */
@Keep
public class TimeUtil {

    /**
     * 得到时间戳
     *
     * @return
     */
    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static String getYMDHMSTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(new Date());
        return str;
    }

    public static String getHMSTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String str = format.format(new Date());
        return str;

    }


    public static String getYMDime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str = format.format(new Date());
        return str;

    }

    public static String formatTime(long ms) {

        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day;
        //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;
        //小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        //分钟
        String strSecond = second < 10 ? "0" + second : "" + second;
        //秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;
        //毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

        return strMinute + "：" + strSecond;
    }

    /**
     * 方法名：  formatFromNoformt	<br>
     * 方法描述：返回时间差时间<br>
     * 修改备注：<br>
     * 创建时间： 2016-4-18上午10:54:52<br>
     *
     * @param time
     * @return
     */
    public static String getTimeDelay(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String str = format.format(new Date());
            Date d1 = format.parse(str);
            Date d2 = format.parse(time);
            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            if (days == 0.0 && hours == 0.0 && minutes == 0.0) {
                return "刚刚 ";
            }
            if (days == 0.0 && hours == 0.0 && minutes > 0.0) {
                return minutes + "分钟前 ";
            }
            if (days == 0.0 && hours > 0.0) {
                return hours + "小时前 ";
            }
            if (1.0 <= days && days < 365) {
                return days + "天前 ";
            }
            if (days > 365) {
                return days + "年前 ";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return time;

    }

    /**
     * 返回日差
     *
     * @param time
     * @return
     */
    public static long getSentDays(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(TimeUtil.getYMDHMSTime());
            Date d2 = df.parse(time);
            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            return days;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }


    /**
     * 返回日差
     *
     * @param time the time
     * @return long long
     */
    public static long getTimeDiff(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date d1 = new Date();
            Date d2 = df.parse(time);
            long diff = d2.getTime() - d1.getTime();//这样得到的差值是微秒级别
            if (diff > 0) {
                return diff;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    /**
     * 传入Data类型日期，返回字符串类型时间（ISO8601标准时间）
     *
     * @return
     */
    public static String getISO8601Timestamp() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String nowAsISO = df.format(new Date());
        return nowAsISO;
    }
}

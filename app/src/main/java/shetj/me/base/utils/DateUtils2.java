package shetj.me.base.utils;

import androidx.annotation.Keep;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 主要是计算周
 */
@Keep
public class DateUtils2 {

    private static String mYear; // 当前年  
    private static String mMonth; // 月  
    private static String mDay;
    private static String mWay;


    /**
     * 获取当前日期几月几号 
     */
    public static String getDateString() {

        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份  
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码  
        return mMonth + "月" + mDay + "日";
    }

    /**
     * 获取当前年月日
     */
    public static String StringData() {

        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份  
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份  
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码  
        return mYear + "-" + mMonth + "-" + mDay;
    }

    /**
     * 获取当前年
     */
    public static String StringYear() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
        return  mYear ;
    }

    /**
     *获取一个月前的日期
     */
    public static String getMonthAfter(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, i);
        mMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);// 获取当前月份
        return  mMonth ;
    }
    /**
     *获取一个月前的日期
     * @return
     */
    public static String getYearAfter(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, i);
        mYear = String.valueOf(calendar.get(Calendar.YEAR));// 获取当前年份
        return  mYear ;
    }

    /**
     * 获取当前是周几 
     *
     */
    public static String getWeekString() {
        final Calendar c = Calendar.getInstance();
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        switch (mWay) {
            case "1":
                mWay = "周天";
                break;
            case "2":
                mWay = "周一";
                break;
            case "3":
                mWay = "周二";
                break;
            case "4":
                mWay = "周三";
                break;
            case "5":
                mWay = "周四";
                break;
            case "6":
                mWay = "周五";
                break;
            case "7":
                mWay = "周六";
                break;
        }
        return mDay;
    }

    /**
     * 根据当前日期获得是星期几 
     */
    public static String getWeek(String time) {
        String Week = "";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "周日";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "周一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += "周二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "周三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "周四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "周五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "周六";
        }
        return Week;
    }

    /**
     * 根据当前日期获得是星期几
     *
     */
    public static int getDayWeek(String time) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
           return 0;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
           return 1;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            return 2;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            return 3;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            return 4;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            return 5;
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return 6;
        }
        return 0;
    }

    /**
     * 获取今天往后一周的日期（年-月-日） */
    public static List<String> get7date() {
        List<String> dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        String date = sim.format(c.getTime());
        dates.add(date);
        for (int i = 0; i < 6; i++) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            date = sim.format(c.getTime());
            dates.add(date);
        }
        return dates;
    }
    /**
     * 获取今天往后一周的日期（几月几号）
     */

    public static  List<String> getSevenDate() {
        List<String > dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        for (int i = 0; i < 7; i++) {
            // 获取当前年份
            mYear = String.valueOf(c.get(Calendar.YEAR));

            // 获取当前月份
            mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);

            //获取当前日份的日期号码
            mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH)+i);


            String date =mMonth + "月" + mDay + "日";
            dates.add(date);
        }
        return dates;
    }
    public  static List<String> get7dateT() {
        List<String > dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        for (int i = 0; i < 7; i++) {
            mYear = String.valueOf(c.get(Calendar.YEAR));
            // 获取当前年份
            mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
            // 获取当前月份
            c.add(Calendar.DAY_OF_MONTH, 1);
            mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            // 获取当前日份的日期号码
            String date = mDay + "";
            dates.add(date);
        }
        return dates;
    }

    /**
     * 获取今天往后一周的集合
     */
    public static List<String > get7week(){
        String week="";
        List<String > weeksList = new ArrayList<String>();
        List<String> dateList = get7date();
        for(String s:dateList ){
            if (s.equals(StringData())) {
                week="今天";
            }else {
                week=getWeek(s);
            }
            weeksList.add(week);
        }
        return weeksList;
    }

    public static int getDate() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        // 获取当前月份的日期号码
        return Integer.parseInt(mDay);
    }

    public  static List<String> get7dateAndToday() {
        List<String > dates = new ArrayList<>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        for (int i = 0; i < 7; i++) {
            if (i != 0 ) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
            String date = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            dates.add(date);
        }
        return dates;
    }
}
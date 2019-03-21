package com.ludi.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author 陆迪
 * @date 2019/3/21 16:09
 **/
public class TimeUtil {

    /**
     * 获取时间 yyyy-MM-dd HH:mm:ss
     */
    public static String getDateTime() {
        return getDateTime(System.currentTimeMillis());
    }

    public static String getDateTime(Date date) {
        return date == null ? "" : getDateTime(date.getTime());
    }

    public static String getDateTime(long date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(date);
    }

    /**
     * 获取日期 yyyy-MM-dd
     */
    public static String getDate() {
        return getDate(System.currentTimeMillis());
    }

    public static String getDate(Date date) {
        return date == null ? "" : getDate(date.getTime());

    }

    public static String getDate(long date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(date);
    }

    public static Date getDate(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }

    }


    /**
     * 获取日期 yyyy-MM
     */
    public static String getMonthDate() {
        return getMonthDate(System.currentTimeMillis());
    }

    public static String getMonthDate(Date date) {
        return date == null ? "" : getMonthDate(date.getTime());

    }

    public static String getMonthDate(long date) {
        return new SimpleDateFormat("yyyy-MM", Locale.CHINA).format(date);
    }

    public static String getMonthDate(String dateStr) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = format.parse(dateStr);
        return getMonthDate(date);
    }

}

package com.jiangtou.server.util;

/**
 * @author 陆迪
 * @date 2019/3/21 15:50
 **/
public class StringUtil {


    /**
     * 获取string,为null则返回""
     */
    public static String toString(Object object) {
        return object == null ? "" : object.toString();
    }
    public static String toString(CharSequence cs) {
        return cs == null ? "" : cs.toString();
    }
    public static String toString(String s) {
        return s == null ? "" : s;
    }

    /**
     * 判断字符是否为空
     * @param trim 为true则去掉前后空格
     * @return 为空返回true
     */
    public static boolean isEmpty(String s, boolean trim) {
        if (s == null) {
            return true;
        }
        if (trim) {
            s = s.trim();
        }
        return s.length() <= 0;

    }
    public static boolean isEmpty(String s) {
        return isEmpty(s, true);
    }

    public static boolean isEmpty(Object object, boolean trim) {
        return isEmpty(toString(object), trim);
    }
    public static boolean isEmpty(CharSequence cs, boolean trim) {
        return isEmpty(toString(cs), trim);
    }

    /**判断字符是否非空
     * @param trim 为true则去掉前后空格
     * @return 为非空返回true
     */
    public static boolean isNotEmpty(String s, boolean trim) {
        return !isEmpty(s, trim);
    }
    public static boolean isNotEmpty(Object object, boolean trim) {
        return isNotEmpty(toString(object), trim);
    }
    public static boolean isNotEmpty(CharSequence cs, boolean trim) {
        return isNotEmpty(toString(cs), trim);
    }
    public static boolean isNotEmpty(String s) {
        return isNotEmpty(s, true);
    }

}

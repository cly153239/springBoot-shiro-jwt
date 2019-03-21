package com.ludi.server.config.datasource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 陆迪
 * @date 2019/3/21 15:32
 * 使用本地变量保存数据源key
 **/
public class DynamicDataSourceContextHolder {

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 数据源Key列表
     */
    public static List<Object> dataSourceKeys = new ArrayList<>();


    public static void setDataSource(String key) {
        CONTEXT_HOLDER.set(key);
    }

    public static String getDataSourceKey() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    public static boolean containDataSourceKey(String key) {
        return dataSourceKeys.contains(key);
    }

}

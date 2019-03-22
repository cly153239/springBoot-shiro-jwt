package com.jiangtou.server.config.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author 陆迪
 * @date 2019/3/21 15:34
 **/
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    /**
     * 获取当前线程数据源key
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceKey();
    }
}

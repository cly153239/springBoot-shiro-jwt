package com.jiangtou.server.config.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.github.pagehelper.PageInterceptor;
import com.jiangtou.server.enums.DataSourceEnum;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;

/**
 * @author 陆迪
 * @date 2018-10-03 11:50
 * 数据源配置
 **/
@Configuration
public class DruidDataSourceConfigurer {

    private final static Logger logger = LogManager.getLogger(DruidDataSourceConfigurer.class.getName());


    /**
     * 数据库
     */
    @Bean("app")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid.app")
    public DataSource app() {
        DataSource dataSource;
        dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }



    @Bean("dynamicDataSource")
    public DynamicRoutingDataSource dynamicDataSource() {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
        Map<Object, Object> dataSourceMap = new TreeMap<>();

        DataSource appDataSource = app();
        dataSourceMap.put(DataSourceEnum.app.sourceKey(), appDataSource);


        /*设置默认数据源*/
        dynamicRoutingDataSource.setDefaultTargetDataSource(appDataSource);
        /*设置所有数据源*/
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);
        /*添加所有数据源key*/
        DynamicDataSourceContextHolder.dataSourceKeys.addAll(dataSourceMap.keySet());
        return dynamicRoutingDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dynamicDataSource());
    }

    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dynamicDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dynamicDataSource);
        sessionFactory.setMapperLocations(resolveMapperLocations());
        sessionFactory.setPlugins(new Interceptor[]{pageInterceptor()});
        return sessionFactory.getObject();
    }


    /**
     * 配置分页插件
     * @return
     */
    private PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();

        Properties properties = new Properties();
        //参数查询支持
        properties.setProperty("supportMethodsArguments", "true");
        //多数据源支持
        properties.setProperty("autoRuntimeDialect", "true");

        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("pageSizeZero", "true");
        properties.setProperty("reasonable", "true");
        return pageInterceptor;
    }

    @Value("${mybatis.mapper-locations}")
    private String[] mapperLocations;
    /**
     * 读取mapper.xml文件
     * @return
     */
    private Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<>();
        if (this.mapperLocations != null) {
            for (String mapperLocation : this.mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                   logger.error("读取Mapper.xml配置出错");
                }
            }
        }
        return resources.toArray(new Resource[0]);
    }


}

package com.jiangtou.server.aop.annotation;

import org.springframework.core.annotation.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 陆迪
 * @date 2019/3/21 15:19
 * 多数据源key列表
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Order(0)
public @interface  DataSourceSwitch {

    String value();

}

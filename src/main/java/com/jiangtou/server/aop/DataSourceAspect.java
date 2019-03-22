package com.jiangtou.server.aop;

import com.jiangtou.server.aop.annotation.DataSourceSwitch;
import com.jiangtou.server.config.datasource.DynamicDataSourceContextHolder;
import com.jiangtou.server.enums.DataSourceEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author 陆迪
 * @date 2019/3/21 15:17
 **/
@Component
@Aspect
@Order(0)
public class DataSourceAspect {

    private static Logger logger = LogManager.getFormatterLogger(DataSourceAspect.class.getName());

    @Pointcut(value = "@annotation(com.jiangtou.server.aop.annotation.DataSourceSwitch)")
    public void aspectMethod() {
    }


    /**
     * 切换数据库
     */
    @Before(value = "aspectMethod()")
    @SuppressWarnings("unchecked")
    public void beforeMethod(JoinPoint point) {
        changeDataSource(point);

    }

    private void changeDataSource(JoinPoint point) {

        DataSourceEnum dataSourceEnum = null;

        MethodSignature proxySignature = (MethodSignature) point.getSignature();
        Method proxyMethod = proxySignature.getMethod();

        logger.info("开始对方法:%s切换数据库", proxyMethod.getDeclaringClass() + "." + proxyMethod.getName());
        /* 门店DataSourceKeyEnum 必须是第一个参数 */
        Object[] args = point.getArgs();
        if (args.length > 0) {
            if (args[0] != null && args[0] instanceof DataSourceEnum) {
                dataSourceEnum = (DataSourceEnum) args[0];
            }
        }

        if (dataSourceEnum != null) {
            DynamicDataSourceContextHolder.setDataSource(dataSourceEnum.sourceKey());
            logger.info("成功对方法:%s切换到数据库：%s", proxyMethod.getDeclaringClass() + "." + proxyMethod.getName(), dataSourceEnum.sourceKey());
            return;
        }

        //获取注解值
        String dataSourceValue = null;
        try {
            DataSourceSwitch dataSourceSwitch = proxyMethod.getAnnotation(DataSourceSwitch.class);
            dataSourceValue = dataSourceSwitch.value();
        } catch (NullPointerException ex) {
            //
        }

        if (dataSourceValue != null && !dataSourceValue.equals(DataSourceEnum.app.sourceKey())) {
            DynamicDataSourceContextHolder.setDataSource(dataSourceValue);
            logger.info("成功对方法:%s切换到数据库：%s", proxyMethod.getDeclaringClass() + "." + proxyMethod.getName(), dataSourceValue);
        } else {
            logger.info("方法:%s不需要切换数据库，使用默认数据库", proxyMethod.getDeclaringClass() + "." + proxyMethod.getName());
        }
    }

    /**
     * 销毁数据源  在所有的方法执行执行完毕后
     *
     * @param joinPoint 切点
     */
    @AfterReturning("aspectMethod()")
    public void afterMethodReturn(JoinPoint joinPoint) {
        DynamicDataSourceContextHolder.clear();
    }
}

package com.ludi.server.config.shiro;


import com.ludi.server.exception.BadRequestException;
import com.ludi.server.pojo.base.BaseResult;
import com.ludi.server.pojo.base.ResultStatus;
import com.ludi.server.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.web.filter.PathMatchingFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 陆迪
 * 权限管理URL过滤器
 */
public class UrlPathMatchingFilter extends PathMatchingFilter {

    private static Logger logger = LogManager.getFormatterLogger(UrlPathMatchingFilter.class.getName());

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) {
        logger.info("UrlPathMatchingFilter");

        return true;
    }


    private void response402(ServletRequest req, ServletResponse resp, String msg) throws BadRequestException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = httpServletResponse.getWriter()) {
            String data = JsonUtil.toJSONString(new BaseResult<>(ResultStatus.UNAUTHORIZED));
            out.append(data);
            out.flush();
        } catch (IOException e) {
            logger.error("直接返回Response信息出现IOException异常:\t\n", e.getMessage());
            throw new BadRequestException(ResultStatus.IO_ERROR, "直接返回Response信息出现IOException异常:" + e.getMessage());
        }
    }
}

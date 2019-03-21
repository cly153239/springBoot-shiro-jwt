package com.ludi.server.config.shiro;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ludi.server.exception.BadRequestException;
import com.ludi.server.exception.UnLoginException;
import com.ludi.server.pojo.base.BaseResult;
import com.ludi.server.pojo.base.ResultStatus;
import com.ludi.server.pojo.vo.UserVo;
import com.ludi.server.util.JwtUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author 陆迪
 * @date 2019/3/21 15:40
 * jwt验证拦截
 **/
@PropertySource("classpath:config.properties")
public class JwtFilter extends BasicHttpAuthenticationFilter {

    private final static Logger logger = LogManager.getLogger(JwtFilter.class.getName());

    private final static String ACCESS_AUTHORIZATION_HEADER = "Access-Authorization";
    private final static String REFRESH_AUTHORIZATION_HEADER = "Refresh-Authorization";

    /**
     * token过期允许有一分钟的置换期
     */
    @Value("${access.token.graceTime}")
    private long accessTokenGraceTime;
    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Authorization字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader(ACCESS_AUTHORIZATION_HEADER);
        return authorization != null;
    }

    /**
     * 进行AccessToken登录认证授权
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader(ACCESS_AUTHORIZATION_HEADER);

        JwtToken token = new JwtToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        Subject subject = getSubject(request, response);
        subject.login(token);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 这里我们详细说明下为什么最终返回的都是true，即允许访问
     * 例如我们提供一个地址 GET /article
     * 登入用户和游客看到的内容是不同的
     * 如果在这里返回了false，请求会被直接拦截，用户看不到任何东西
     * 所以我们在这里返回true，Controller中可以通过 subject.isAuthenticated() 来判断用户是否登入
     * 如果有些资源只有登入用户才能访问，我们只需要在方法上面加上 @RequiresAuthentication 注解即可
     * 但是这样做有一个缺点，就是不能够对GET,POST等请求进行分别过滤鉴权(因为我们重写了官方的方法)，但实际上对应用影响不大
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        logger.info("isAccessAllowed");

        //判断用户是否想登入
        if (this.isLoginAttempt(request, response)) {
            if (isRefreshAttempt(request)) {
                //验证token并刷新刷新token
                return refreshToken(request, response);
            } else {
                //登陆验证
                return this.login(request, response);
            }
        }
        response401(request, response, ResultStatus.UNLOGIN, "当前访问需要登陆验证.");
        return false;
    }

    /**
     * token 登陆验证
     * @param request
     * @param response
     * @return
     */
    private boolean login(ServletRequest request, ServletResponse response) {
        try {
            return this.executeLogin(request, response);
        } catch (Exception exception) {
            String message = exception.getMessage();
            Throwable throwable = exception.getCause();
            if (throwable instanceof SignatureVerificationException) {
                message = "Token校验失败。";
            } else if (throwable instanceof TokenExpiredException) {
                //token过期，如果token过期时间在限度(accessTokenGraceTime)范围内,则通过

                String authToken = this.getAccessAuthorizationHeader(request);
                if (JwtUtil.isWithInExpireAuthToken(authToken)) {
                    return true;
                }
                message = "token已经过期.";
            }
            response401(request, response, ResultStatus.UNLOGIN,  message);
            return false;
        }
    }

    /**
     * 此处为AccessToken刷新，进行判断RefreshToken是否过期，未过期就返回新的AccessToken且继续正常访问
     */
    private boolean refreshToken(ServletRequest request, ServletResponse response) {
        // 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
        String accessToken = getAccessAuthorizationHeader(request);
        String refreshToken = getRefreshAuthorizationHeader(request);

        //判断token是否存在
        if (accessToken == null || refreshToken == null) {
            response401(request, response, ResultStatus.UNLOGIN, "token获取失败。");
            return false;
        }


        boolean isRightAccessToken;
        UserVo userVo = null;
        try {
            userVo = JwtUtil.getUserVo(accessToken);
            isRightAccessToken = JwtUtil.verifyAccessToken(accessToken);
        } catch (Exception e) {
            Throwable throwable = e.getCause();
            //如果token抛出已过期异常则允许刷新token
            isRightAccessToken = e instanceof TokenExpiredException || throwable instanceof TokenExpiredException;
        }
        if (!isRightAccessToken || userVo == null) {
            response401(request, response, ResultStatus.UNLOGIN, "token校验失败。");
            return false;
        }

        String userCode = userVo.getUserCode();
        String deviceId = userVo.getDeviceId();

        boolean isRightRefreshToken = JwtUtil.verifyRefreshToken(refreshToken, userCode, deviceId);
        if (!isRightRefreshToken) {
            response401(request, response, ResultStatus.UNLOGIN, "token校验失败。");
            return false;
        }

        // 判断Redis中RefreshToken是否存在
        if (!JwtUtil.isRefreshTokenExistsInRedis(userCode, deviceId)) {
            response401(request, response, ResultStatus.UNLOGIN, "token已经失效。");
            return false;
        }
        //判断refreshToken的createTime时间戳和redis中的时间戳是否一样
        long  createTimeMillisRedis = JwtUtil.getTokenCreateTimeFromRedis(userCode, deviceId);
        long createTimeMillis;
        try {
            createTimeMillis = JwtUtil.getTokenCreateTimeFromToken(refreshToken);
        } catch (UnLoginException e){
            response401(request, response, ResultStatus.UNLOGIN, "token已经失效。");
            return false;
        }
        if (createTimeMillisRedis != createTimeMillis) {
            response401(request, response, ResultStatus.REPEAT_REFRESH, "token重复刷新。");
            return false;
        }

        long currentTimeMillis = System.currentTimeMillis();

        String newAccessToken = JwtUtil.signAuthToken(userVo, currentTimeMillis);
        //refreshToken创建的时候同时在redis中插入一条createTime的时间戳
        String newRefreshToken = JwtUtil.signRefreshToken(userCode, deviceId, currentTimeMillis);

        // 将新刷新的AccessToken再次进行Shiro的登录
        JwtToken jwtToken = new JwtToken(newAccessToken);
        // 提交给UserRealm进行认证，如果错误他会抛出异常并被捕获，如果没有抛出异常则代表登入成功，返回true
        this.getSubject(request, response).login(jwtToken);

        // 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader(ACCESS_AUTHORIZATION_HEADER, newAccessToken);
        httpServletResponse.setHeader(REFRESH_AUTHORIZATION_HEADER, newRefreshToken);
        httpServletResponse.setHeader("Access-Control-Expose-Headers", ACCESS_AUTHORIZATION_HEADER + "," + REFRESH_AUTHORIZATION_HEADER);

        logger.info("刷新token成功。authToken:" + accessToken + "\t\nrefreshToken:" + refreshToken);
        return true;

    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }


    /**
     * 无需转发，直接返回Response信息
     */
    private void response401(ServletRequest req, ServletResponse resp, ResultStatus resultStatus, String msg) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        try (OutputStream out = httpServletResponse.getOutputStream()) {
            String data = JSON.toJSONString(new BaseResult<>(resultStatus, msg));
            if (data != null) {
                out.write(data.getBytes(StandardCharsets.UTF_8));
            }
            out.flush();
        } catch (IOException e) {
            logger.error("直接返回Response信息出现IOException异常:\t\n", e.getMessage());
            throw new RuntimeException("直接返回Response信息出现IOException异常:" + e.getMessage());
        }

    }




    /**
     * 判断用户是否想要刷新token
     * @param request
     * @return
     */
    private boolean isRefreshAttempt(ServletRequest request) {
        String refreshToken = getRefreshAuthorizationHeader(request);
        return refreshToken != null;
    }

    private String getAccessAuthorizationHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return httpRequest.getHeader(ACCESS_AUTHORIZATION_HEADER);
    }

    private String getRefreshAuthorizationHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return httpRequest.getHeader(REFRESH_AUTHORIZATION_HEADER);
    }
}

package com.ludi.server.config.shiro;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ludi.server.pojo.vo.UserVo;
import com.ludi.server.service.auth.UserService;
import com.ludi.server.util.JwtUtil;
import com.ludi.server.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 陆迪
 * @date 2019-03-08 20:59
 **/
@Component
public class UserRealm extends AuthorizingRealm {

    private final static Logger logger = LogManager.getLogger(UserRealm.class.getName());


    @Resource
    private UserService userService;

    /**
     * 权限验证
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //TODO 权限认证暂时留空
        String userName = (String) principals.getPrimaryPrincipal();
        logger.debug("权限认证方法AuthorizationInfo" + userName);

        return null;
    }

    /**
     * 登陆验证
     *
     * @param authenticationToken 用户提交的身份信息
     * @return 返回用户身份信息
     * @throws AuthenticationException 身份验证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        logger.info("开始验证用户次数》》》》》》》》》》》》》》》》》》》》》");

        String token = (String) authenticationToken.getCredentials();
        // 解密获得username，用于和数据库进行对比
        UserVo userVo = null;

        try {
            userVo = JwtUtil.getUserVo(token);
            JwtUtil.verifyAccessToken(token);
        } catch (Exception e) {
            if (e instanceof TokenExpiredException && userVo != null) {
                UserUtil.setCurrentUserVo(userVo);
            }
            throw new AuthenticationException(e);
        }

        if (userVo == null) {
            throw new AuthenticationException("token invalid");
        }

        UserUtil.setCurrentUserVo(userVo);


        return new SimpleAuthenticationInfo(token, token, getName());
    }
}

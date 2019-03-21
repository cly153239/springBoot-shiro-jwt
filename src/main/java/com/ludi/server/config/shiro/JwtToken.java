package com.ludi.server.config.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @author 陆迪
 * @date 2019/3/21 15:38
 * shiro token属性扩展，设置JWT 产生的token为shiro验证的凭证
 **/
public class JwtToken extends UsernamePasswordToken {

    /**
     * 密钥
     */
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}

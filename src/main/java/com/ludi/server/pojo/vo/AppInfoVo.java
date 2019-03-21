package com.ludi.server.pojo.vo;

import java.io.Serializable;

/**
 * @author 陆迪
 * @date 2019/3/17 10:24
 **/
public class AppInfoVo implements Serializable {

    private static final long serialVersionUID = 2876475271446650609L;

    private UserVo userVo;
    private TokenVo tokenVo;

    public AppInfoVo(UserVo userVo, TokenVo tokenVo) {
        this.userVo = userVo;
        this.tokenVo = tokenVo;
    }

    public UserVo getUserVo() {
        return userVo;
    }

    public void setUserVo(UserVo userVo) {
        this.userVo = userVo;
    }

    public TokenVo getTokenVo() {
        return tokenVo;
    }

    public void setTokenVo(TokenVo tokenVo) {
        this.tokenVo = tokenVo;
    }
}

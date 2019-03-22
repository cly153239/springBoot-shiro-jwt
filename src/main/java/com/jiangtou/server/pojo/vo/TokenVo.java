package com.jiangtou.server.pojo.vo;

import java.io.Serializable;

/**
 * @author 陆迪
 * @date 2019/3/17 10:21
 **/
public class TokenVo implements Serializable {

    private static final long serialVersionUID = 8289291464865139807L;

    private String accessToken;
    private String refreshToken;
    private long authTokenExpireTime;

    public TokenVo(String accessToken, String refreshToken, long authTokenExpireTime) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.authTokenExpireTime = authTokenExpireTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getAuthTokenExpireTime() {
        return authTokenExpireTime;
    }

    public void setAuthTokenExpireTime(long authTokenExpireTime) {
        this.authTokenExpireTime = authTokenExpireTime;
    }
}

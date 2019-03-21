package com.ludi.server.pojo.dto;

import com.ludi.server.pojo.base.BaseUser;

import javax.validation.constraints.NotNull;

/**
 * @author 陆迪
 * @date 2019-03-09 09:57
 * 用户登陆传输到服务器的信息
 **/
public class UserDto extends BaseUser {

    private static final long serialVersionUID = -835381622357206824L;
    @NotNull
    protected String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

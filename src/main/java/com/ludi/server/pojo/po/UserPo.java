package com.ludi.server.pojo.po;


import com.ludi.server.pojo.base.BaseUser;

/**
 * @author 陆迪
 * @date 2019-03-09 10:08
 **/
public class UserPo extends BaseUser {

    private static final long serialVersionUID = -8043256554888102506L;
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserPo{" +
                "userName='" + userName + '\'' +
                ", userCode='" + userCode + '\'' +
                ", shpNo='" + shpNo + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}

package com.jiangtou.server.pojo.vo;

import com.jiangtou.server.pojo.dto.UserDto;
import com.jiangtou.server.pojo.po.UserPo;
import com.jiangtou.server.pojo.base.BaseUser;


/**
 * @author 陆迪
 * @date 2019-03-08 20:23
 * 用户登陆成功，返回客户端的信息
 **/
public class UserVo extends BaseUser {

    private String userName;

    /**
     * 构建当前用户的登陆信息，门店，设备ID
     * @param userDto
     */
    public void buildByUserDto(UserDto userDto) {
        this.userCode = userDto.getUserCode();
        this.shpNo = userDto.getShpNo();
        this.deviceId = userDto.getDeviceId();
    }

    /**
     * 构建当前用户的详细信息,姓名等
     * @param userPo
     */
    public void buildByUserPo(UserPo userPo) {
        this.userName = userPo.getUserName();
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }




    @Override
    public String toString() {
        return "UserVo{" +
                "userName='" + userName + '\'' +
                ", userCode='" + userCode + '\'' +
                ", shpNo='" + shpNo + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}

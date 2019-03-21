package com.ludi.server.service.auth.impl;

import com.ludi.server.exception.BadRequestException;
import com.ludi.server.exception.UnLoginException;
import com.ludi.server.pojo.base.ResultStatus;
import com.ludi.server.pojo.dto.UserDto;
import com.ludi.server.pojo.po.UserPo;
import com.ludi.server.pojo.vo.AppInfoVo;
import com.ludi.server.pojo.vo.TokenVo;
import com.ludi.server.pojo.vo.UserVo;
import com.ludi.server.service.auth.UserService;
import com.ludi.server.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author 陆迪
 * @date 2019-03-08 22:10
 **/
@Service
public class UserServiceImpl implements UserService {

    @Override
    public Optional<UserPo> getUserPo(String userCode) {
        UserPo userPo = new UserPo();
        userPo.setUserCode("135261");
        userPo.setUserName("陆迪");
        userPo.setPassword("135261");

        return Optional.of(userPo);
    }

    @Override
    public AppInfoVo login(UserDto userDto) throws BadRequestException {
        AppInfoVo appInfoVo;

        String userCode = userDto.getUserCode();
        String deviceId = userDto.getDeviceId();

        //获取数据库中用户详细信息
        Optional<UserPo> optionalUserVo = getUserPo(userCode);
        if (optionalUserVo.isEmpty()) {
            throw new BadRequestException(ResultStatus.VERIFY_ERROR, "用户不存在。");
        }
        UserPo userPo = optionalUserVo.get();

        //验证登陆信息是否匹配
        if (!userPo.getPassword().equals(userDto.getPassword())) {
            throw new BadRequestException(ResultStatus.VERIFY_ERROR, "用户名或密码错误.");
        }

        long currentTime = System.currentTimeMillis();

        UserVo userVo = new UserVo();
        userVo.buildByUserDto(userDto);
        userVo.buildByUserPo(userPo);

        //根据登陆信息，生成token
        String authToken = JwtUtil.signAuthToken(userVo, currentTime);
        long authTokenExpireTime = JwtUtil.getExpireTime(authToken);

        String refreshToken = JwtUtil.signRefreshToken(userCode, deviceId, currentTime);

        TokenVo tokenVo = new TokenVo(authToken, refreshToken, authTokenExpireTime);

        appInfoVo = new AppInfoVo(userVo, tokenVo);

        return appInfoVo;
    }

    @Override
    public void logout() throws UnLoginException {
        Subject subject = SecurityUtils.getSubject();
        String token = (String) subject.getPrincipal();

        subject.logout();
        SecurityUtils.getSecurityManager().logout(subject);
        JwtUtil.logout(token);

    }
}

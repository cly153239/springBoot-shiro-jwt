package com.jiangtou.server.service.auth;

import com.jiangtou.server.exception.BadRequestException;
import com.jiangtou.server.exception.UnLoginException;
import com.jiangtou.server.pojo.dto.UserDto;
import com.jiangtou.server.pojo.po.UserPo;
import com.jiangtou.server.pojo.vo.AppInfoVo;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author 陆迪
 * @date 2019-03-08 21:01
 **/
@Service
public interface UserService {

    /**
     * 通过用户名获取数据库中用户的信息
     * @param userCode
     * @return
     */
    Optional<UserPo> getUserPo(String userCode);

    /**
     * 登陆验证失败则抛出异常
     * @param userPo
     * @return 用户完整信息
     * @throws BadRequestException
     */
    /**
     * 登陆成功则返回token
     * @param userDto
     * @return
     * @throws BadRequestException
     */
    AppInfoVo login(UserDto userDto) throws BadRequestException;

    /**
     * 登出
     */
    void logout() throws UnLoginException;
}

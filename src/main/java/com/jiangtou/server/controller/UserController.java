package com.jiangtou.server.controller;

import com.jiangtou.server.service.auth.UserService;
import com.jiangtou.server.service.log.OperationLogService;
import com.jiangtou.server.exception.BadRequestException;
import com.jiangtou.server.exception.UnLoginException;
import com.jiangtou.server.pojo.base.BaseResult;
import com.jiangtou.server.pojo.base.ResultStatus;
import com.jiangtou.server.pojo.dto.UserDto;
import com.jiangtou.server.pojo.po.OperationLogPo;
import com.jiangtou.server.pojo.vo.AppInfoVo;
import com.jiangtou.server.pojo.vo.TokenVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author 陆迪
 * @date 2019-03-08 21:13
 **/
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private OperationLogService operationLogService;

    private static final Logger logger = LogManager.getLogger(UserController.class.getName());

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public BaseResult<AppInfoVo> userLogin(@RequestBody @Valid UserDto userDto, HttpServletResponse httpServletResponse) throws BadRequestException {

        logger.info("%s用户登陆", userDto.getUserCode());

        AppInfoVo appInfoVo = userService.login(userDto);
        TokenVo tokenVo = appInfoVo.getTokenVo();

        httpServletResponse.setHeader("Access-Authorization", tokenVo.getAccessToken());
        httpServletResponse.setHeader("Refresh-Authorization", tokenVo.getRefreshToken());
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Access-Authorization,Refresh-Authorization");

        return new BaseResult<>(ResultStatus.OK.getCode(), "此次登陆信息", appInfoVo);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public BaseResult<Object> userLogout() throws UnLoginException {

        logger.info("%s用户登出");
        userService.logout();

        return new BaseResult<>(ResultStatus.OK);
    }

    @RequestMapping(value = "/upload/test", method = RequestMethod.GET)
    public BaseResult<String> test() {
        operationLogService.insertOperationLog(new OperationLogPo());

        boolean isAuth = SecurityUtils.getSubject().isAuthenticated();
        if (isAuth) {
            return new BaseResult<>(ResultStatus.OK,"test");
        } else {
            return new BaseResult<>(ResultStatus.UNAUTHORIZED);
        }
    }
}

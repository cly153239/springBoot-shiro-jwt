package com.ludi.server.util;

import com.ludi.server.pojo.vo.UserVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import java.util.Optional;


/**
 * @author 陆迪
 * @date 2019-03-08 22:01
 * 当前线程用户类
 **/
public class UserUtil {




    private static final Logger logger = LogManager.getLogger(UserUtil.class.getName());
    private static final String SESSION_USER_KEY = "shiro-user";
    private static final String ERROR_STR = "未知用户";
    private static final int ERROR_CODE = -1;


    /**
     * 设置当前用户
     * @param userVo
     */
    public static void setCurrentUserVo(UserVo userVo) {
        SecurityUtils.getSubject().getSession().setAttribute("user", userVo);
    }

    /**
     * 获取当前用户userCode
     */
    public static Optional<UserVo> getCurrentUserVo() {
        Session session = SecurityUtils.getSubject().getSession();
        try {
            UserVo userVo = (UserVo) session.getAttribute(SESSION_USER_KEY);
            return Optional.of(userVo);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    
    /**
     * 获取当前用户userCode
     */
    public static String getCurrentUserCode() {
        Optional<UserVo> optionalUserVo = getCurrentUserVo();
        if (optionalUserVo.isPresent()) {
            return optionalUserVo.get().getUserCode();
        } else {
            return ERROR_STR;
        }
    }

    /**
     * 获取当前用户userName
     * */
    public static String getCurrentUserName() {
        Optional<UserVo> optionalUserVo = getCurrentUserVo();
        if (optionalUserVo.isPresent()) {
            return optionalUserVo.get().getUserName();
        } else {
            return ERROR_STR;
        }
    }


    /**
     * 获取当前用户shpNo
     */
    public static String getCurrentShpNo() {
        Optional<UserVo> optionalUserVo = getCurrentUserVo();
        if (optionalUserVo.isPresent()) {
            return optionalUserVo.get().getShpNo();
        } else {
            return ERROR_STR;
        }
    }


    /**
     * 获取当前用户deviceId
     */
    public static String getCurrentDeviceId() {
        Optional<UserVo> optionalUserVo = getCurrentUserVo();
        if (optionalUserVo.isPresent()) {
            return optionalUserVo.get().getDeviceId();
        } else {
            return ERROR_STR;
        }
    }


}

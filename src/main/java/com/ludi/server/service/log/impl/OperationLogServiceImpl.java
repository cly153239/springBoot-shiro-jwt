package com.ludi.server.service.log.impl;

import com.ludi.server.pojo.po.OperationLogPo;
import com.ludi.server.pojo.vo.UserVo;
import com.ludi.server.service.log.OperationLogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;


/**
 * @author 陆迪
 * @date 2019-03-09 13:57
 * 操作日志
 **/
@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final static Logger logger = LogManager.getLogger(OperationLogServiceImpl.class.getName());

    @Override
    public void insertOperationLog(OperationLogPo operationLogPo) {
        UserVo userVo = (UserVo) SecurityUtils.getSubject().getSession().getAttribute("user");
        logger.info(userVo.toString());
        logger.info("测试");
    }
}

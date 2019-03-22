package com.jiangtou.server.service.log;

import com.jiangtou.server.pojo.po.OperationLogPo;



/**
 * @author 陆迪
 * @date 2019-03-09 13:48
 **/
public interface OperationLogService {

    /**
     * 记录操作日志
     * @param operationLogPo
     */
    void insertOperationLog(OperationLogPo operationLogPo);
}

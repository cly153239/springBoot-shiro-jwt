package com.ludi.server.controller;

import com.ludi.server.pojo.base.BaseResult;
import com.ludi.server.pojo.base.ResultStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 陆迪
 * @date 2019/3/17 11:58
 * 与客户端同步时间
 **/
@RestController
@RequestMapping("/time")
public class SynchronisedTimeController {

    public BaseResult<Long> getServerTime() {
        long currentTime = System.currentTimeMillis();

        return new BaseResult<>(ResultStatus.OK.getCode(), "服务器时间戳", currentTime);
    }
}

package com.ludi.server.exception;

import com.ludi.server.pojo.base.ResultStatus;

/**
 * @author 陆迪
 * @date 2019/3/13 23:48
 * 未查找到数据异常
 **/
public class NotFoundException extends RuntimeException {

    private String code;

    public NotFoundException(ResultStatus resultStatus) {
        super(resultStatus.getMessage());
        this.code = resultStatus.getCode();
    }

    public NotFoundException(ResultStatus resultStatus, String message) {
        super(message);
        this.code = resultStatus.getCode();
    }


    public NotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}

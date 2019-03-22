package com.jiangtou.server.exception;

import com.jiangtou.server.pojo.base.ResultStatus;

/**
 * @author 陆迪
 * @date 2019-03-08 20:32
 **/
public class BadRequestException extends Exception {

    private String code;

    public BadRequestException(ResultStatus resultStatus) {
        super(resultStatus.getMessage());
        this.code = resultStatus.getCode();
    }

    public BadRequestException(ResultStatus resultStatus, String message) {
        super(message);
        this.code = resultStatus.getCode();
    }


    public BadRequestException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

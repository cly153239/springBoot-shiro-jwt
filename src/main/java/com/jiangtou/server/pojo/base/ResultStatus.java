package com.jiangtou.server.pojo.base;

/**
 * @author 陆迪
 * @date 2019/3/16 9:50
 **/
public enum ResultStatus {

    /**
     * 返回信息键值描述对
     */
    OK("OK", "请求成功"),
    BAD_REQUEST("BAD_REQUEST", "客户端错误"),
    UNLOGIN("UNLOGIN", "用户未登录"),
    REPEAT_REFRESH("REPEAT_REFRESH", "重复刷新token"),
    UNAUTHORIZED("UNAUTHORIZED", "用户已登陆，但无访问权限"),
    NOT_FOUND("NOT_FOUND", "发出的请求针对的是不存在的记录，服务器没有进行操作。"),
    PARAMETER_ERROR("PARAMETER_ERROR", "参数校验错误。"),
    IO_ERROR("IO_ERROR", "出现IO异常。"),
    VERIFY_ERROR("VERIFY_ERROR", "输入的信息不符合，验证错误");

    private final String code;
    private final String message;

    ResultStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.code + " " + name() + " " + this.message;
    }


    public static ResultStatus resolve(String statusCode) {
        for (ResultStatus status : values()) {
            if (status.code.equals(statusCode)) {
                return status;
            }
        }
        return null;
    }
}

package com.ludi.server.enums;


/**
 * @author 陆迪
 * @date 2019/3/15 17:19
 **/
public enum LogTypeEnum {

    /**
     * 日志文件类型
     */
    operation("operation", "操作日志记录"),
    pos("pos", "数据日志");

    private final String value;
    private final String reasonPhrase;


    LogTypeEnum(String value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public String value() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public static LogTypeEnum getFileType(String value) throws Exception {
        LogTypeEnum logTypeEnum = resolve(value);
        if (logTypeEnum == null) {
            throw new Exception("没有找到匹配的 [" + value + "]");
        }
        return logTypeEnum;
    }


    public static LogTypeEnum resolve(String value) {
        for (LogTypeEnum logTypeEnum : values()) {
            if (logTypeEnum.value.equals(value)) {
                return logTypeEnum;
            }
        }
        return null;
    }
}

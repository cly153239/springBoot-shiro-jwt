package com.jiangtou.server.enums;

/**
 * @author 陆迪
 * @date 2019/3/21 15:20
 * 多数据源名称枚举类，方便动态Aspect获取数据源选则参数
 **/
public enum DataSourceEnum {

    /**
     * 总部
     */
    app("app");

    private final String sourceKey;

    DataSourceEnum(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String sourceKey() {
        return sourceKey;
    }


    public static DataSourceEnum resolve(String statusCode) {
        for (DataSourceEnum sourceEnum : values()) {
            if (sourceEnum.sourceKey().equals(statusCode)) {
                return sourceEnum;
            }
        }
        return null;
    }

}

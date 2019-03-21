package com.ludi.server.pojo.base;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author 陆迪
 * @date 2019-03-09 10:02
 **/
public class BaseUser implements Serializable {

    @NotNull
    protected String userCode;

    @Pattern(regexp = "[0-9]{3}")
    protected String shpNo;
    @NotNull
    protected String deviceId;

    private final static long serialVersionUID = 334224953988435L;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getShpNo() {
        return shpNo;
    }

    public void setShpNo(String shpNo) {
        this.shpNo = shpNo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

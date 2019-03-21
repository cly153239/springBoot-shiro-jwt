package com.ludi.server.pojo.po;

import java.util.Date;

/**
 * @author 陆迪
 * @date 2019-03-09 13:52
 * 用户操作操作日志
 **/
public class OperationLogPo {

    /**
     * 门店
     */
    private String shpNo;

    /**
     * 用户信息
     */
    private String userCode;
    private String userName;
    private Date operationDate;

    /**
     * 客户端信息
     */
    private String host;
    private String deviceId;

    /**
     * 操作描述
     */
    private String dsc;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getShpNo() {
        return shpNo;
    }

    public void setShpNo(String shpNo) {
        this.shpNo = shpNo;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDsc() {
        return dsc;
    }

    public void setDsc(String dsc) {
        this.dsc = dsc;
    }
}

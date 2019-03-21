package com.ludi.server.pojo.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author 陆迪
 * @date 2018-10-28 22:17
 * 所有的请求必须带上当前登陆的门店shpNO
 **/
public class BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Pattern(regexp = "[0-9]{3}", message = "{valid.space.shpno}")
    @NotNull
    protected String shpNo;

    public String getShpNo() {
        return shpNo;
    }

    public void setShpNo(String shpNo) {
        this.shpNo = shpNo;
    }
}

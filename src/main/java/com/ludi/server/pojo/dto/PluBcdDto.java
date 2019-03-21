package com.ludi.server.pojo.dto;

import javax.validation.constraints.Pattern;

/**
 * @author 陆迪
 * @date 2019-03-09 14:35
 * 获取商品销售所需要的信息
 **/
public class PluBcdDto extends BaseDto {

    @Pattern(regexp = "[0-9]{1,13}")
    private String queryString;
    private Boolean isVip;

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public Boolean getVip() {
        return isVip;
    }

    public void setVip(Boolean vip) {
        isVip = vip;
    }
}

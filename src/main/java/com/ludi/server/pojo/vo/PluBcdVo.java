package com.ludi.server.pojo.vo;

/**
 * @author 陆迪
 * @date 2019-03-09 14:42
 * 商品销售必要的信息
 **/
public class PluBcdVo {

    private String bcd;
    private String pluName;
    private String pkUnit;
    private String spec;

    /**
     * 原价
     */
    private Double slPrc;
    /**
     * 原会员价
     */
    private Double vipSlPrc;
    /**
     * 特价
     */
    private Double mmSlPrc;
    /**
     * 会员特价
     */
    private Double mmVipSlPrc;

    public String getBcd() {
        return bcd;
    }

    public void setBcd(String bcd) {
        this.bcd = bcd;
    }

    public String getPluName() {
        return pluName;
    }

    public void setPluName(String pluName) {
        this.pluName = pluName;
    }

    public String getPkUnit() {
        return pkUnit;
    }

    public void setPkUnit(String pkUnit) {
        this.pkUnit = pkUnit;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Double getSlPrc() {
        return slPrc;
    }

    public void setSlPrc(Double slPrc) {
        this.slPrc = slPrc;
    }

    public Double getVipSlPrc() {
        return vipSlPrc;
    }

    public void setVipSlPrc(Double vipSlPrc) {
        this.vipSlPrc = vipSlPrc;
    }

    public Double getMmSlPrc() {
        return mmSlPrc;
    }

    public void setMmSlPrc(Double mmSlPrc) {
        this.mmSlPrc = mmSlPrc;
    }

    public Double getMmVipSlPrc() {
        return mmVipSlPrc;
    }

    public void setMmVipSlPrc(Double mmVipSlPrc) {
        this.mmVipSlPrc = mmVipSlPrc;
    }
}

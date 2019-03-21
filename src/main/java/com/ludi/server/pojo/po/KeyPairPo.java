package com.ludi.server.pojo.po;

/**
 * @author 陆迪
 * @date 2019/3/18 22:21
 * 私钥公钥实体类
 **/
public class KeyPairPo {

    private String publicKey;
    private String privateKey;
    private String module;

    public KeyPairPo(String publicKey, String privateKey, String module) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.module = module;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}

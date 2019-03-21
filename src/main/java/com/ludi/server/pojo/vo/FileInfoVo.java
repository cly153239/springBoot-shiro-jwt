package com.ludi.server.pojo.vo;

import java.io.Serializable;

/**
 * @author 陆迪
 * @date 2019/3/16 9:55
 **/
public class FileInfoVo implements Serializable {

    private final static long serialVersionUID = 1L;

    private String filePath;

    public FileInfoVo() {
    }

    public FileInfoVo(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

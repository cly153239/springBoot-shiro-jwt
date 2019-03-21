package com.ludi.server.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @author 陆迪
 * @date 2019/3/11 18:21
 **/
public class PageVo implements Serializable {

    /**
     * 当前页码数
     * 页码从1开始
     */
    private Integer pageNumber;
    /**
     * 总记录数
     */
    private Integer totalRecord;
    /**
     * 每页显示条数
     */
    private Integer pageSize;
    /**
     * 当前页的起始索引
     */
    private Integer index;

    private final static long serialVersionUID = 3322L;

    @JsonIgnore
    public boolean hasMore() {
        if (index == null || pageSize == null || totalRecord == null || pageNumber == null) {
            return false;
        }
        return (index + pageSize) < totalRecord;
    }


    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}

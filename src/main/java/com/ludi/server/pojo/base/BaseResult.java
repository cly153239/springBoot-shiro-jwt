package com.ludi.server.pojo.base;


/**
 * @author 陆迪
 * @date 2019/3/14 9:09
 * 标准返回类
 * code 为描述处理结果的key
 * msg 描述处理结果简短信息
 * data 为处理结果详细信息
 **/
public class BaseResult<T> {

    /**
     * 返回状态码
     */
    private String code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 返回的数据
     */
    private T data;


    public BaseResult(T data) {
       this(ResultStatus.OK, data);
    }

    public BaseResult(ResultStatus resultStatus) {
        this(resultStatus.getCode(), resultStatus.getMessage(), null);
    }
    public BaseResult(String code, String msg) {
       this(code, msg, null);
    }

    public BaseResult(ResultStatus resultStatus, T data) {
        this(resultStatus.getCode(), resultStatus.getMessage(), data);
    }

    public BaseResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public static class Builder<V> {

        private String mStatus;
        private String mMsg;
        private V mData;

        public Builder<V> setStatus(String status) {
            this.mStatus = status;
            return this;
        }

        public Builder<V> setMsg(String msg) {
            this.mMsg = msg;
            return this;
        }

        public Builder<V> setData(V data) {
            this.mData = data;
            return this;
        }


        /**
         * 返回MarketResult对象
         */
        public BaseResult<V> build() {
            return new BaseResult<>(mStatus, mMsg, mData);
        }
    }
}

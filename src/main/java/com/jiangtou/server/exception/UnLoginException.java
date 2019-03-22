package com.jiangtou.server.exception;

/**
 * @author 陆迪
 * @date 2019/3/13 23:22
 * 凡是读取验证token，获取当前用户相关的操作，若出现异常，则都抛出此异常
 **/
public class UnLoginException extends Exception{

    public UnLoginException(String msg){
        super(msg);
    }

    public UnLoginException() {
        super();
    }
}

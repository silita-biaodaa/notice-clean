package com.silita.biaodaa.rules.exception;

/**
 * Created by dh on 2018/9/26.
 */
public class MyRetryException extends Exception{
    public MyRetryException(String message) {
        super("重试异常:"+message);
    }
}

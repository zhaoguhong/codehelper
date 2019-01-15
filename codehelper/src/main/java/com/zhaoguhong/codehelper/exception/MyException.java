package com.zhaoguhong.codehelper.exception;

/**
 * Created by zhaoguhong on 2019/1/15.
 */
public class MyException  extends RuntimeException{

    public MyException() {
    }

    public MyException(String message) {
        super(message);
    }
}

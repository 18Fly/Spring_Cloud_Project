package org.project.feign.common;

public class CustomException extends RuntimeException{
    /**
     * 自定义异常类
     *
     * @param message 字符串类型的异常信息
     */
    public CustomException(String message) {
        super(message);
    }
}

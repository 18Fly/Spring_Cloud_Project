package org.project.feign.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     *
     * @param ex 异常对象
     * @return 异常信息返回前端显示
     */
    @ExceptionHandler(Exception.class)
    public Return<String> exceptionHandler(Exception ex) {
        log.error(ex.getMessage());

        return Return.error("服务端内部未知异常|" + ex.getMessage());
    }

}

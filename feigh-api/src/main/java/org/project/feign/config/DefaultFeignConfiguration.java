package org.project.feign.config;

import feign.Logger;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

//默认Feign的配置类
@Slf4j
@Configuration
public class DefaultFeignConfiguration {
    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.BASIC;
    }

    //为每次Fegin内部请求加上Authorization鉴权认证请求头
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String origin = getOriginalAuthorizationHeader();
            if (origin != null) {
                requestTemplate.header("Authorization", origin);
            }
        };
    }

    //获取每个线程的Http请求
    private String getOriginalAuthorizationHeader() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        try {
            request = requestAttributes.getRequest();
        } catch (Exception ex) {
            log.error("内部请求Feign出错" + ex.getMessage());
        }
        return request != null ? request.getHeader("Authorization") : null;
    }
}

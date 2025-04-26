package org.project;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@EnableTransactionManagement
@EnableCaching
public class LoginServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoginServiceApplication.class, args);
        log.info("员工微服务模块启动完成");
    }
}
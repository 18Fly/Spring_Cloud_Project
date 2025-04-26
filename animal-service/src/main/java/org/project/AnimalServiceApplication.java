package org.project;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
public class AnimalServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnimalServiceApplication.class, args);
        log.info("动物信息微服务模块启动完成");
    }
}
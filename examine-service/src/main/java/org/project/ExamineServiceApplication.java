package org.project;

import lombok.extern.slf4j.Slf4j;
import org.project.feign.clients.AdoptClient;
import org.project.feign.clients.AnimalClient;
import org.project.feign.clients.UserClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients(clients = {AdoptClient.class, UserClient.class, AnimalClient.class})
public class ExamineServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamineServiceApplication.class, args);
        log.info("审核微服务模块启动完成");
    }
}
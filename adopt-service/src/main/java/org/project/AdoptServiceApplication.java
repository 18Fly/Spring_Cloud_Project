package org.project;

import lombok.extern.slf4j.Slf4j;
import org.project.feign.clients.AnimalClient;
import org.project.feign.clients.EmployeeClient;
import org.project.feign.clients.ExamineClient;
import org.project.feign.clients.UserClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients(clients = {AnimalClient.class, UserClient.class, EmployeeClient.class, ExamineClient.class})
public class AdoptServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdoptServiceApplication.class, args);
        log.info("领养信息微服务模块启动完成");
    }
}

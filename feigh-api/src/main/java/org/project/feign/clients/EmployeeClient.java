package org.project.feign.clients;

import org.project.feign.config.DefaultFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "loginService", configuration = DefaultFeignConfiguration.class)
public interface EmployeeClient {

    /**
     * Feign内部请求员工ID列表
     *
     * @return 员工ID列表
     */
    @GetMapping("/employee")
    List<Long> getEmployeeIds();
}

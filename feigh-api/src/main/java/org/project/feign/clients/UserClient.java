package org.project.feign.clients;

import org.project.feign.config.DefaultFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "userService", configuration = DefaultFeignConfiguration.class)
public interface UserClient {

    /**
     * Feign内部获取用户ID列表
     *
     * @return 用户ID列表
     */
    @GetMapping("/user")
    List<Long> getUserIds();

    /**
     * Feign内部根据用户ID获取对应手机号
     *
     * @param id 用户ID
     * @return 手机号
     */
    @GetMapping("/user/callphone/{id}")
    String getUserCallPhone(@PathVariable Long id);

    /**
     * Feign内部请求-根据用户ID查询其姓名
     *
     * @param uid 用户UID
     * @return 用户姓名
     */
    @GetMapping("/user/feign/{uid}")
    String getUserName(@PathVariable Long uid);
}

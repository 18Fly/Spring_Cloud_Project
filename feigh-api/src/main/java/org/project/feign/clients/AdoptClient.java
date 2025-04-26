package org.project.feign.clients;

import org.project.feign.config.DefaultFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "adoptService", configuration = DefaultFeignConfiguration.class)
public interface AdoptClient {

    /**
     * Feign内部审查模块获取对应领养单IDs信息
     *
     * @param atid 领养单ID
     * @return 返回List的IDs信息
     */
    @GetMapping("/adopt/feign/{atid}")
    List<Long> getIds2Examine(@PathVariable Long atid);

    /**
     * Feign内部请求-查询对应用户的领养单号
     *
     * @param uid 用户ID
     * @return 领养单号
     */
    @GetMapping("/adopt/feign/getUidAtid/{uid}")
    List<Long> getUserAtid(@PathVariable Long uid);

    /**
     * Feign内部请求-获取对应用户的审核频率
     *
     * @param atid 领养单Id
     * @return 用户领养审核频率
     */
    @GetMapping("/adopt/feign/getAdoptAfy/{atid}")
    Integer getAdoptAfy(@PathVariable Long atid);

    /**
     * Feign内部请求-根据领养单ID获取对应动物ID
     *
     * @param atid 领养单ID
     * @return 对应动物ID
     */
    @GetMapping("/adopt/feign/getAdoptAid/{atid}")
    Long getAdoptAid(@PathVariable Long atid);
}

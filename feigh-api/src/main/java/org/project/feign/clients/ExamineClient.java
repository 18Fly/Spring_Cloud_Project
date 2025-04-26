package org.project.feign.clients;

import org.project.feign.config.DefaultFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "examineService",configuration = DefaultFeignConfiguration.class)
public interface ExamineClient {

    /**
     * Feign内部请求-新增的领养信息订单与审核信息做关联
     *
     * @param atid 新增的领养信息单号
     * @return 保存结果
     */
    @GetMapping("/examine/feign/relativeadopt/{atid}")
    Boolean saveRelativeAdopt(@PathVariable Long atid);
}

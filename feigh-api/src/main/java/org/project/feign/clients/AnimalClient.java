package org.project.feign.clients;

import org.project.feign.config.DefaultFeignConfiguration;
import org.project.feign.pojo.Animal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "animalService", configuration = DefaultFeignConfiguration.class)
public interface AnimalClient {
    /**
     * Feign内部获取用户头像uri
     *
     * @param id 动物ID
     * @return 返回动物对象
     */
    @GetMapping("/animal/feign/{id}")
    Animal getAnimalById(@PathVariable Long id);

    /**
     * Feign内部动物ID列表
     *
     * @return 动物ID列表
     */
    @GetMapping("/animal")
    List<Long> getAnimalIds();

    /**
     * Feign内部获取动物数量
     *
     * @return 动物总数量
     */
    @GetMapping("/animal/feign/getAnimalCount")
    Integer getAnimalCount();

    @GetMapping("/animal/feign/editanimalstatus/{aid}")
    Boolean editAnimalStatus(@PathVariable Long aid);
}

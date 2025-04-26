package org.project.web;

import lombok.extern.slf4j.Slf4j;
import org.project.feign.common.Return;
import org.project.feign.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonWeb {

    // 保存路径，通过yml配置路径
    @Value("${save_path}")
    private String save_path;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 保存用户上传的图片信息
     *
     * @param file 图片信息
     * @return 图片uri
     */
    @PostMapping("/upload")
    public Return<String> saveImages(@RequestBody MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String[] split = new String[]{};
        if (originalFilename != null) {
            split = originalFilename.split("\\.");
        }
        String imgUrl = UUID.randomUUID() + "." + split[split.length - 1];

        File path = new File(save_path);
        if (path.exists()) {
            path.mkdirs();
        }

        try {
            file.transferTo(new File(save_path + imgUrl));
        } catch (Exception ex) {
            log.error("图片上传出现问题\r" + ex.getMessage());
        }

        return Return.success(imgUrl);
    }
}

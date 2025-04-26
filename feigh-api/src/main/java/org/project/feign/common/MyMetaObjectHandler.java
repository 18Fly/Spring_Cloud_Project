package org.project.feign.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 * 字段自动填充，因为放在Feign中，如果有一些Entitiy没有填充的字段，可以在单独模块中再次配置
 */


@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Autowired
    private HttpServletRequest req;

    /**
     * 插入操作，自动填充
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[INSERT]......");

        metaObject.setValue("createtime", LocalDateTime.now());
        metaObject.setValue("updatetime", LocalDateTime.now());
        metaObject.setValue("ipaddress", req.getHeader("x-forwarded-for") == null ? req.getHeader("X-Real-IP") : req.getHeader("x-forwarded-for"));
    }

    /**
     * 更新操作，自动填充
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[UPDATE]......");

        metaObject.setValue("updatetime", LocalDateTime.now());
        metaObject.setValue("ipaddress", req.getHeader("x-forwarded-for") == null ? req.getHeader("X-Real-IP") : req.getHeader("x-forwarded-for"));
    }
}

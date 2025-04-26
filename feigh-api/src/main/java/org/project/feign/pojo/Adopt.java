package org.project.feign.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Adopt implements Serializable {
    private Long atid;
    private Long aid;
    private Long uid;
    private Long eid;
    private LocalDateTime createtime;
    private LocalDateTime updatetime;
    private String ipaddress;
    private Integer afy;
}

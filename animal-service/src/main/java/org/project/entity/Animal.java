package org.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Animal implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long aid;
    private Integer astatus;
    private String aname;
    private Integer aage;
    private Integer asex;
    private String adescription;
    private String aimages;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createtime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String ipaddress;
}

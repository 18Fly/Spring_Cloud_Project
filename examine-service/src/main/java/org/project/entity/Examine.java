package org.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Examine implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long emid;

    private Long atid;

    private Integer estatus;

    private String euimage;

    private String emdescription;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createtime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;
    private String ipaddress;
}

package org.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long eid;
    private String ename;
    private String epassword;
    private int eage;
    private int esex;
    private String ecallphone;
    private String eothercall;
    private String eemail;
    private int epower;
    private int estatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createtime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String ipaddress;

}

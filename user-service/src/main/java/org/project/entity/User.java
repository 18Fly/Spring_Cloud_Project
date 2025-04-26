package org.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long uid;
    private String uname;
    private String upassword;
    private Integer usex;
    private Integer uage;
    private String udegree;
    private String uearing;
    private String ucallphone;
    private String uothercall;
    private String uaddress;
    private String uemail;
    private String uimage;
    private Integer ustatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createtime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String ipaddress;
}

package org.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Help {
    private static final long serialVersionUID = 1L;
    @TableId
    public Long helpid;

    public Long uid;
    public Long eid;
    public Integer estatus;
    public String images;
    public String title;
    public String content;
    public String callphone;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createtime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String ipaddress;
}

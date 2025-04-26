package org.project.feign.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Animal {
    private Long aid;
    private String aname;
    private Integer aage;
    private Integer asex;
    private String adescription;
    private String aimages;
    private LocalDateTime createtime;
    private LocalDateTime updatetime;
    private String ipaddress;
}

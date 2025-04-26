package org.project.dto;

import lombok.Data;
import org.project.entity.Examine;

@Data
public class Examine_Adopt extends Examine {

    private Long aid;

    private Long eid;

    private Long uid;

    private String userCallPhone;

    private String userName;

    private Integer afy;

}

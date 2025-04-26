package org.project.dto;

import lombok.Data;

import java.util.List;

@Data
public class Adopt_Ids {

    private List<Long> eid;
    private List<Long> uid;
    private List<Long> aid;

}

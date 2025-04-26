package org.project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.project.entity.Adopt;

@EqualsAndHashCode(callSuper = true)
@Data
public class Adopt_Animal extends Adopt {

    private String aimages;

    private String aname;
}

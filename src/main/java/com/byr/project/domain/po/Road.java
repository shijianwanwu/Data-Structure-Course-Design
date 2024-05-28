package com.byr.project.domain.po;

import java.math.BigDecimal;

public interface Road {
    Integer getStartPoint();
    Integer getEndPoint();
    BigDecimal getDistance();
    BigDecimal getTime();
    Integer getId();

    String getVehicle();
}

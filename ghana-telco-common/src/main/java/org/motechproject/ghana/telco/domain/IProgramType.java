package org.motechproject.ghana.telco.domain;

import org.motechproject.ghana.telco.vo.Money;

import java.util.List;

public interface IProgramType {
    String getProgramName();
    String getProgramKey();
    List<String> getShortCodes();
    Integer getMinWeek();
    Integer getMaxWeek();
    Money getFee();
}
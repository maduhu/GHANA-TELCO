package org.motechproject.ghana.mtn.domain;

import org.motechproject.ghana.mtn.vo.Money;

import java.util.List;

public interface IProgramType {
    String getProgramName();
    String getProgramKey();
    List<String> getShortCodes();
    Integer getMinWeek();
    Integer getMaxWeek();
    Money getFee();
}
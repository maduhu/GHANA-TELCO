package org.motechproject.ghana.mtn.domain;

import org.motechproject.ghana.mtn.vo.Money;

import java.util.List;

public interface IProgramType {
   String getProgramName();
   List<String> getShortCodes();
   Integer getMinWeek();
   Integer getMaxWeek();
   Money getFee();
}
package org.motechproject.ghana.mtn.domain;

import java.util.List;

public interface IProgramType {
   String getProgramName();
   List<String> getShortCodes();
   Integer getMinWeek();
   Integer getMaxWeek();
   Double getFee();
}
package org.motechproject.ghana.telco;

import org.motechproject.ghana.telco.domain.SMSAudit;
import org.motechproject.ghana.telco.repository.AllSMSAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SMSAuditAnalyzer {

    @Autowired
    private AllSMSAudits allSMSAudits;

    public List<SMSAudit> getSmsAuditForDate(String date) {
        return allSMSAudits.fetchSmsByDate(date);
    }
}

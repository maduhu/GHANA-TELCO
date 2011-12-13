package com.motechproject;

import org.motechproject.ghana.mtn.domain.SMSAudit;
import org.motechproject.ghana.mtn.repository.AllSMSAudits;
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
package org.motechproject.ghana.mtn.parser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.RetainOrRollOverChildCareProgramSMS;
import org.motechproject.ghana.mtn.domain.builder.ShortCodeBuilder;
import org.motechproject.ghana.mtn.repository.AllShortCodes;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.mtn.domain.ShortCode.RETAIN_EXISTING_CHILDCARE_PROGRAM;
import static org.motechproject.ghana.mtn.domain.ShortCode.USE_ROLLOVER_TO_CHILDCARE_PROGRAM;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class RetainOrRollOverChildCareMessageParserTest {

    RetainOrRollOverChildCareMessageParser retainOrRollOverChildCareMessageParser;

    @Mock
    AllShortCodes allShortCodes;

    @Before
    public void setUp() {
        initMocks(this);
        retainOrRollOverChildCareMessageParser = new RetainOrRollOverChildCareMessageParser();
        setField(retainOrRollOverChildCareMessageParser, "allShortCodes", allShortCodes);
        when(allShortCodes.getShortCodeFor(RETAIN_EXISTING_CHILDCARE_PROGRAM)).thenReturn(shortCodeBuilder(RETAIN_EXISTING_CHILDCARE_PROGRAM, asList("E", "ee")).build());
        when(allShortCodes.getShortCodeFor(USE_ROLLOVER_TO_CHILDCARE_PROGRAM)).thenReturn(shortCodeBuilder(USE_ROLLOVER_TO_CHILDCARE_PROGRAM, asList("N", "nn")).build());
    }

    @Test
    public void shouldParseRollOverDecisionMessage() {
        String enrolledMobileNumber = "9500012345";

        RetainOrRollOverChildCareProgramSMS retainOrRollOverChildCareProgramSMS = retainOrRollOverChildCareMessageParser.parse("E ", enrolledMobileNumber);
        assertTrue(retainOrRollOverChildCareProgramSMS.retainExistingChildCareProgram());

        retainOrRollOverChildCareProgramSMS = retainOrRollOverChildCareMessageParser.parse("eE ", enrolledMobileNumber);
        assertTrue(retainOrRollOverChildCareProgramSMS.retainExistingChildCareProgram());

        retainOrRollOverChildCareProgramSMS = retainOrRollOverChildCareMessageParser.parse("N", enrolledMobileNumber);
        assertFalse(retainOrRollOverChildCareProgramSMS.retainExistingChildCareProgram());

        retainOrRollOverChildCareProgramSMS = retainOrRollOverChildCareMessageParser.parse("Nn", enrolledMobileNumber);
        assertFalse(retainOrRollOverChildCareProgramSMS.retainExistingChildCareProgram());
    }

    @Test
    public void shouldReturnNullIfParseRollOverDecisionMessageIsNotMatching() {
        String enrolledMobileNumber = "9500012345";

        assertNull(retainOrRollOverChildCareMessageParser.parse("Eee ", enrolledMobileNumber));
        assertNull(retainOrRollOverChildCareMessageParser.parse("Ee 2", enrolledMobileNumber));
        assertNull(retainOrRollOverChildCareMessageParser.parse("Ee e", enrolledMobileNumber));
        assertNull(retainOrRollOverChildCareMessageParser.parse("nnn ", enrolledMobileNumber));
        assertNull(retainOrRollOverChildCareMessageParser.parse("nnn 2", enrolledMobileNumber));
        assertNull(retainOrRollOverChildCareMessageParser.parse("nnn ", enrolledMobileNumber));
        assertNull(retainOrRollOverChildCareMessageParser.parse("ee nn", enrolledMobileNumber));
    }

    private ShortCodeBuilder shortCodeBuilder(String key, List<String> codes) {
        return new ShortCodeBuilder().withCodeKey(key).withShortCode(codes.toArray(new String[codes.size()]));
    }
}

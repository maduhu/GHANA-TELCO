package org.motechproject.ghana.telco.parser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.telco.domain.DeliverySMS;
import org.motechproject.ghana.telco.domain.ShortCode;
import org.motechproject.ghana.telco.repository.AllProgramTypes;
import org.motechproject.ghana.telco.repository.AllShortCodes;
import org.motechproject.util.DateUtil;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class DeliveryMessageParserTest {

    DeliveryMessageParser parser;
    @Mock
    private AllProgramTypes allProgramTypes;
    @Mock
    private AllShortCodes allShortCodes;
    private String senderMobileNumber = "123456";

    @Before
    public void setUp() {
        initMocks(this);

        parser = new DeliveryMessageParser();
        when(allShortCodes.getShortCodeFor(ShortCode.DELIVERY))
                .thenReturn(new ShortCode().setCodeKey(ShortCode.DELIVERY).setCodes(asList("delivery", "dd", "d")));
        setField(parser, "allShortCodes", allShortCodes);
        setField(parser, "allProgramTypes", allProgramTypes);
    }

    @Test
    public void ShouldParseDeliveryMessageWithoutDate() {
        String message = "d ";
        DeliverySMS sms = parser.parse(message, senderMobileNumber);
        assertEquals(message, sms.getMessage());
        assertEquals(senderMobileNumber, sms.getFromMobileNumber());
        assertEquals(DateUtil.today().toDate(), sms.getDomain());

        message = "delivery";
        sms = parser.parse(message, senderMobileNumber);
        assertEquals(message, sms.getMessage());
        assertEquals(senderMobileNumber, sms.getFromMobileNumber());
        assertEquals(DateUtil.today().toDate(), sms.getDomain());

        message = "dd ";
        sms = parser.parse(message, senderMobileNumber);
        assertEquals(message, sms.getMessage());
        assertEquals(senderMobileNumber, sms.getFromMobileNumber());
        assertEquals(DateUtil.today().toDate(), sms.getDomain());
    }
}


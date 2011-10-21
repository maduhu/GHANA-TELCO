package org.motechproject.ghana.mtn.parser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.ShortCode;
import org.motechproject.ghana.mtn.domain.builder.ShortCodeBuilder;
import org.motechproject.ghana.mtn.exception.InvalidMobileNumberException;
import org.motechproject.ghana.mtn.repository.AllShortCodes;
import org.motechproject.ghana.mtn.vo.ParsedRequest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RelativeProgramMessageParserTest {
    public static final ShortCode RELATIVE_SHORTCODE = new ShortCodeBuilder().withShortCode("R").build();
    private RelativeProgramMessageParser relativeRegisterProgramMessageParser;
    @Mock
    private AllShortCodes mockAllShortCodes;

    @Before
    public void setUp() {
        initMocks(this);
        when(mockAllShortCodes.getShortCodeFor(ShortCode.RELATIVE)).thenReturn(RELATIVE_SHORTCODE);
        relativeRegisterProgramMessageParser = new RelativeProgramMessageParser(mockAllShortCodes);
    }

    @Test
    public void ShouldParseRelativeMessage() {
        String mobileNumber = "0234567891";
        String inputMessage = "R " + mobileNumber + " P 12";
        String senderNumber = "0987654321";

        ParsedRequest parsedRequest = relativeRegisterProgramMessageParser.parse(inputMessage, senderNumber);

        assertThat(parsedRequest.getSubscriberNumber(), is(mobileNumber));
    }

    @Test(expected = InvalidMobileNumberException.class)
    public void ShouldThrowInvalidMobileNumberException() {
        String mobileNumber = "1234567890";
        String inputMessage = "R " + mobileNumber + " P 12";
        String senderNumber = "0987654321";

        when(mockAllShortCodes.getShortCodeFor(ShortCode.RELATIVE)).thenReturn(RELATIVE_SHORTCODE);

        relativeRegisterProgramMessageParser.parse(inputMessage, senderNumber);
    }

    @Test(expected = InvalidMobileNumberException.class)
    public void ShouldThrowInvalidMobileNumberExceptionWhenMobileNumberIsLessThanTenDigits() {
        String mobileNumber = "023890";
        String inputMessage = "R " + mobileNumber + " P 12";
        String senderNumber = "0987654321";

        when(mockAllShortCodes.getShortCodeFor(ShortCode.RELATIVE)).thenReturn(RELATIVE_SHORTCODE);

        relativeRegisterProgramMessageParser.parse(inputMessage,senderNumber);
    }

    @Test
    public void ShouldNotParseNonRelativeMessages() {
        String inputMessage = " P 12";
        String senderNumber = "0987654321";

        when(mockAllShortCodes.getShortCodeFor(ShortCode.RELATIVE)).thenReturn(RELATIVE_SHORTCODE);

        ParsedRequest parsedRequest = relativeRegisterProgramMessageParser.parse(inputMessage, senderNumber);

        assertNull(parsedRequest);
    }
}

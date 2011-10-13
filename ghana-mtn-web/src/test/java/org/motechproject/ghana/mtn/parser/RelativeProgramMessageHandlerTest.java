package org.motechproject.ghana.mtn.parser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.ShortCode;
import org.motechproject.ghana.mtn.domain.builder.ShortCodeBuilder;
import org.motechproject.ghana.mtn.exception.InvalidMobileNumberException;
import org.motechproject.ghana.mtn.repository.AllShortCodes;
import org.motechproject.ghana.mtn.vo.ParsedRequest;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RelativeProgramMessageHandlerTest {
    private RelativeProgramMessageHandler relativeRegisterProgramMessageHandler;
    @Mock
    private AllShortCodes mockAllShortCodes;

    @Before
    public void setUp() {
        initMocks(this);
        when(mockAllShortCodes.getAllCodesFor(ShortCode.RELATIVE)).thenReturn(Arrays.asList(new ShortCodeBuilder().withShortCode("R").build()));
        relativeRegisterProgramMessageHandler = new RelativeProgramMessageHandler(mockAllShortCodes);
    }

    @Test
    public void ShouldParseRelativeMessage() {
        String mobileNumber = "0234567891";
        String inputMessage = "R " + mobileNumber + " P 12";
        String senderNumber = "0987654321";

        ParsedRequest parsedRequest = relativeRegisterProgramMessageHandler.parse(inputMessage, senderNumber);

        assertThat(parsedRequest.getSubscriberNumber(), is(mobileNumber));
    }

    @Test(expected = InvalidMobileNumberException.class)
    public void ShouldThrowInvalidMobileNumberException() {
        String mobileNumber = "1234567890";
        String inputMessage = "R " + mobileNumber + " P 12";
        String senderNumber = "0987654321";

        when(mockAllShortCodes.getAllCodesFor(ShortCode.RELATIVE)).thenReturn(Arrays.asList(new ShortCodeBuilder().withShortCode("R").build()));

        relativeRegisterProgramMessageHandler.parse(inputMessage, senderNumber);
    }

    @Test(expected = InvalidMobileNumberException.class)
    public void ShouldThrowInvalidMobileNumberExceptionWhenMobileNumberIsLessThanTenDigits() {
        String mobileNumber = "023890";
        String inputMessage = "R " + mobileNumber + " P 12";
        String senderNumber = "0987654321";

        when(mockAllShortCodes.getAllCodesFor(ShortCode.RELATIVE)).thenReturn(Arrays.asList(new ShortCodeBuilder().withShortCode("R").build()));

        relativeRegisterProgramMessageHandler.parse(inputMessage,senderNumber);
    }

    @Test
    public void ShouldNotParseNonRelativeMessages() {
        String inputMessage = " P 12";
        String senderNumber = "0987654321";

        when(mockAllShortCodes.getAllCodesFor(ShortCode.RELATIVE)).thenReturn(Arrays.asList(new ShortCodeBuilder().withShortCode("R").build()));

        ParsedRequest parsedRequest = relativeRegisterProgramMessageHandler.parse(inputMessage, senderNumber);

        assertNull(parsedRequest);
    }
}

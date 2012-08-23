package org.motechproject.ghana.telco.parser;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.telco.exception.InvalidMonthException;
import org.motechproject.ghana.telco.repository.AllProgramTypes;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegisterProgramMessageParserTest {
    @InjectMocks
    private RegisterProgramMessageParser parser = new RegisterProgramMessageParser();
    @Mock
    private AllProgramTypes mockAllProgramTypes;

    public RegisterProgramMessageParserTest() {
        initMocks(this);
    }

    @Test
    public void shouldGetMessageStartWeekForProgramType() throws InvalidMonthException {
        ProgramType mockProgramType = mock(ProgramType.class);
        when(mockAllProgramTypes.findByCampaignShortCode("C")).thenReturn(mockProgramType);
        when(mockAllProgramTypes.getAll()).thenReturn(Arrays.<ProgramType>asList(
            new ProgramTypeBuilder().withShortCode("p").withProgramKey(ProgramType.PREGNANCY).withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build(),
            new ProgramTypeBuilder().withShortCode("c").withProgramKey(ProgramType.CHILDCARE).withProgramName("Child Care").withMinWeek(5).withMaxWeek(35).build()
        ));

        parser.parse("C 10", "mobileNumber");
        verify(mockProgramType).weekFor(10);
    }

    @Test
    public void shouldReturnNullOnInvalidMonthException() throws InvalidMonthException {
        ProgramType mockProgramType = mock(ProgramType.class);
        when(mockAllProgramTypes.findByCampaignShortCode("C")).thenReturn(mockProgramType);
        when(mockAllProgramTypes.getAll()).thenReturn(Arrays.<ProgramType>asList(
                new ProgramTypeBuilder().withShortCode("p").withProgramKey(ProgramType.PREGNANCY).withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build(),
                new ProgramTypeBuilder().withShortCode("c").withProgramKey(ProgramType.CHILDCARE).withProgramName("Child Care").withMinWeek(5).withMaxWeek(35).build()
        ));
        when(mockProgramType.weekFor(anyInt())).thenThrow(new InvalidMonthException(""));

        assertThat(parser.parse("C 50", "mobileNumber"), is(nullValue()));
    }


}

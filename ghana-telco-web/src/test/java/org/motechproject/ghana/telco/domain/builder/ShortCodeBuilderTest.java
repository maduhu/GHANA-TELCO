package org.motechproject.ghana.telco.domain.builder;

import org.junit.Test;
import org.motechproject.ghana.telco.domain.ShortCode;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ShortCodeBuilderTest {
    @Test
    public void ShouldBuildShortCode() {
        ShortCode shortCode = new ShortCodeBuilder().withShortCode("R").build();
        assertThat(shortCode.getCodes(), is(Arrays.asList("R")));
    }
}
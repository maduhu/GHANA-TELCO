package org.motechproject.ghana.telco.utils;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.ghana.telco.utils.Encrypt.*;

public class EncryptTest {

    @Test
    public void shouldEncryptAsSHA256() {
        assertThat(encrypt("admin"), is("8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"));
        assertThat(encrypt("admin1234$%^&*()!"), is("4632a5a8a5e1a5bd79a47dac97f3108ebe95ad1ea73773bc0c6bcfaaedd806cf"));
    }
}

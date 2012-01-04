package org.motechproject.ghana.telco.domain.builder;

import org.motechproject.ghana.telco.domain.ShortCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShortCodeBuilder extends Builder<ShortCode> {
    private List<String> codes;
    private String codeKey;

    public ShortCodeBuilder() {
        super(new ShortCode());
        this.codes = new ArrayList<String>();
    }

    public ShortCodeBuilder withShortCode(String... shortCodes) {
        codes.addAll(Arrays.asList(shortCodes));
        return this;
    }

    public ShortCodeBuilder withCodeKey(String codeKey) {
        this.codeKey = codeKey;
        return this;
    }
}
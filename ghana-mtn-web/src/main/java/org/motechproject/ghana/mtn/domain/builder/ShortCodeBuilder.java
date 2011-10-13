package org.motechproject.ghana.mtn.domain.builder;

import org.motechproject.ghana.mtn.domain.ShortCode;

import java.util.ArrayList;
import java.util.List;

public class ShortCodeBuilder extends Builder<ShortCode> {
    private List<String> codes;
    private String codeKey;

    public ShortCodeBuilder() {
        super(new ShortCode());
        this.codes = new ArrayList<String>();
    }

    public ShortCodeBuilder withShortCode(String shortCode) {
        codes.add(shortCode);
        return this;
    }

    public ShortCodeBuilder withCodeKey(String codeKey) {
        this.codeKey = codeKey;
        return this;
    }
}
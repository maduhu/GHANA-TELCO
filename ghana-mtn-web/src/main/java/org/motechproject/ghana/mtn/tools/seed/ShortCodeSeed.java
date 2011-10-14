package org.motechproject.ghana.mtn.tools.seed;

import org.motechproject.ghana.mtn.domain.ShortCode;
import org.motechproject.ghana.mtn.domain.builder.ShortCodeBuilder;
import org.motechproject.ghana.mtn.repository.AllShortCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShortCodeSeed extends Seed {
    @Autowired
    private AllShortCodes allShortCodes;

    @Override
    protected void load() {
        ShortCode relativeCode = new ShortCodeBuilder().withCodeKey(ShortCode.RELATIVE).withShortCode("R").withShortCode("r").build();
        ShortCode stopCode = new ShortCodeBuilder().withCodeKey(ShortCode.STOP).withShortCode("stop").build();
        allShortCodes.add(relativeCode);
        allShortCodes.add(stopCode);
    }
}

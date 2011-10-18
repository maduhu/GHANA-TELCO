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
        allShortCodes.add(new ShortCodeBuilder().withCodeKey(ShortCode.RELATIVE).withShortCode("R").withShortCode("r").build());
        allShortCodes.add(new ShortCodeBuilder().withCodeKey(ShortCode.STOP).withShortCode("stop").withShortCode("s").build());
        allShortCodes.add(new ShortCodeBuilder().withCodeKey(ShortCode.DELIVERY).withShortCode("d").withShortCode("dd").build());
    }
}

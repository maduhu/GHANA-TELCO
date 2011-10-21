package org.motechproject.ghana.mtn.tools.seed;

import org.motechproject.ghana.mtn.domain.builder.ShortCodeBuilder;
import org.motechproject.ghana.mtn.repository.AllShortCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.ghana.mtn.domain.ShortCode.*;

@Component
public class ShortCodeSeed extends Seed {
    @Autowired
    private AllShortCodes allShortCodes;

    @Override
    protected void load() {
        allShortCodes.add(new ShortCodeBuilder().withCodeKey(RELATIVE).withShortCode("R").withShortCode("r").build());
        allShortCodes.add(new ShortCodeBuilder().withCodeKey(STOP).withShortCode("stop").withShortCode("s").build());
        allShortCodes.add(new ShortCodeBuilder().withCodeKey(DELIVERY).withShortCode("d").withShortCode("dd").build());
        allShortCodes.add(new ShortCodeBuilder().withCodeKey(RETAIN_EXISTING_CHILDCARE_PROGRAM).withShortCode("e").build());
        allShortCodes.add(new ShortCodeBuilder().withCodeKey(USE_ROLLOVER_TO_CHILDCARE_PROGRAM).withShortCode("n").build());
    }
}

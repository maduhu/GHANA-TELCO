package org.motechproject.ghana.telco.parser;

import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.SMS;
import org.motechproject.ghana.telco.domain.ShortCode;
import org.motechproject.ghana.telco.repository.AllProgramTypes;
import org.motechproject.ghana.telco.repository.AllShortCodes;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.*;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public abstract class MessageParser {

    @Autowired
    protected AllProgramTypes allProgramTypes;

    @Autowired
    protected AllShortCodes allShortCodes;

    protected Pattern pattern;

    public abstract SMS parse(String message, String senderMobileNumber);

    public String getProgramCodePatterns() {
        return joinFrom(flatten(extract(allProgramTypes.getAll(), on(ProgramType.class).getShortCodes())), "|").toString();
    }

    protected Pattern pattern() {
        if (pattern == null) recompilePatterns();
        return pattern;
    }

    protected String shortCodePattern(String key) {
        ShortCode shortCode = allShortCodes.getShortCodeFor(key);
        return isNotEmpty(shortCode.getCodes()) ? join(shortCode.getCodes(), "|") : "";
    }

    public abstract void recompilePatterns();
}

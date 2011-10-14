package org.motechproject.ghana.mtn.parser;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.domain.ShortCode;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.motechproject.ghana.mtn.repository.AllShortCodes;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.*;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public abstract class MessageParser {
    
    protected AllProgramTypes allProgramTypes;
    protected Pattern pattern;

    @Autowired
    protected AllShortCodes allShortCodes;

    public MessageParser(AllProgramTypes allProgramTypes) {
        this.allProgramTypes = allProgramTypes;
    }

    public abstract SMS parse(String message, String senderMobileNumber);

    public String getProgramCodePatterns() {
        return joinFrom(flatten(extract(allProgramTypes.getAll(), on(ProgramType.class).getShortCodes())), "|").toString();
    }

    protected Pattern pattern() {
        if (pattern == null) recompilePatterns();
        return pattern;
    }

    protected String shortCodePattern(String key) {
        List<ShortCode> shortCodes = allShortCodes.getAllCodesFor(key);
        List<String> codes = shortCodes.get(0)  != null ? shortCodes.get(0).getCodes() : null;
        return isNotEmpty(codes) ? join(codes, "|") : "";
    }

    public abstract void recompilePatterns();
}

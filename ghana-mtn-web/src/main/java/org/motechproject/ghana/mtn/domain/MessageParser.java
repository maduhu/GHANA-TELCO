package org.motechproject.ghana.mtn.domain;

import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.*;

public abstract class MessageParser {
    
    protected AllProgramTypes allProgramTypes;
    protected Pattern pattern;

    public MessageParser(AllProgramTypes allProgramTypes) {
        this.allProgramTypes = allProgramTypes;
    }

    public abstract SMS parse(String message);

    public String getProgramCodePatterns() {
        return joinFrom(flatten(extract(allProgramTypes.getAll(), on(ProgramType.class).getShortCodes())), "|").toString();
    }

    protected Pattern pattern() {
        if (pattern == null) recompilePatterns();
        return pattern;
    }

    public abstract void recompilePatterns();
}

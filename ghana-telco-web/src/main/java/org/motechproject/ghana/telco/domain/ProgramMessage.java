package org.motechproject.ghana.telco.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.Arrays;

@TypeDiscriminator("doc.type === 'ProgramMessage'")
public class ProgramMessage extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "ProgramMessage";
    @JsonProperty
    private String messageKey;
    private String programKey;
    private String content;

    public ProgramMessage() {
    }

    public ProgramMessage(String messageKey, String programKey, String content) {
        this.messageKey = messageKey;
        this.programKey = programKey;
        this.content = content;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getProgramKey() {
        return programKey;
    }

    public void setProgramKey(String programKey) {
        this.programKey = programKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return StringUtils.join(Arrays.asList(programKey, messageKey, content), "|");
    }
}

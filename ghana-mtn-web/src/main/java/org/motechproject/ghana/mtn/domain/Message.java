package org.motechproject.ghana.mtn.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

@TypeDiscriminator("doc.type === 'Message'")
public class Message extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "Message";
    private String key;
    private String content;

    public Message() {
    }

    public Message(String key, String content) {
        this.key = key;
        this.content = content;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return join(asList(key, content), "|");
    }
}
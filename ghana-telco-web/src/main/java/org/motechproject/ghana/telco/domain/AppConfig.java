package org.motechproject.ghana.telco.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'AppConfig'")
public class AppConfig extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "AppConfig";
    @JsonIgnore
    public static final String WINDOW_START_TIME_KEY = "window.start.time.for.sending.sms";
    @JsonIgnore
    public static final String WINDOW_END_TIME_KEY = "window.end.time.for.sending.sms";

    @JsonProperty(value = "key")
    private String key;

    @JsonProperty(value = "value")
    private Object value;

    public AppConfig() {
    }

    public AppConfig(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object value() {
        return value;
    }

    public String getKey() {
        return key;
    }
}

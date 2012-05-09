package org.motechproject.ghana.telco.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.List;

@TypeDiscriminator("doc.type === 'TelcoUser'")
public class TelcoUser extends MotechBaseDataObject {
    @JsonProperty
    String userName;
    @JsonProperty
    String password;
    @JsonProperty
    List<String> roles;

    public TelcoUser() {
    }

    public TelcoUser(String userName, String password, List<String> roles) {
        this.userName = userName;
        this.password = password;
        this.roles = roles;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }
}

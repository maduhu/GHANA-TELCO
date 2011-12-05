package org.motechproject.ghana.mtn.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'AppConfig'")
public class AppConfig extends MotechAuditableDataObject {

}
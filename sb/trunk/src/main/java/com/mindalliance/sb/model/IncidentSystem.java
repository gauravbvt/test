package com.mindalliance.sb.model;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(identifierType = IncidentSystemPK.class, versionField = "", table = "incident_system")
@RooDbManaged(automaticallyDelete = true)
public class IncidentSystem implements PrintableObject {

    @Override
    public String toString() {
        return getAcronym();
    }
}

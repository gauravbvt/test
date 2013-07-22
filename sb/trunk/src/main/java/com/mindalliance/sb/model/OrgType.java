package com.mindalliance.sb.model;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "org_type", finders = { "findOrgTypesByNameEquals" })
public class OrgType {

    @Override
    @JsonValue
    public String toString() {
        return getName();
    }
}

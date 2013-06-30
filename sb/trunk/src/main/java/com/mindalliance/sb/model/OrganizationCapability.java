package com.mindalliance.sb.model;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(identifierType = OrganizationCapabilityPK.class, versionField = "", table = "organization_capability")
@RooDbManaged(automaticallyDelete = true)
public class OrganizationCapability {
}

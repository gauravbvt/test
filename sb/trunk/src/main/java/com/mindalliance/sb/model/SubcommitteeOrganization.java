package com.mindalliance.sb.model;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(identifierType = SubcommitteeOrganizationPK.class, versionField = "", table = "subcommittee_organization")
@RooDbManaged(automaticallyDelete = true)
public class SubcommitteeOrganization {
}

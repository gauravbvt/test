package com.mindalliance.sb.model;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(identifierType = RespondentSubcommitteePK.class, versionField = "", table = "respondent_subcommittee")
@RooDbManaged(automaticallyDelete = true)
public class RespondentSubcommittee {
}

package com.mindalliance.sb.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(identifierType = RespondentSubcommitteePK.class, versionField = "", table = "respondent_subcommittee")
@RooDbManaged(automaticallyDelete = true)
public class RespondentSubcommittee {

    private static final Logger LOG = LoggerFactory.getLogger(RespondentSubcommittee.class);

    public RespondentSubcommittee(Respondent respondent, Subcommittee subcommittee) {
        setSubcommittee(subcommittee);
        setRespondent(respondent);
        setId(new RespondentSubcommitteePK(respondent.getId(), subcommittee.getId()));
    }

    public void addSubcommitteeOrg(String orgName, boolean actual) {
        if (orgName != null) {
            LOG.debug("Marking {} as member of subcommittee {}", orgName, getSubcommittee().getName());
            SubcommitteeOrganization so = new SubcommitteeOrganization(this, Organization.findOrCreate(orgName, getRespondent().getSubmitted()));
            so.setActual(actual);
            so.persist();
        }
    }
}

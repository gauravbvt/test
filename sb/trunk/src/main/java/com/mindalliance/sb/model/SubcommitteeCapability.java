package com.mindalliance.sb.model;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(identifierType = SubcommitteeCapabilityPK.class, versionField = "", table = "subcommittee_capability")
@RooDbManaged(automaticallyDelete = true)
public class SubcommitteeCapability {

    private SubcommitteeCapability() {
    }

    public SubcommitteeCapability(RespondentSubcommittee rs, CoreCapability coreCapability) {
        setRespondentSubcommittee(rs);
        setCoreCapability(coreCapability);
        setId(new SubcommitteeCapabilityPK(rs.getRespondent().getId(), rs.getSubcommittee().getId(), coreCapability.getId()));
    }
}

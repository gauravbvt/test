package com.mindalliance.sb.model;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "respondent")
@RooDbManaged(automaticallyDelete = true)
public class Respondent {

    @Override
    public String toString() {
        return "Respondent{#" + getId() + ": " + getContactInfo() + '}';
    }

    public void setOrganization(Organization result) {
        getContactInfo().setOrganization( result );
    }

    public Organization getOrganization() {
        return getContactInfo().getOrganization();
    }
}

package com.mindalliance.sb.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

import java.util.Set;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "respondent")
@RooDbManaged(automaticallyDelete = true)
@JsonPropertyOrder({ "id", "contactInfo", "organization", "expertises", "submitted" })
public class Respondent implements PrintableObject {

    @Override
    public String toString() {
        return getContactInfo().toString();
    }

    public void setOrganization(Organization result) {
        getContactInfo().setOrganization(result);
    }

    public Organization getOrganization() {
        return getContactInfo().getOrganization();
    }

    public SuperbowlPlan getSuperbowlPlan() {
        Set<SuperbowlPlan> plans = getSuperbowlPlans();
        return plans.isEmpty() ? null : plans.iterator().next();
    }
}

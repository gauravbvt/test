package com.mindalliance.sb.model;

import java.util.Set;
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

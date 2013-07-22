package com.mindalliance.sb.model;

import java.util.List;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(identifierType = SuperbowlPlannerPK.class, versionField = "", table = "superbowl_planner", finders = { "findSuperbowlPlannersBySuperbowlPlanAndContactInfo" })
public class SuperbowlPlanner {

    private SuperbowlPlanner() {
    }

    public SuperbowlPlanner(ContactInfo contactInfo, SuperbowlPlan plan) {
        setContactInfo(contactInfo);
        setSuperbowlPlan(plan);
        setId(new SuperbowlPlannerPK(plan.getId(), contactInfo.getId()));
    }

    public static com.mindalliance.sb.model.SuperbowlPlanner findOrCreate(ContactInfo contactInfo, SuperbowlPlan plan) {
        List<SuperbowlPlanner> planners = findSuperbowlPlannersBySuperbowlPlanAndContactInfo(plan, contactInfo).getResultList();
        if (planners.isEmpty()) {
            SuperbowlPlanner planner = new SuperbowlPlanner(contactInfo, plan);
            planner.persist();
            return planner;
        } else return planners.get(0);
    }
}

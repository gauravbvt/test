package com.mindalliance.sb.model;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(versionField = "", table = "plan_file")
@RooDbManaged(automaticallyDelete = true)
public class PlanFile implements NamedObject {

    public long getLastModified() {
        return getSuperbowlPlan().getRespondent().getSubmitted().getTime();
    }
}

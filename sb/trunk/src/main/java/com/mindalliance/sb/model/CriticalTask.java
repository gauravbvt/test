package com.mindalliance.sb.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "critical_task")
@RooDbManaged(automaticallyDelete = true)
@JsonPropertyOrder({"description","coreCapability","criticalTasks"})
public class CriticalTask  implements PrintableObject {

    @Override
    public String toString() {
        return getDescription();
    }
}

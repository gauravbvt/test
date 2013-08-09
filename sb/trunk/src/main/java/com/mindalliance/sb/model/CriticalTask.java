package com.mindalliance.sb.model;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "critical_task")
@RooDbManaged(automaticallyDelete = true)
public class CriticalTask  implements PrintableObject {

    @Override
    public String toString() {
        return getDescription();
    }
}

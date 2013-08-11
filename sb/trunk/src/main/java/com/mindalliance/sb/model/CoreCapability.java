package com.mindalliance.sb.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "core_capability", finders = { "findCoreCapabilitysByNameEquals" } )
@JsonPropertyOrder({ "name","description","missionAreas","criticalTasks" })
public class CoreCapability implements NamedObject {

    @Override
    public String toString() {
        return getName();
    }
}

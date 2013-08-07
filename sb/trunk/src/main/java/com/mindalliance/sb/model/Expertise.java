package com.mindalliance.sb.model;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "expertise", finders = { "findExpertisesByNameEquals" })
public class Expertise implements NamedObject {

    @Override
    public String toString() {
        return "Expertise{" + getName() + '}';
    }
}

package com.mindalliance.sb.model;

import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "contact_info", finders = { "findContactInfoesByEmailEquals" })
public class ContactInfo {

    @Override
    public String toString() {
        return "ContactInfo{" + getPrefix() + ' ' + getFirstName() + ' ' + getLastName() + " <" + getEmail() + ">}";
    }
}

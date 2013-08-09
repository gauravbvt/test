package com.mindalliance.sb.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

import java.util.List;

@RooJavaBean
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "contact_info", finders = { "findContactInfoesByEmailEquals" })
@JsonPropertyOrder({ "firstName", "lastName", "organization", "title", "email", "landline" })
public class ContactInfo implements PrintableObject {

    public static com.mindalliance.sb.model.ContactInfo findOrCreate(String fullName, String title, String email, Organization organization) {
        if (email != null) {
            List<ContactInfo> contacts = findContactInfoesByEmailEquals(email).getResultList();
            if (!contacts.isEmpty()) return contacts.get(0);
        }
        ContactInfo result = new ContactInfo();
        result.setFullName(fullName);
        result.setTitle(title);
        result.setEmail(email);
        result.setOrganization(organization);
        result.persist();
        return result;
    }

    public static com.mindalliance.sb.model.ContactInfo findOrCreate(String firstName, String lastName, String title, String email, Organization organization) {
        if (email != null) {
            List<ContactInfo> contacts = findContactInfoesByEmailEquals(email).getResultList();
            if (!contacts.isEmpty()) return contacts.get(0);
        }
        ContactInfo result = new ContactInfo();
        result.setFirstName(firstName);
        result.setLastName(lastName);
        result.setTitle(title);
        result.setEmail(email);
        result.setOrganization(organization);
        result.persist();
        return result;
    }

    @Override
    public String toString() {
        String p = getPrefix() == null ? "" : getPrefix() + ' ';
        String fn = getFirstName() == null ? "" : getFirstName() + ' ';
        return p + fn + getLastName() + " <" + getEmail() + '>';
    }

    public void setFullName(String name) {
        if (name != null) {
            int start = name.indexOf(' ');
            int end = name.lastIndexOf(' ');
            if (start >= 0) {
                setFirstName(name.substring(0, end));
                setLastName(name.substring(end + 1, name.length()));
            } else setLastName(name);
        }
    }
}

package com.mindalliance.sb.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.roo.addon.dbre.RooDbManaged;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RooJavaBean
@RooDbManaged(automaticallyDelete = true)
@RooJpaActiveRecord(versionField = "", table = "organization", finders = { "findOrganizationsByNameEquals", "findOrganizationsByNameEqualsOrAcronymEquals" })
@RooJson
@JsonFilter("csvFilter")
@JsonPropertyOrder({ "id", "name", "acronym", "type", "added" })
public class Organization {

    private static final Logger LOG = LoggerFactory.getLogger(Organization.class);

    public static com.mindalliance.sb.model.Organization findOrCreate(String orgName, Date submitted) {
        if (orgName == null) throw new IllegalArgumentException("Organization name can't be null");
        Organization org;
        List<Organization> organizations = findOrganizationsByNameEqualsOrAcronymEquals(orgName, orgName).getResultList();
        if (organizations.isEmpty()) {
            LOG.debug("Creating organization {}", orgName);
            org = new Organization();
            org.setName(orgName);
            Calendar instance = Calendar.getInstance();
            instance.setTime(submitted);
            org.setAdded(instance);
            org.persist();
        } else org = organizations.get(0);
        return org;
    }

    public static long getLastModified() {
        return entityManager().createQuery("SELECT max(added) FROM Organization", Calendar.class).getSingleResult().getTimeInMillis();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append(getName());
        if (getAcronym() != null) builder.append(" (").append(getAcronym()).append(')');
        return builder.toString();
    }

    public ContactInfo addContact(String fullName, String title, String email) {
        LOG.debug("Adding contact {} <{}> in {}: {}", fullName, email, getName(), title);
        return ContactInfo.findOrCreate(fullName, title, email, this);
    }
}

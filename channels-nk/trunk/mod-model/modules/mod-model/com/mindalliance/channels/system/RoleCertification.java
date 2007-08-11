// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.definitions.Organization;
import com.mindalliance.channels.profiles.Person;
import com.mindalliance.channels.support.GUID;

/**
 * Certification that a person does in fact play given roles in an
 * organization.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class RoleCertification extends Certification {

    private Person person;
    private Organization organization;
    private List<GUID> roleGUIDs;

    /**
     * Default constructor.
     */
    public RoleCertification() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public RoleCertification( GUID guid ) {
        super( guid );
    }

    /**
     * Return the organization.
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Set the organization.
     * @param organization the organization to set
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    /**
     * Return the person.
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Set the person.
     * @param person the person to set
     */
    public void setPerson( Person person ) {
        this.person = person;
    }

    /**
     * Return the role guid list.
     */
    public synchronized List<GUID> getRoleGUIDs() {
        if ( roleGUIDs == null )
            roleGUIDs = new ArrayList<GUID>();
        return roleGUIDs;
    }

    /**
     * Set the role guid list.
     * @param roleGUIDs the roleGUIDs to set
     */
    public void setRoleGUIDs( List<GUID> roleGUIDs ) {
        this.roleGUIDs = roleGUIDs;
    }

    /**
     * Add a role guid.
     * @param guid the guid
     */
    public void addRoleGUID( GUID guid ) {
        getRoleGUIDs().add( guid );
    }

    /**
     * Remove a role guid.
     * @param guid the guid
     */
    public void removeRoleGUID( GUID guid ) {
        getRoleGUIDs().remove( guid );
    }
}

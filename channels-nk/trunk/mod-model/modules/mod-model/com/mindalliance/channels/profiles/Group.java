// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.profiles;

import java.util.List;

import com.mindalliance.channels.support.GUID;

/**
 * A number of interchangeable, anonymous persons with identical
 * accessibility and contact info.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 * @navassoc - - * Role
 */
public class Group extends ContactableResource {

    private List<Role> roles;
    private Integer minimumNumber;

    /**
     * Default constructor.
     */
    public Group() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Group( GUID guid ) {
        super( guid );
    }

    /**
     * Return the minimum number of participants in this group.
     */
    public Integer getMinimumNumber() {
        return minimumNumber;
    }

    /**
     * Set the minimum number of participants in this group.
     * @param number the number
     */
    public void setMinimumNumber( Integer number ) {
        this.minimumNumber = number;
    }

    /**
     * Return the roles associated with this group.
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Set the roles included in this group.
     * @param roles the roles
     */
    public void setRoles( List<Role> roles ) {
        this.roles = roles;
    }

    /**
     * Add a role.
     * @param role the role
     */
    public void addRole( Role role ) {
        roles.add( role );
    }

    /**
     * Remove a role.
     * @param role the role
     */
    public void removeRole( Role role ) {
        roles.remove( role );
    }
}

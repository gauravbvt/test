/*
 * Created on May 1, 2007
 */
package com.mindalliance.channels.data.elements.resources;

import java.util.List;

import com.mindalliance.channels.util.GUID;

/**
 * A number of interchangeable, anonymous persons with identical
 * accessibility and contact info.
 * 
 * @author jf
 */
public class Group extends ContactableResource {

    private List<Role> roles;
    private Integer number;

    public Group() {
        super();
    }

    public Group( GUID guid ) {
        super( guid );
    }

    /**
     * @return the number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber( Integer number ) {
        this.number = number;
    }

    /**
     * @return the roles
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles( List<Role> roles ) {
        this.roles = roles;
    }

    /**
     * @param role
     */
    public void addRole( Role role ) {
        roles.add( role );
    }

    /**
     * @param role
     */
    public void removeRole( Role role ) {
        roles.remove( role );
    }

}

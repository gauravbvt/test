// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.reference.TitleType;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * A position in an organization to be filled by one or more persons.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @navassoc * managers * Role
 */
public class Role extends AbstractJavaBean implements Comparable<Role> {

    private List<TitleType> titles = new ArrayList<TitleType>();
    private Set<Role> managers = new TreeSet<Role>();
    private Organization organization;
    private String name;

    /**
     * Default constructor.
     */
    public Role() {
        super();
    }

    //----------------------------------
    /**
     * Return the value of managers.
     */
    public Set<Role> getManagers() {
        return this.managers;
    }

    /**
     * Set the value of managers.
     * @param managers The new value of managers
     */
    public void setManagers( Set<Role> managers ) {
        this.managers = managers;
    }

    /**
     * Add a manager to this role.
     * @param manager the manager to add
     */
    public void addManager( Role manager ) {
        this.managers.add( manager );
    }

    /**
     * Remove a manager from this role.
     * @param manager the manager to remove
     */
    public void removeManage( Role manager ) {
        this.managers.remove( manager );
    }

    //----------------------------------
    /**
     * Return the value of titles.
     */
    public List<TitleType> getTitles() {
        return this.titles;
    }

    /**
     * Set the value of titles.
     * @param titles The new value of titles
     */
    public void setTitles( List<TitleType> titles ) {
        this.titles = titles;
    }

    /**
     * Add a title to this role.
     * @param title the title to add
     */
    public void addTitle( TitleType title ) {
        this.titles.add( title );
    }

    /**
     * Remove a title from this role.
     * @param title the title to remove
     */
    public void removeTitle( TitleType title ) {
        this.titles.remove( title );
    }

    /**
     * Return the value of organization.
     */
    public Organization getOrganization() {
        return this.organization;
    }

    /**
     * Set the value of organization.
     * @param organization The new value of organization
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    /**
     * Return the value of name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the value of name.
     * @param name The new value of name
     * @throws PropertyVetoException if new name clashes with other role within
     * the organization.
     */
    public void setName( String name ) throws PropertyVetoException {
        this.name = name;
    }

    /**
     * Compares this object with the specified object for order.
     * @param o the role to compare to.
     */
    public int compareTo( Role o ) {
        return getName().compareToIgnoreCase( o.getName() );
    }
}

// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.profiles;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.beanview.annotation.PropertyOptions;
import com.beanview.validation.WebsiteUrl;
import com.mindalliance.channels.User;
import com.mindalliance.channels.definitions.CategorySet;
import com.mindalliance.channels.definitions.Category.Taxonomy;
import com.mindalliance.channels.support.CollectionType;
import com.mindalliance.channels.support.GUID;

/**
 * A person.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @navassoc - - * Role
 * @navassoc - - 0..1 User
 */
public class Person extends ContactableResource {

    private String firstName;
    private String middleName;
    private String lastName;
    private URL photo;
    private List<Role> roles;
    private CategorySet clearances;
    private User user;

    /**
     * Default constructor.
     */
    public Person() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Person( GUID guid ) {
        super( guid );
        roles = new ArrayList<Role>();
    }

    /**
     * Return the first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the first name.
     * @param firstName the firstName to set
     */
    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    /**
     * Return the last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the last name.
     * @param lastName the lastName to set
     */
    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    /**
     * Return the middle name.
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Set the middle name.
     * @param middleName the middleName to set
     */
    public void setMiddleName( String middleName ) {
        this.middleName = middleName;
    }

    /**
     * Return the photo.
     */
    @PropertyOptions( ignore = true )
    @WebsiteUrl
    public URL getPhoto() {
        return photo;
    }

    /**
     * Set the photo.
     * @param photo the photo to set
     */
    public void setPhoto( URL photo ) {
        this.photo = photo;
    }

    /**
     * Return the roles.
     */
    @CollectionType( type = Role.class )
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Set the roles.
     * @param roles the roles to set
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

    /**
     * Return the value of clearances.
     */
    public CategorySet getClearances() {
        return clearances;
    }

    /**
     * Set the value of clearances.
     * @param clearances The new value of clearances
     */
    public void setClearances( CategorySet clearances ) {
        if ( clearances.getTaxonomy() != Taxonomy.Clearance )
            throw new IllegalArgumentException();

        this.clearances = clearances;
    }

    /**
     * Return the system user corresponding to this person.
     * @return null if there are no corresponding user.
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Set the user.
     * @param user the user
     */
    public void setUser( User user ) {
        this.user = user;
    }
}

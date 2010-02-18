// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import java.util.HashSet;
import java.util.Set;

/**
 * A user profile.
 *
 * Profile properties will be used in faceted searches.
 */
@Entity
public class Profile extends ModelObject {

    @OneToOne
    private User user;

    private String fullName;

    private String organisation;

    private String designation;

    private String location;

    private String phone;

    private String fax;

    private String website;

    @ManyToMany( cascade = { CascadeType.MERGE, CascadeType.PERSIST } )
    @JoinTable( name = "Profile_Interest",
                joinColumns = @JoinColumn( name = "profile_id" ),
                inverseJoinColumns = @JoinColumn( name = "tag_id" ) )
    @OrderBy( "description" )
    private Set<Tag> interests = new HashSet<Tag>();

    /**
     * Create a new Profile instance.
     */
    public Profile() {
    }

    /**
     * Create a new profile instance for a given user
     * @param user of type User
     */
    public Profile( User user ) {
        this.user = user;
    }

    /**
     * Return the user's full name.
     * @return the value of fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name of the associated user.
     * @param fullName the new full name.
     */
    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }

    /**
     * Return the profile's associated user.
     * @return the value of user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user of this profile.
     * @param user the new user value.
     */
    public void setUser( User user ) {
        this.user = user;
    }

    /**
     * Return the Profile's designation.
     * @return the value of designation
     */
    public String getDesignation() {

        return designation;
    }

    /**
     * Sets the designation of this Profile.
     * @param designation the new designation value.
     *
     */
    public void setDesignation( String designation ) {
        this.designation = designation;
    }

    /**
     * Return the Profile's fax.
     * @return the value of fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * Sets the fax of this Profile.
     * @param fax the new fax value.
     *
     */
    public void setFax( String fax ) {
        this.fax = fax;
    }

    /**
     * Return the Profile's interests.
     * @return the value of interests
     */
    public Set<Tag> getInterests() {
        return interests;
    }

    /**
     * Sets the interests of this Profile.
     * @param interests the new interests value.
     *
     */
    public void setInterests( Set<Tag> interests ) {
        this.interests = interests;
    }

    /**
     * Return the Profile's location.
     * @return the value of location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of this Profile.
     * @param location the new location value.
     *
     */
    public void setLocation( String location ) {
        this.location = location;
    }

    /**
     * Return the Profile's organisation.
     * @return the value of organisation
     */
    public String getOrganisation() {
        return organisation;
    }

    /**
     * Sets the organisation of this Profile.
     * @param organisation the new organisation value.
     *
     */
    public void setOrganisation( String organisation ) {
        this.organisation = organisation;
    }

    /**
     * Return the Profile's phone.
     * @return the value of phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone of this Profile.
     * @param phone the new phone value.
     *
     */
    public void setPhone( String phone ) {
        this.phone = phone;
    }

    /**
     * Return the Profile's website.
     * @return the value of website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the website of this Profile.
     * @param website the new website value.
     *
     */
    public void setWebsite( String website ) {
        this.website = website;
    }
}

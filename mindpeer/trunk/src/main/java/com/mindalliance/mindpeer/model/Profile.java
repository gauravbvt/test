// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A user profile.
 *
 * Profile properties will be used in faceted searches.
 */
@Entity
public class Profile extends NamedModelObject {

    private static final long serialVersionUID = -8519781448042452022L;

    @OneToOne
    private User user;

    private String organization;

    private String designation;

    private String location;

    private String phone;

    private String fax;

    private String website;

    @Column( length = 32*1024 )
    private byte[] picture;

    @ManyToMany( cascade = { CascadeType.MERGE, CascadeType.PERSIST } )
    @JoinTable( name = "Profile_Interest",
                joinColumns = @JoinColumn( name = "profile_id" ),
                inverseJoinColumns = @JoinColumn( name = "tag_id" ) )
    @OrderBy( "description" )
    private Set<Tag> interests = new HashSet<Tag>();

    @OneToMany( cascade = { CascadeType.MERGE }, mappedBy = "profile" )
    @OrderBy( "name" )
    @MapKey( name = "name" )
    private Map<String, Product> products = new HashMap<String, Product>();

    /**
     * Create a new Profile instance.
     */
    public Profile() {
    }

    /**
     * Create a new Profile instance.
     * @param name the given name
     */
    protected Profile( String name ) {
        super( name );
    }

    /**
     * Create a new Profile instance.
     *
     * @param user the given user
     */
    public Profile( User user ) {
        this( "Unknown" );
        this.user = user;
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
    public String getOrganization() {
        return organization;
    }

    /**
     * Sets the organisation of this Profile.
     * @param organization the new organisation value.
     *
     */
    public void setOrganization( String organization ) {
        this.organization = organization;
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

    /**
     * Return the Profile's picture.
     * @return the value of picture
     */
    public byte[] getPicture() {
        return picture;
    }

    /**
     * Sets the picture of this Profile.
     * @param picture the new picture value.
     *
     */
    public void setPicture( byte[] picture ) {
        this.picture = picture;
    }

    @Transient
    public List<Product> getProducts() {
        List<Product> result = new ArrayList<Product>( products.values() );
        Collections.sort( result );

        return Collections.unmodifiableList( result );
    }

    @Transient
    public Iterator<Product> products() {
        return products.values().iterator();
    }

    /**
     * ...
     *
     * @param product the given product
     */
    public void addProduct( Product product ) {
        product.setProfile( this );
        products.put( product.getName(), product );
    }

    /**
     * ...
     *
     * @param name the given name
     * @return AbstractProduct
     */
    public Product getProduct( String name ) {
        return products.get( name );
    }

    /**
     * Remove a product.
     *
     * @param productName the name of the product
     */
    public void removeProduct( String productName ) {
        products.remove( productName );
    }

    /**
     * Return the number of defined products.
     * @return the number of defined products
     */
    @Transient
    public int getProductCount() {
        return products.size();
    }

    @Transient
    public int getSubscriberCount() {
        int result = 0;
        for ( Product product : products.values() )
            result += product.getCount();

        return result;
    }

    /**
     * ...
     * @return String
     */
    @Override
    public String toString() {
        return "Profile[" + getId() + ':' + getName() + ']';
    }

    /**
     * Return the count to show in a listing.
     * @return the value of count
     */
    public int getCount() {
        return 0;
    }
}

/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A user account.
 */
@Entity
@Indexed
@Table( uniqueConstraints = @UniqueConstraint( columnNames = { "USERID", "PROVIDERID" } ) )
public class Account implements Serializable, Timestamped {

    private static final long serialVersionUID = 1861272453366746780L;

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private long id;

    @Basic( optional = false )
    private String providerId;

    @Basic( optional = false )
    private String userId;

    private String password;

    private String confirmation;

    private Date created;

    private Date lastModified;

    private boolean disabled;

    private boolean confirmed;

    private boolean viewByTags;

    private boolean showInactive;

    @OneToMany( mappedBy = "account", cascade = CascadeType.ALL )
    private List<Playbook> playbooks;

    @OneToMany( mappedBy = "account", cascade = CascadeType.ALL )
    private List<Contact> contacts;

    //------------------------------------------
    public Account() {
    }

    public Account( String providerId, String userId, Contact contact ) {
        this();

        this.providerId = providerId;
        this.userId = userId;
        created = new Date();

        lastModified = created;
        playbooks = new ArrayList<Playbook>();

        contacts = new ArrayList<Contact>();
        contact.setMain( true );
        addContact( contact );

        playbooks.add( new Playbook( this, contact ) );
    }

    /**
     * Get the value of userId.
     *
     * @return the value of userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set the value of password, SHA1 signature of actual password.
     *
     * @param password the new value of password
     */
    public void setPassword( String password ) {
        this.password = password;
    }

    /**
     * Get the value of password.
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the value of confirmation.
     *
     * @param newVar the new value of confirmation
     */
    public void setConfirmation( String newVar ) {
        confirmation = newVar;
    }

    /**
     * Get the value of confirmation.
     *
     * @return the value of confirmation
     */
    public String getConfirmation() {
        return confirmation;
    }

    /**
     * Get the date that this account was created.
     *
     * @return the value of created
     */
    public Date getCreated() {
        return created;
    }

    @Override
    public void setLastModified( Date date ) {
        this.lastModified = date;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Set the value of disabled
     *
     * @param disabled the new value of disabled
     */
    public void setDisabled( boolean disabled ) {
        this.disabled = disabled;
    }

    /**
     * Get the value of disabled.
     *
     * @return the value of disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    public List<Playbook> getPlaybooks() {
        return playbooks;
    }

    public void setPlaybooks( List<Playbook> playbooks ) {
        this.playbooks = playbooks;
    }

    /**
     * Return default playbook.
     *
     * @return the first playbook available.
     */
    @Transient
    public Playbook getPlaybook() {
        return playbooks.get( 0 );
    }

    public long getId() {
        return id;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts( List<Contact> contacts ) {
        this.contacts = contacts;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed( boolean confirmed ) {
        this.confirmed = confirmed;
    }

    public boolean isViewByTags() {
        return viewByTags;
    }

    public void setViewByTags( boolean viewByTags ) {
        this.viewByTags = viewByTags;
    }

    public boolean isShowInactive() {
        return showInactive;
    }

    public void setShowInactive( boolean showInactive ) {
        this.showInactive = showInactive;
    }

    public final Contact addContact( Contact contact ) {
        contact.setAccount( this );
        contacts.add( contact );
        return contact;
    }

    /**
     * Get the contact information of the owner of this account.
     *
     * @return a contact
     */
    @Transient
    public Contact getOwner() {
        return getPlaybook().getMe();
    }

    public String getProviderId() {
        return providerId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        Contact me = getOwner();

        sb.append( "account #" ).append( getId() );
        sb.append( " (" ).append( me.getFullName() ).append( ')' );
        return sb.toString();
    }

    @Transient
    public String getUserKey() {
        return providerId + ':' + userId;
    }

    @Override
    public boolean equals( Object obj ) {
        return this == obj 
            || obj != null 
               && Account.class.isAssignableFrom( obj.getClass() ) 
               && id == ( (Account) obj ).getId();
    }

    @Override
    public int hashCode() {
        return (int) ( id ^ id >>> 32 );
    }
}

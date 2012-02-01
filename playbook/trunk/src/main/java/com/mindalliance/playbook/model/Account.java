/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import org.hibernate.search.annotations.Indexed;

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
@Table(
    uniqueConstraints = @UniqueConstraint( columnNames = "EMAIL" )
)
public class Account implements Serializable, Timestamped {

    private static final long serialVersionUID = 1861272453366746780L;

    @Id @GeneratedValue( strategy = GenerationType.AUTO )
    private long id;

    private String email;

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

    public Account( String email, Date created ) {
        this();
        this.email = email;
        this.created = new Date( created.getTime() );
        
        lastModified = this.created;
        playbooks = new ArrayList<Playbook>();
        playbooks.add( new Playbook( this ) );
        
        contacts = new ArrayList<Contact>();        
    }

    /**
     * Set the value of email.
     *
     * @param email the new value of email
     */
    public void setEmail( String email ) {
        this.email = email;
    }

    /**
     * Get the value of email.
     *
     * @return the value of email
     */
    public String getEmail() {
        return email;
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

    /**
     * Set the value of lastModified.
     *
     * @param lastModified the new value of lastModified
     */
    @Override
    public void setLastModified( Date lastModified ) {
        this.lastModified = lastModified;
    }

    /**
     * Get the value of lastModified.
     *
     * @return the value of lastModified
     */
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
    
    public void addContact( Contact contact ) {
        contacts.add( contact );       
    }
}

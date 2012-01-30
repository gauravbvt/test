/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A playbook of an account.
 */
@Entity
public class Playbook implements Serializable {
    
    @Id @GeneratedValue
    private long id;

    private String email;

    @ManyToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private Contact me;

    @ManyToOne
    private Account account;
    
    @OneToMany( mappedBy = "playbook", cascade = CascadeType.ALL )
    private List<Play> plays;

    //
    // Constructors
    //
    public Playbook() {
        plays = new ArrayList<Play>();
    }

    public Playbook( Account account ) {
        this();
        this.account = account;
        email = account.getEmail();
        me = new Contact( account, email );
    }

    @Transient
    long getAccountId() {
        return account.getId();
    }

    /**
     * Set the value of email.
     *
     * @param newVar the new value of email
     */
    public void setEmail( String newVar ) {
        email = newVar;
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
     * Set the value of the contact information associated with this playbook.
     *
     * @param newVar the new value of me
     */
    public void setMe( Contact newVar ) {
        me = newVar;
    }

    /**
     * Get the value of me.
     *
     * @return the value of me
     */
    public Contact getMe() {
        return me;
    }

    public Account getAccount() {
        return account;
    }

    public long getId() {
        return id;
    }

    public List<Play> getPlays() {
        return plays;
    }

    public void setPlays( List<Play> plays ) {
        this.plays = plays;
    }
}

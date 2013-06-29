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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A playbook of an account.
 */
@Entity
public class Playbook implements Serializable {

    private static final long serialVersionUID = 8908306371796636571L;

    @Id @GeneratedValue
    private long id;

    @ManyToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private Contact me;

    @ManyToOne
    private Account account;
    
    @OneToMany( mappedBy = "playbook", cascade = CascadeType.ALL )
    private List<Play> plays;
    
    @OneToMany( mappedBy = "playbook", cascade = CascadeType.ALL )
    private List<ConfirmationReq> requests;

    //
    // Constructors
    //
    public Playbook() {
        plays = new ArrayList<Play>();
    }

    public Playbook( Account account, Contact me ) {
        this();
        this.account = account;
        this.me = me;
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
        return Collections.unmodifiableList( plays );
    }

    public List<ConfirmationReq> getRequests() {
        return Collections.unmodifiableList( requests );
    }
}

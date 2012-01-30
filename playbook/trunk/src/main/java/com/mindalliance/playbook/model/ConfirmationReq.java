/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * Request for confirmation of a collaboration step.
 */
@Entity
public class ConfirmationReq implements Serializable, Timestamped {

    private static final long serialVersionUID = 1171444045850660147L;

    @Id @GeneratedValue
    private long id;

    private String description;

    private Date date = new Date();

    private boolean pending = true;
    
    private boolean forwardable;
    
    @ManyToOne
    private Collaboration collaboration;
    
    @OneToOne( optional = true )
    private ConfirmationAck confirmation;

    //
    // Constructors
    //
    public ConfirmationReq() {
    }

    public ConfirmationReq( Collaboration collaboration ) {
        this.collaboration = collaboration;
    }

    public Collaboration getCollaboration() {
        return collaboration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending( boolean pending ) {
        this.pending = pending;
    }

    public ConfirmationAck getConfirmation() {
        return confirmation;
    }

    public void setConfirmation( ConfirmationAck confirmation ) {
        this.confirmation = confirmation;
    }

    @Transient
    @Override
    public Date getLastModified() {
        return new Date( date.getTime() );
    }

    @Override
    public void setLastModified( Date date ) {
        this.date = new Date( date.getTime() );
    }

    public boolean isForwardable() {
        return forwardable;
    }

    public void setForwardable( boolean forwardable ) {
        this.forwardable = forwardable;
    }
}

/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.CascadeType;
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

    @Id
    @GeneratedValue
    private long id;
    
    private String shortDescription;

    private String description;

    private Date date = new Date();

    private boolean forwardable;

    @OneToOne( optional = true )
    private Collaboration collaboration;

    @OneToOne( optional = true, cascade = CascadeType.REMOVE )
    private ConfirmationAck confirmation;

    @OneToOne( optional = true, cascade = CascadeType.REMOVE )
    private RedirectReq redirect;

    @ManyToOne
    private Playbook playbook;

    //
    // Constructors
    //
    public ConfirmationReq() {
    }

    public ConfirmationReq( Playbook playbook ) {
        this.playbook = playbook;
    }

    public ConfirmationReq( Collaboration collaboration ) {
        this( collaboration.getPlay().getPlaybook() );
        this.collaboration = collaboration;
    }

    /**
     * Get a short description of the collaboration.
     *
     * @return a description of who and how
     */
    public String getSummary() {
        Collaboration step = getCollaboration();
        if ( isRedirect() )
            return "Forwarding request on behalf of " + step.getOwner();
        else {
            Medium medium = step.getUsing();
            return ( medium == null ? "somehow" : medium.getDescription( !step.isSend() ) ) 
                   + ( shortDescription != null ? " when " + shortDescription : "" );
        }
    }

    public String getOrigin( boolean incoming ) {
        Contact recipient = getRecipient();
        return ( incoming ? "From " + getSender() 
                          : "To " + ( recipient == null ? "someone" : recipient ) ) 
               + ": ";
    }

    /**
     * Get the sender of the collaboration request.
     *
     * @return a contact information, local to the sender
     */
    @Transient
    public Contact getSender() {
        return playbook.getMe();
    }

    /**
     * Get the recipient of the redirect request.
     *
     * @return a local contact of the sender
     */
    public Contact getRecipient() {
        return collaboration.getWith();
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

    @Transient
    public boolean isPending() {
        return confirmation == null;
    }

    @Transient
    public boolean isRedirect() {
        return false;
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

    public RedirectReq getRedirect() {
        return redirect;
    }

    public void setRedirect( RedirectReq redirect ) {
        this.redirect = redirect;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription( String shortDescription ) {
        this.shortDescription = shortDescription;
    }

    public long getId() {
        return id;
    }

    public Playbook getPlaybook() {
        return playbook;
    }
}

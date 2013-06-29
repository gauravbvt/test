/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * A request for a redirect.
 */
@Entity
public class RedirectReq extends ConfirmationReq {

    private static final long serialVersionUID = 7424188101027819583L;

    @OneToOne( mappedBy = "redirect" )
    private ConfirmationReq originalRequest;

    @ManyToOne
    private Contact recipient;
    
    //
    // Constructors
    //
    public RedirectReq() {
    }

    public RedirectReq( Playbook playbook, Contact recipient, ConfirmationReq originalRequest, String description ) {
        super( playbook ); 
        this.originalRequest = originalRequest;
        this.recipient = recipient;
        setDescription( description );
    }

    @Override
    @Transient
    public Collaboration getCollaboration() {
        return originalRequest.getCollaboration();
    }

    @Override
    public Contact getRecipient() {
        return recipient;
    }

    public ConfirmationReq getOriginalRequest() {
        return originalRequest;
    }
    
    @Transient
    @Override
    public boolean isRedirect() {
        return true;
    }

    @Override
    public boolean isForwardable() {
        return originalRequest.isForwardable();
    }
}

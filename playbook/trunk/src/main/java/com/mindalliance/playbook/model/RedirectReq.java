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

    @OneToOne
    private ConfirmationReq originalRequest;

    @ManyToOne
    private Contact recipient;

    //
    // Constructors
    //
    public RedirectReq() {
    }

    public RedirectReq( ConfirmationReq originalRequest, Contact recipient ) {
        super( originalRequest.getCollaboration() );
        this.originalRequest = originalRequest;
        this.recipient = recipient;
    }

    /**
     * Get the sender of the redirection request.
     * @return a contact, local to the owner of the original request
     */
    @Override
    @Transient
    public Contact getSender() {
        return originalRequest.getCollaboration().getWith();
    }

    /**
     * Get the recipient of the redirect request.
     * @return a local contact of the sender
     */
    public Contact getRecipient() {
        return recipient;
    }

    public ConfirmationReq getOriginalRequest() {
        return originalRequest;
    }
}

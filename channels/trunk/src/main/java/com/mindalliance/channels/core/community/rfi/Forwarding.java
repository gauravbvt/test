package com.mindalliance.channels.core.community.rfi;

import com.mindalliance.channels.core.orm.model.AbstractPersistentPlanObject;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/21/12
 * Time: 8:25 PM
 */
@Entity
public class Forwarding extends AbstractPersistentPlanObject {
    
    private String toUsername;
    private String message = "";

    @ManyToOne
    private RFI rfi;

    public Forwarding() {}

    public Forwarding( String planUri, String fromUsername, String toUsername ) {
        super( fromUsername, planUri );
        this.toUsername = toUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername( String toUsername ) {
        this.toUsername = toUsername;
    }

    public RFI getRfi() {
        return rfi;
    }

    public void setRfi( RFI rfi ) {
        this.rfi = rfi;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }
}

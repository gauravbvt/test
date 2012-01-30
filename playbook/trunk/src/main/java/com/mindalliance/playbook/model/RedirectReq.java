/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.Entity;

/**
 * A request for a redirect.
 */
@Entity
public class RedirectReq extends ConfirmationReq {

    //
    // Constructors
    //
    public RedirectReq() {
    }
}

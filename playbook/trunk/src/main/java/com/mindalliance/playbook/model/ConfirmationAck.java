/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * A response to a request.
 */
@Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn( discriminatorType = DiscriminatorType.STRING )
abstract public class ConfirmationAck {

    @Id @GeneratedValue
    private long id;

    private Date date;

    @OneToOne( mappedBy = "confirmation", optional = false )
    private ConfirmationReq request;

    //
    // Constructors
    //
    public ConfirmationAck() {
    }

    protected ConfirmationAck( ConfirmationReq request ) {
        this.request = request;
    }

    public Date getDate() {
        return date;
    }

    public void setDate( Date date ) {
        this.date = date;
    }

    public ConfirmationReq getRequest() {
        return request;
    }

    public long getId() {
        return id;
    }
}

// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.model;

import org.hibernate.search.annotations.Field;

import javax.persistence.Entity;

/**
 * A medium with a string address.
 */
@Entity
public abstract class GenericMedium extends Medium {

    private static final long serialVersionUID = 6697525780963228187L;

    private String address;

    protected GenericMedium() {
    }
    
    protected GenericMedium( Contact contact, GenericMedium medium ) {
        this( contact, medium.getType(), medium.getAddress() );
    }

    protected GenericMedium( Contact contact, String type, String address ) {
        super( contact, type );

        if ( address == null )
            throw new IllegalArgumentException();
        this.address = address;
    }

    @Field
    @Override
    public String getAddress() {
        return address;
    }
}

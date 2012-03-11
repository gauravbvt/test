// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.model;

import javax.persistence.Entity;

/**
 * Instant messaging medium.
 */
@Entity
public class IMMedium extends GenericMedium {

    private static final long serialVersionUID = -4413077667745481499L;

    public IMMedium() {
    }

    public IMMedium( Contact contact, IMMedium medium ) {
        super( contact, medium );
    }

    public IMMedium( String type, String address ) {
        super( null, type, address );
    }

    @Override
    public MediumType getMediumType() {
        return MediumType.IM;
    }

    @Override
    public String getCssClass() {
        return "m-im";
    }
}

// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.model;

import javax.persistence.Entity;

/**
 * A Twitter connection information
 */
@Entity
public class TwitterMedium extends GenericMedium {

    private static final long serialVersionUID = 919809855910319053L;

    public TwitterMedium() {
    }

    public TwitterMedium( String twitterId ) {
        super( null, null, twitterId );
    }

    public TwitterMedium( Contact contact, TwitterMedium medium ) {
        super( contact, medium );
    }

    @Override
    public MediumType getMediumType() {
        return MediumType.TWITTER;
    }

    @Override
    public String getCssClass() {
        return "m-twitter";
    }
    
    @Override
    public String toString() {
        return '@' + getAddress();
    }

    @Override
    public String getActionUrl() {
        return "https://twitter.com/#!/" + getAddress();
    }
}

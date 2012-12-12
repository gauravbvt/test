package com.mindalliance.channels.core.dao.user;

import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/** A user's contact info
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/13/12
 * Time: 11:00 AM
 */
@Entity
public class UserContactInfo extends AbstractPersistentChannelsObject {

    @ManyToOne
    private ChannelsUserInfo user;
    private long transmissionMediumId;

    @Column(length=1000)
    private String address;

    public UserContactInfo() {}

    public UserContactInfo( String username, ChannelsUserInfo user ) {
        super( username );
        this.user = user;
    }

    public UserContactInfo( String username, ChannelsUserInfo user, Channel channel ) {
        super( username );
        this.user = user;
        this.transmissionMediumId = channel.getMedium().getId();
        this.address = channel.getAddress();
    }

    public ChannelsUserInfo getUser() {
        return user;
    }

    public void setUser( ChannelsUserInfo user ) {
        this.user = user;
    }

    public long getTransmissionMediumId() {
        return transmissionMediumId;
    }

    public void setTransmissionMediumId( long transmissionMediumId ) {
        this.transmissionMediumId = transmissionMediumId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }
}

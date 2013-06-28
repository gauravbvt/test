package com.mindalliance.channels.db.data;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.util.ChannelsUtils;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/19/13
 * Time: 1:20 PM
 */
public class ContactInfo implements Serializable {

    private long transmissionMediumId;
    private String address;

    public ContactInfo() {
    }

    public ContactInfo( Channel channel ) {
        this.transmissionMediumId = channel.getMedium().getId();
        this.address = channel.getAddress();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public long getTransmissionMediumId() {
        return transmissionMediumId;
    }

    public void setTransmissionMediumId( long transmissionMediumId ) {
        this.transmissionMediumId = transmissionMediumId;
    }

    public boolean isValid( CommunityService communityService ) {
        Channel channel = asChannel( communityService );
        return channel != null && channel.isValid();
    }

    public Channel asChannel( CommunityService communityService ) {
        try {
            TransmissionMedium medium = communityService.find(
                    TransmissionMedium.class,
                    transmissionMediumId );
            return new Channel( medium, address );
        } catch ( NotFoundException e ) {
            return null;
        }
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof ContactInfo ) {
            ContactInfo other = (ContactInfo)object;
            return transmissionMediumId == other.getTransmissionMediumId()
                    && ChannelsUtils.areEqualOrNull( address, other.getAddress() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash + 31 * new Long( transmissionMediumId ).hashCode();
        if ( address != null )
            hash = hash + 31 * address.hashCode();
        return hash;
    }

}

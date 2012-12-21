package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.orm.model.AbstractPersistentChannelsObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Organization contact info.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/12
 * Time: 2:51 PM
 */
@Entity
public class OrganizationContactInfo extends AbstractPersistentChannelsObject {

    @ManyToOne
    private RegisteredOrganization registeredOrganization;
    private long transmissionMediumId;
    @Column(length=1000)
    private String address;

    public OrganizationContactInfo() {

    }

    public OrganizationContactInfo(
            String username,
            RegisteredOrganization registeredOrganization,
            PlanCommunity planCommunity ) {
        super( planCommunity.getUri(), planCommunity.getPlanUri(), planCommunity.getPlanVersion(), username );
        this.registeredOrganization = registeredOrganization;
    }

    public OrganizationContactInfo(
            String username,
            RegisteredOrganization registeredOrganization,
            Channel channel,
            PlanCommunity planCommunity ) {
        this( username, registeredOrganization, planCommunity );
        this.transmissionMediumId = channel.getMedium().getId();
        this.address = channel.getAddress();
    }


    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public RegisteredOrganization getRegisteredOrganization() {
        return registeredOrganization;
    }

    public long getTransmissionMediumId() {
        return transmissionMediumId;
    }

    public void setTransmissionMediumId( long transmissionMediumId ) {
        this.transmissionMediumId = transmissionMediumId;
    }

    public boolean isValid( PlanCommunity planCommunity ) {
        Channel channel = asChannel( planCommunity );
        return channel != null && channel.isValid();
    }

    public Channel asChannel( PlanCommunity planCommunity ) {
        try {
            TransmissionMedium medium = planCommunity.getPlanService()
                    .find( TransmissionMedium.class, transmissionMediumId );
            return new Channel( medium, address );
        } catch ( NotFoundException e ) {
            return null;
        }
    }
}

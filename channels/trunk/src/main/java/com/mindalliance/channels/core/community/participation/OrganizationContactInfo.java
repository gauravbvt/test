package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.PlanCommunity;
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
}

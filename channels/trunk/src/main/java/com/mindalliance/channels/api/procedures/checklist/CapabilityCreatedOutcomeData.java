package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.CapabilityCreatedOutcome;
import com.mindalliance.channels.core.model.checklist.Outcome;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/25/13
 * Time: 1:00 PM
 */
@XmlType( name = "capabilityCreatedOutcome", propOrder = {"label", "infoCapability"} )

public class CapabilityCreatedOutcomeData extends OutcomeData {

    private InfoCapabilityData infoCapability;

    public CapabilityCreatedOutcomeData() {
        // required
    }

    public CapabilityCreatedOutcomeData( Outcome outcome,
                                         String serverUrl,
                                         CommunityService communityService,
                                         ChannelsUser user ) {
        super( outcome, serverUrl, communityService, user );
    }

    @Override
    protected void initData( Outcome outcome,
                             String serverUrl,
                             CommunityService communityService,
                             ChannelsUser user ) {
        super.initData( outcome, serverUrl, communityService, user );
        infoCapability = new InfoCapabilityData(
                serverUrl,
                ( (CapabilityCreatedOutcome) outcome ).getInfoCapability(),
                communityService );
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

    @XmlElement
    public InfoCapabilityData getInfoCapability() {
        return infoCapability;
    }

    @Override
    public Set<Long> allActorIds() {
        return infoCapability.allActorIds();
    }

    @Override
    public Set<Long> allOrganizationIds() {
        return infoCapability.allOrganizationIds();
    }

    @Override
    public Set<Long> allPlaceIds() {
        return infoCapability.allPlaceIds();
    }

    @Override
    public Set<Long> allRoleIds() {
        return infoCapability.allRoleIds();
    }


}

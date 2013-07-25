package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.entities.InformationData;
import com.mindalliance.channels.api.procedures.TimeDelayData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.InfoCapability;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/25/13
 * Time: 2:52 PM
 */
@XmlType(name = "infoCapability", propOrder = {"info", "targetSpec", "maxDelay"})
public class InfoCapabilityData implements Serializable {

    private InformationData info;
    private ResourceSpecData targetSpec;
    private TimeDelayData maxDelay;


    public InfoCapabilityData() {
        // required
    }

    public InfoCapabilityData( String serverUrl,
                               InfoCapability infoCapability,
                               CommunityService communityService ) {
        info = new InformationData( serverUrl, infoCapability.getInformation(), communityService );
        targetSpec = new ResourceSpecData( serverUrl,infoCapability.getTargetSpec(), communityService );
        maxDelay = new TimeDelayData( infoCapability.getMaxDelay() );
    }

    @XmlElement
    public InformationData getInfo() {
        return info;
    }

    public void setInfo( InformationData info ) {
        this.info = info;
    }

    @XmlElement
    public TimeDelayData getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay( TimeDelayData maxDelay ) {
        this.maxDelay = maxDelay;
    }

    @XmlElement
    public ResourceSpecData getTargetSpec() {
        return targetSpec;
    }

    public void setTargetSpec( ResourceSpecData targetSpec ) {
        this.targetSpec = targetSpec;
    }

    public Set<Long> allActorIds() {
        return targetSpec.allActorIds();
    }

    public Set<Long> allOrganizationIds() {
        return targetSpec.allOrganizationIds();
    }

    public Set<Long> allPlaceIds() {
        return targetSpec.allPlaceIds();
    }

    public Set<Long> allRoleIds() {
        return targetSpec.allRoleIds();
    }


}

package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.entities.InformationData;
import com.mindalliance.channels.api.procedures.TimeDelayData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.InfoNeed;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/25/13
 * Time: 11:17 AM
 */
@XmlType(name = "infoNeed", propOrder = {"info", "sourceSpec", "maxDelay"})
public class InfoNeedData implements Serializable {

    private InformationData info;
    private ResourceSpecData sourceSpec;
    private TimeDelayData maxDelay;

    public InfoNeedData() {
        // required
    }

    public InfoNeedData( String serverUrl,
                         InfoNeed infoNeed,
                         CommunityService communityService ) {
        info = new InformationData( serverUrl, infoNeed.getInformation(), communityService );
        sourceSpec = new ResourceSpecData( serverUrl,infoNeed.getSourceSpec(), communityService );
        maxDelay = new TimeDelayData( infoNeed.getMaxDelay() );
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
    public ResourceSpecData getSourceSpec() {
        return sourceSpec;
    }

    public void setSourceSpec( ResourceSpecData sourceSpec ) {
        this.sourceSpec = sourceSpec;
    }

    public Set<Long> allActorIds() {
        return sourceSpec.allActorIds();
    }

    public Set<Long> allOrganizationIds() {
        return sourceSpec.allOrganizationIds();
    }

    public Set<Long> allPlaceIds() {
        return sourceSpec.allPlaceIds();
    }

    public Set<Long> allRoleIds() {
        return sourceSpec.allRoleIds();
    }
}

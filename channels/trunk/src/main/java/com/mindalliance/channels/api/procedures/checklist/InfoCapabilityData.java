package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.entities.InformationData;
import com.mindalliance.channels.api.procedures.TimeDelayData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.InfoCapability;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/25/13
 * Time: 2:52 PM
 */
@XmlType(name = "infoCapability", propOrder = {"info", "maxDelay"})
public class InfoCapabilityData implements Serializable {

    private InformationData info;
    private TimeDelayData maxDelay;


    public InfoCapabilityData() {
        // required
    }

    public InfoCapabilityData( String serverUrl,
                               InfoCapability infoCapability,
                               CommunityService communityService ) {
        info = new InformationData( serverUrl, infoCapability.getInformation(), communityService );
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


}

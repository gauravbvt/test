package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;

/**
 * Web Service data element for a discovery made by an agent.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 12:49 PM
 */
public class DiscoveryData {

    private PlanService planService;
    private PlanParticipationService planParticipationService;
    private ChannelsUser user;
    private Flow notificationToSelf;

    public DiscoveryData() {
        // required
    }


    public DiscoveryData( Flow notificationToSelf,
                          PlanService planService,
                          PlanParticipationService planParticipationService,
                          ChannelsUser user ) {
        this.notificationToSelf = notificationToSelf;
        this.planService = planService;
        this.planParticipationService = planParticipationService;
        this.user = user;
    }


    @XmlElement
    public InfoDiscoveredData getInformationDiscovered() {
        if ( notificationToSelf != null )
            return new InfoDiscoveredData( notificationToSelf, planService, planParticipationService, user );
        else
            return null;
    }

}

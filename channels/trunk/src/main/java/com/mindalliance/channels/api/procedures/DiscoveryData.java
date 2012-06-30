package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.query.QueryService;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * Web Service data element for a discovery made by an agent.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 12:49 PM
 */
public class DiscoveryData  implements Serializable {

    private ChannelsUser user;
    private Commitment commitmentToSelf;
    private InfoDiscoveredData infoDiscoveredData;
    private TaskData followUpTask;

    public DiscoveryData() {
        // required
    }


    public DiscoveryData( Commitment commitmentToSelf,
                          QueryService queryService,
                          PlanParticipationService planParticipationService,
                          ChannelsUser user ) {
        this.commitmentToSelf = commitmentToSelf;
        this.user = user;
        initData( queryService, planParticipationService );
    }

    private void initData( QueryService planService, PlanParticipationService planParticipationService ) {
        if ( commitmentToSelf != null ) {
            infoDiscoveredData = new InfoDiscoveredData( commitmentToSelf, planService, planParticipationService, user );
            followUpTask = new TaskData(
                    commitmentToSelf.getBeneficiary(),
                    planService,
                    planParticipationService,
                    user );
        }
        else
            infoDiscoveredData = null;
    }


    @XmlElement
    public InfoDiscoveredData getInformationDiscovered() {
        return infoDiscoveredData;
    }

    public Assignment getDiscoveringAssignment() {
        return commitmentToSelf.getCommitter();
    }

    public TaskData getFollowUpTask() {
          return followUpTask;
    }
}

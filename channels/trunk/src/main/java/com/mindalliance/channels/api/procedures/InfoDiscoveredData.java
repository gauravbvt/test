package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.query.QueryService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 1:18 PM
 */
@XmlType( propOrder = {"information", "doingTask", "followUpTask", "documentation"} )
public class InfoDiscoveredData  implements Serializable {

    private Commitment commitmentToSelf;
    private ChannelsUser user;
    private TaskData doingTaskData;
    private TaskData followUpTaskData;

    public InfoDiscoveredData() {
        // required
    }

    public InfoDiscoveredData( Commitment commitmentToSelf,
                               QueryService queryService,
                               PlanParticipationService planParticipationService,
                               ChannelsUser user ) {
        this.commitmentToSelf = commitmentToSelf;
        this.user = user;
        initData( queryService, planParticipationService );
    }

    private void initData( QueryService queryService, PlanParticipationService planParticipationService ) {
        doingTaskData = new TaskData(
                commitmentToSelf.getCommitter(),
                queryService,
                planParticipationService,
                user );
        followUpTaskData = new TaskData(
                commitmentToSelf.getBeneficiary(),
                queryService,
                planParticipationService,
                user );
    }

    @XmlElement
    public InformationData getInformation() {
        return new InformationData( commitmentToSelf.getSharing() );
    }

    @XmlElement
    public TaskData getDoingTask() {
        return doingTaskData;
    }

    @XmlElement
    public TaskData getFollowUpTask() {
        return followUpTaskData;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return new DocumentationData( commitmentToSelf.getSharing() );
    }

}

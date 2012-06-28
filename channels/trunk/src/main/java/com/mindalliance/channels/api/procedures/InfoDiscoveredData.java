package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.PlanService;

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

    private Flow notificationToSelf;
    private ChannelsUser user;
    private TaskData doingTaskData;
    private TaskData followUpTaskData;

    public InfoDiscoveredData() {
        // required
    }

    public InfoDiscoveredData( Flow notificationToSelf,
                               PlanService planService,
                               PlanParticipationService planParticipationService,
                               ChannelsUser user ) {
        this.notificationToSelf = notificationToSelf;
        this.user = user;
        initData( planService, planParticipationService );
    }

    private void initData( PlanService planService, PlanParticipationService planParticipationService ) {
        doingTaskData = new TaskData(
                (Part)notificationToSelf.getSource(),
                planService,
                planParticipationService,
                user );
        followUpTaskData = new TaskData(
                (Part)notificationToSelf.getTarget(),
                planService,
                planParticipationService,
                user );
    }

    @XmlElement
    public InformationData getInformation() {
        return new InformationData( notificationToSelf );
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
        return new DocumentationData( notificationToSelf );
    }

}

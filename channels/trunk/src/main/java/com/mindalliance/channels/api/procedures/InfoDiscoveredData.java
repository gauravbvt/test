package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.participation.PlanParticipationService;
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

    private Flow notificationToSelf;
    private ChannelsUser user;
    private TaskData doingTaskData;
    private TaskData followUpTaskData;
    private DocumentationData documentation;

    public InfoDiscoveredData() {
        // required
    }

    public InfoDiscoveredData(
            String serverUrl,
            Flow notificationToSelf,
                               QueryService queryService,
                               PlanParticipationService planParticipationService,
                               ChannelsUser user ) {
        this.notificationToSelf = notificationToSelf;
        this.user = user;
        initData( serverUrl, queryService, planParticipationService );
    }

    private void initData( String serverUrl, QueryService queryService, PlanParticipationService planParticipationService ) {
        doingTaskData = new TaskData(
                serverUrl,
                (Part)notificationToSelf.getSource(),
                queryService,
                planParticipationService,
                user );
        followUpTaskData = new TaskData(
                serverUrl,
                (Part)notificationToSelf.getTarget(),
                queryService,
                planParticipationService,
                user );
        documentation = new DocumentationData( serverUrl, notificationToSelf );
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
        return documentation;
    }

}

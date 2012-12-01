package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;

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
            PlanCommunity planCommunity,
            Flow notificationToSelf,
                               ChannelsUser user ) {
        this.notificationToSelf = notificationToSelf;
        this.user = user;
        initData( serverUrl, planCommunity );
    }

    private void initData( String serverUrl, PlanCommunity planCommunity ) {
        doingTaskData = new TaskData(
                serverUrl,
                planCommunity,
                (Part)notificationToSelf.getSource(),
                user );
        followUpTaskData = new TaskData(
                serverUrl,
                planCommunity,
                (Part)notificationToSelf.getTarget(),
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

package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.community.CommunityService;
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
            CommunityService communityService,
            Flow notificationToSelf,
                               ChannelsUser user ) {
        this.notificationToSelf = notificationToSelf;
        this.user = user;
        initData( serverUrl, communityService );
    }

    private void initData( String serverUrl, CommunityService communityService ) {
        doingTaskData = new TaskData(
                serverUrl,
                communityService,
                (Part)notificationToSelf.getSource(),
                user );
        followUpTaskData = new TaskData(
                serverUrl,
                communityService,
                (Part)notificationToSelf.getTarget(),
                user );
        documentation = new DocumentationData( serverUrl, notificationToSelf );
    }

    @XmlElement
    public SharedInformationData getInformation() {
        return new SharedInformationData( notificationToSelf );
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

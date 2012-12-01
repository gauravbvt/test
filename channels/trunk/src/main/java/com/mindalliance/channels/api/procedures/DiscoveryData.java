package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;

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
public class DiscoveryData implements Serializable {

    private ChannelsUser user;
    private Flow notificationToSelf;
    private InfoDiscoveredData infoDiscoveredData;
    private TaskData followUpTask;

    public DiscoveryData() {
        // required
    }


    public DiscoveryData(
            String serverUrl,
            PlanCommunity planCommunity,
            Flow notificationToSelf,
            ChannelsUser user ) {
        this.notificationToSelf = notificationToSelf;
        this.user = user;
        initData( serverUrl, planCommunity );
    }

    private void initData( String serverUrl, PlanCommunity planCommunity ) {
        if ( notificationToSelf != null ) {
            infoDiscoveredData = new InfoDiscoveredData( serverUrl, planCommunity, notificationToSelf, user );
            followUpTask = new TaskData(
                    serverUrl,
                    planCommunity,
                    (Part) notificationToSelf.getTarget(),
                    user );
        } else
            infoDiscoveredData = null;
    }


    @XmlElement
    public InfoDiscoveredData getInformationDiscovered() {
        return infoDiscoveredData;
    }

    public TaskData getFollowUpTask() {
        return followUpTask;
    }

    public Part getDiscoveringPart() {
        return (Part) notificationToSelf.getSource();
    }
}

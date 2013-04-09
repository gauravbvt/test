package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web service data element for a follow up task.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/8/13
 * Time: 9:00 PM
 */
@XmlType( propOrder = {"information", "maxDelay", "followUpTask", "instructions",
        "failureImpact", "documentation"} )
public class FollowUpData  extends AbstractProcedureElementData {

    private Flow notificationToSelf;
    private TaskData followUpTaskData;
    private String failureImpact;
    private DocumentationData documentation;

    public FollowUpData() {
        // required
    }

    public FollowUpData( String serverUrl,
                         CommunityService communityService,
                         Flow notificationToSelf,
                         CommunityAssignment assignment,
                         ChannelsUser user ) {
        super( communityService, assignment, user );
        this.notificationToSelf = notificationToSelf;
        initData( serverUrl, communityService );
    }

    private void initData( String serverUrl, CommunityService communityService ) {
        initFollowUpTask( serverUrl, communityService );
        failureImpact = communityService.getPlanService().computeSharingPriority( getSharing() ).getNegativeLabel();
        documentation =  new DocumentationData( serverUrl, notificationToSelf );
    }

    private void initFollowUpTask( String serverUrl, CommunityService communityService ) {
        followUpTaskData = new TaskData(
                serverUrl,
                communityService,
                (Part)notificationToSelf.getTarget(),
                getUser() );
    }

    @XmlElement
    public InformationData getInformation() {
        return new InformationData( getSharing() );
    }

    @XmlElement
    public TimeDelayData getMaxDelay() {
        return new TimeDelayData( getSharing().getMaxDelay() );
    }

    @XmlElement
    public TaskData getFollowUpTask() {
        return followUpTaskData;
    }


    @XmlElement
    public String getInstructions() {
        String instructions = getSharing().getDescription();
        return instructions == null
                ? null
                : StringEscapeUtils.escapeXml( instructions );
    }

    @XmlElement
    public String getFailureImpact() {
        return failureImpact;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return documentation;
    }

    private Flow getSharing() {
        return notificationToSelf;
    }

}

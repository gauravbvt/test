package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Flow;
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
@XmlType( propOrder = {"information", "maxDelay", "followUpAssignment", "instructions",
        "failureImpact", "documentation"} )
public class FollowUpData  extends AbstractProcedureElementData {

    private Flow notificationToSelf;
    private AssignmentData followUpAssignmentData;
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
        initData( serverUrl, communityService, user );
    }

    private void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        initFollowUpTask( serverUrl, communityService, user );
        failureImpact = communityService.getModelService().computeSharingPriority( getSharing() ).getNegativeLabel();
        documentation =  new DocumentationData( serverUrl, notificationToSelf );
    }

    private void initFollowUpTask( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        CommunityCommitments allCommitments = communityService.getAllCommitments( true );   // include commitments to self
        allCommitments.committing( getAssignment() );
        followUpAssignmentData = new AssignmentData(
                serverUrl,
                getAssignment(),
                allCommitments.benefiting( getAssignment() ),
                allCommitments.committing( getAssignment() ),
                communityService,
                user
        );
/*
        followUpTaskData = new TaskData(
                serverUrl,
                communityService,
                (Part)notificationToSelf.getTarget(),
                getUser() );
*/
    }

    @XmlElement
    public SharedInformationData getInformation() {
        return new SharedInformationData( getSharing() );
    }

    @XmlElement
    public TimeDelayData getMaxDelay() {
        return new TimeDelayData( getSharing().getMaxDelay() );
    }

    @XmlElement
    public AssignmentData getFollowUpAssignment() {
        return followUpAssignmentData;
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

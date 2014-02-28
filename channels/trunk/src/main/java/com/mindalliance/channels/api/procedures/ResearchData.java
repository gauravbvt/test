package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Web service data element for a request to oneself.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/12/11
 * Time: 8:49 PM
 */
@XmlType( propOrder = {"information", "maxDelay", "researchAssignment", "consumingTask", "instructions",
        "failureImpact", "documentation"} )
public class ResearchData extends AbstractProcedureElementData {

    private Flow requestToSelf;
    private AssignmentData researchAssignmentData;
    private TaskData consumingTaskData;
    private String failureImpact;
    private DocumentationData documentation;

    public ResearchData() {
        // required
    }

    public ResearchData(
            String serverUrl,
            CommunityService communityService,
            Flow requestToSelf,
            CommunityAssignment assignment,
            ChannelsUser user ) {
        super( communityService, assignment, user );
        this.requestToSelf = requestToSelf;
        initData( serverUrl, communityService, user );
    }

    private void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        initResearchAssignment( serverUrl, communityService, user );
        initConsumingTaskData( serverUrl, communityService );
        failureImpact = communityService.getModelService().computeSharingPriority( getSharing() ).getNegativeLabel();
        documentation =  new DocumentationData( serverUrl, requestToSelf );
    }

    private void initConsumingTaskData( String serverUrl, CommunityService communityService ) {
         consumingTaskData = new TaskData(
                serverUrl,
                communityService,
                (Part)requestToSelf.getTarget(),
                getUser() );
    }

    private void initResearchAssignment( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        CommunityCommitments allCommitments = communityService.getAllCommitments( true );   // include commitments to self
        allCommitments.committing( getAssignment() );
        researchAssignmentData = new AssignmentData(
                serverUrl,
                getAssignment(),
                allCommitments.benefiting( getAssignment() ),
                allCommitments.committing( getAssignment() ),
                communityService,
                user
        );
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
    public AssignmentData getResearchAssignment() {
        return researchAssignmentData;
    }


    public TaskData getResearchTask() {
        return researchAssignmentData.getTask();
    }

    @XmlElement
    public TaskData getConsumingTask() {
        return  consumingTaskData;
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
        return requestToSelf;
    }


}

package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.NotificationData;
import com.mindalliance.channels.api.procedures.RequestData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Step;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 10:06 PM
 */
@XmlType( name = "answerStep", propOrder = {"label", "answer", "ifConditions", "unlessConditions", "prerequisites", "outcomes"})
public class AnswerStepData extends CommunicationStepData {

    private RequestData request;

    public AnswerStepData() {
        // required
    }

    @Override
    public List<ContactData> allContacts() {
        return request.getContacts();
    }

    public AnswerStepData( Step step,
                           ChecklistData checklist,
                           String serverUrl,
                           CommunityService communityService,
                           ChannelsUser user ) {
        super( step, checklist, serverUrl, communityService, user );
    }

    @Override
    protected void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        super.initData(  serverUrl, communityService, user );
        request = new RequestData(
                serverUrl,
                communityService,
                getCommunicationStep().getSharing(),
                true,
                getChecklist().getAssignment(),
                user );
        setFlowData( new NotificationData(
                serverUrl,
                communityService,
                request.getSharing(),
                false,
                getChecklist().getAssignment(),
                user ) );
    }

    @Override
    @XmlElement (name = "if")
    public List<ConditionData> getIfConditions() {
        return super.getIfConditions();
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

    @Override
    @XmlElement( name = "after" )
    public List<Integer> getPrerequisites() {
        return super.getPrerequisites();
    }

    @Override
    @XmlElement ( name = "unless" )
    public List<ConditionData> getUnlessConditions() {
        return super.getUnlessConditions();
    }

    @Override
    @XmlElement( name = "outcome" )
    public List<OutcomeData> getOutcomes() {
        return super.getOutcomes();
    }

    @XmlElement
    public RequestData getAnswer() {
        return request;
    }

    @Override
    public Set<Long> allActorIds() {
        Set<Long> ids = super.allActorIds();
        ids.addAll( request.allActorIds() );
        return ids;
    }

    @Override
    public Set<Long> allOrganizationIds() {
        Set<Long> ids = super.allOrganizationIds();
        ids.addAll( request.allOrganizationIds() );
        return ids;
    }

    @Override
    public Set<Long> allRoleIds() {
        Set<Long> ids = super.allRoleIds();
        ids.addAll( request.allRoleIds() );
        return ids;
    }

    @Override
    public Set<Long> allPlaceIds() {
        Set<Long> ids = super.allPlaceIds();
        ids.addAll( request.allPlaceIds() );
        return ids;
    }

    @Override
    public Set<Long> allMediumIds() {
        Set<Long> ids = super.allMediumIds();
        ids.addAll( request.getMediumIds() );
        return ids;
    }

    @Override
    public Set<Long> allInfoProductIds() {
        Set<Long> ids = super.allInfoProductIds();
        ids.addAll( request.getInfoProductIds() );
        return ids;
    }

    @Override
    public Set<Long> allInfoFormatIds() {
        Set<Long> ids = super.allInfoFormatIds();
        ids.addAll( request.getInfoFormatIds() );
        return ids;
    }

}

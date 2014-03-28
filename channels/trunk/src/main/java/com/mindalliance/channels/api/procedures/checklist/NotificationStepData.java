package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.NotificationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.checklist.Step;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Set;

/**
 * Web service data for a notification collaboration step in a checklist.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 10:05 PM
 */
@XmlType(name = "notificationStep", propOrder = {"label", "notification", "ifConditions", "unlessConditions", "prerequisites", "outcomes"})
public class NotificationStepData extends CommunicationStepData {

    private NotificationData notification;

    public NotificationStepData() {
        // required
    }

    public NotificationStepData( Step step,
                                 ChecklistData checklist,
                                 String serverUrl,
                                 CommunityService communityService,
                                 ChannelsUser user ) {
        super( step, checklist, serverUrl, communityService, user );
    }

    @Override
    protected void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        super.initData( serverUrl, communityService, user );
        notification = new NotificationData(
                serverUrl,
                communityService,
                getCommunicationStep().getSharing(),
                true,
                getChecklist().getAssignment(),
                user );
        setFlowData( new NotificationData(
                serverUrl,
                communityService,
                notification.getSharing(),
                true,
                getChecklist().getAssignment(),
                user ) );
    }

    @Override
    @XmlElement(name = "if")
    public List<ConditionData> getIfConditions() {
        return super.getIfConditions();
    }

    @Override
    @XmlElement
    public String getLabel() {
        return super.getLabel();
    }

    @Override
    @XmlElement(name = "after")
    public List<Integer> getPrerequisites() {
        return super.getPrerequisites();
    }

    @Override
    @XmlElement(name = "unless")
    public List<ConditionData> getUnlessConditions() {
        return super.getUnlessConditions();
    }

    @Override
    @XmlElement(name = "outcome")
    public List<OutcomeData> getOutcomes() {
        return super.getOutcomes();
    }

    @XmlElement
    public NotificationData getNotification() {
        return notification;
    }

    @Override
    public Set<Long> allActorIds() {
        Set<Long> ids = super.allActorIds();
        ids.addAll( notification.allActorIds() );
        return ids;
    }

    @Override
    public Set<Long> allOrganizationIds() {
        Set<Long> ids = super.allOrganizationIds();
        ids.addAll( notification.allOrganizationIds() );
        return ids;
    }

    @Override
    public Set<Long> allRoleIds() {
        Set<Long> ids = super.allRoleIds();
        ids.addAll( notification.allRoleIds() );
        return ids;
    }

    @Override
    public Set<Long> allPlaceIds() {
        Set<Long> ids = super.allPlaceIds();
        ids.addAll( notification.allPlaceIds() );
        return ids;
    }

    @Override
    public Set<Long> allMediumIds() {
        Set<Long> ids = super.allMediumIds();
        ids.addAll( notification.getMediumIds() );
        return ids;
    }

    @Override
    public Set<Long> allInfoProductIds() {
        Set<Long> ids = super.allInfoProductIds();
        ids.addAll( notification.getInfoProductIds() );
        return ids;
    }

    @Override
    public Set<Long> allInfoFormatIds() {
        Set<Long> ids = super.allInfoFormatIds();
        ids.addAll( notification.getInfoFormatIds() );
        return ids;
    }

    public List<ContactData> allContacts() {
        return notification.getContacts();
    }
}

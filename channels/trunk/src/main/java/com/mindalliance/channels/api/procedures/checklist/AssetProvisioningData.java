package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.community.AgentData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.AbstractProcedureElementData;
import com.mindalliance.channels.api.procedures.TaskData;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.checklist.AssetProvisioning;
import com.mindalliance.channels.core.model.checklist.Checklist;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/14
 * Time: 2:41 PM
 */
@XmlType( name="assetProvisioned", propOrder = {"assetId", "provisionedTask", "assetRecipients",
        "recipientsSupervisor", "label"} )
public class AssetProvisioningData extends AbstractProcedureElementData {

    private AssetProvisioning assetProvisioning;
    private String label;
    private TaskData provisionedTask;
    private Set<ContactData> deliveryContacts;
    private List<AgentData> assetRecipients;
    private AgentData recipientsSupervisor;


    public AssetProvisioningData() {
        //required
    }

    public AssetProvisioningData(
            String serverUrl,
            ChecklistData checklistData,
            AssetProvisioning assetProvisioning,
            CommunityService communityService,
            ChannelsUser user ) {
        super( communityService, checklistData.getAssignment(), user );
        this.assetProvisioning = assetProvisioning;
        Checklist checklist = checklistData.checklist();
        label = assetProvisioning.getLabel( checklist, communityService );
        initData( serverUrl, communityService);
    }

    private void initData( String serverUrl, CommunityService communityService ) {
        provisionedTask = new TaskData( serverUrl, getAssignment(), communityService, getUser() );
        initContactsSupervisor( serverUrl, getAssignment(), communityService );
        initAssetRecipients( serverUrl, getAssignment(), communityService );
    }

    private void initContactsSupervisor(
            String serverUrl,
            CommunityAssignment assignment,
            CommunityService communityService ) {
        Agent supervisorAgent = assignment.getSupervisor();
        if ( supervisorAgent != null ) {
            recipientsSupervisor = new AgentData( serverUrl, supervisorAgent, communityService );
        }
    }

    private void initAssetRecipients(
            String serverUrl,
            CommunityAssignment assignment,
            CommunityService communityService ) {
        assetRecipients = new ArrayList<AgentData>();
        deliveryContacts = new HashSet<ContactData>();
        if ( getAssignment().getPart().isAsTeam() ) {
            for ( CommunityAssignment teamAssignment
                    : communityService.getAllAssignments().assignedTo( assignment.getPart() ) ) {
                if ( !teamAssignment.equals( assignment ) ) {
                    assetRecipients.add( new AgentData( serverUrl, teamAssignment.getAgent(), communityService ) );
                    for ( ContactData contactData :
                            ContactData.findContactsFromEmployment(
                                    serverUrl,
                                    teamAssignment.getCommunityEmployment(),
                                    communityService ) ) {
                        deliveryContacts.add( contactData );
                    }
                }
            }
        }
    }

    @XmlElement
    public TaskData getProvisionedTask() {
        return provisionedTask;
    }

    @XmlElement
    public List<AgentData> getAssetRecipients() {
        return assetRecipients;
    }

    public Set<ContactData> getDeliveryContacts() {
        return deliveryContacts;
    }

    @XmlElement
    public AgentData getRecipientsSupervisor() {
        return recipientsSupervisor;
    }

    @XmlElement
    public Long getAssetId() {
        return assetProvisioning.getAssetId();
    }

    @XmlElement
    public String getLabel() {
        return label;
    }

    public Set<Long> allAssetIds() {
        Set<Long> ids = new HashSet<Long>(  );
        ids.add( getAssetId() );
        return ids;
    }

    public Set<Long> allEventIds() {
        Set<Long> ids = new HashSet<Long>();
        Event initiatedEvent = getAssignment().getPart().getInitiatedEvent();
        if ( initiatedEvent != null )
            ids.add( initiatedEvent.getId() );
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        ids.add( getAssignment().getAgent().getActorId() );
        if ( getAssignment().getSupervisor() != null )
            ids.add( getAssignment().getSupervisor().getActorId() );
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getAssignment().getJurisdiction() != null )
            ids.add( getAssignment().getJurisdiction().getId() );
        Event initiatedEvent = getAssignment().getPart().getInitiatedEvent();
        if ( initiatedEvent != null ) {
            Place scope = initiatedEvent.getScope();
            if ( scope != null ) {
                ids.add( scope.getId() );
            }
            Place placeBasis = initiatedEvent.getPlaceBasis();
            if ( placeBasis != null ) {
                ids.add( placeBasis.getId() );
            }
        }
        return ids;
    }
}

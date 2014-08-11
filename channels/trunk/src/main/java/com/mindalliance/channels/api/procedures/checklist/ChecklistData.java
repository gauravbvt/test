package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.community.AgencyData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.AssignmentData;
import com.mindalliance.channels.api.procedures.TaskData;
import com.mindalliance.channels.api.procedures.TriggerData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.Step;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A Web Service data element for a checklist..
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/1/13
 * Time: 3:11 PM
 */
@XmlType( propOrder = {"confirmed", "anchor", "agentName", "actorId", "employerName",
        "triggers", "assignmentData", "steps", "assetsProvisioned"} )
public class ChecklistData implements Serializable {

    private Checklist checklist;
    private List<Step> sortedSteps;
    private CommunityAssignment assignment;
    private CommunityCommitments benefitingCommitments;
    private CommunityCommitments committingCommitments;
    private ChannelsUser user;
    private AgencyData employer;
    private AssignmentData assignmentData;
    /**
     * All triggers.
     */
    private List<TriggerData> triggers;

    /**
     * '
     * All steps.
     */
    private List<ChecklistStepData> steps;

    private AssetsProvisionedData assetsProvisioned;

    public ChecklistData() {
        // required
    }

    public ChecklistData( String serverUrl,
                          CommunityService communityService,
                          CommunityAssignment assignment,
                          CommunityCommitments benefitingCommitments,
                          CommunityCommitments committingCommitments,
                          ChannelsUser user ) {
        this.assignment = assignment;
        this.benefitingCommitments = benefitingCommitments;
        this.committingCommitments = committingCommitments;
        this.user = user;
        initData( serverUrl, communityService, user );
    }

    private void initData( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        assignmentData = new AssignmentData(
                serverUrl,
                assignment,
                benefitingCommitments,
                committingCommitments,
                communityService,
                user );
        checklist = assignment.getPart().getEffectiveChecklist();
        sortedSteps = checklist.listEffectiveSteps();
        checklist.sort( sortedSteps );
        initEmployer( serverUrl, communityService );
        initTriggers( serverUrl, communityService );
        initSteps( serverUrl, communityService, user );
        assetsProvisioned = new AssetsProvisionedData(
                serverUrl,
                assignment,
                benefitingCommitments,
                committingCommitments,
                communityService,
                user );
    }


    private void initEmployer( String serverUrl, CommunityService communityService ) {
        employer = new AgencyData( serverUrl, assignment.getAgency(), communityService );
    }

    private void initTriggers( String serverUrl, CommunityService communityService ) {
        triggers = new ArrayList<TriggerData>();
        // anytime
        if ( assignment.isOngoing() ) {
            TriggerData triggerData = new TriggerData( serverUrl, communityService, assignment, user );
            triggerData.setOngoing( true );
            triggerData.initTrigger( communityService );
            triggers.add( triggerData );
        } else if ( assignment.isRepeating() ) {
            TriggerData triggerData = new TriggerData( serverUrl, communityService, assignment, user );
            triggerData.setRepeating( true );
            triggerData.initTrigger( communityService );
            triggers.add( triggerData );
        } else {
            // event phase is trigger
            if ( assignment.isInitiatedByEventPhase() ) {
                TriggerData triggerData = new TriggerData( serverUrl, communityService, assignment, user );
                triggerData.setEventPhase( assignment.getEventPhase() );
                triggerData.setEventPhaseContext( assignment.getEventPhaseContext() );
                triggerData.initTrigger( communityService );
                triggers.add( triggerData );
            }
            // triggering notifications (from others)
            for ( Flow triggerNotification : triggeringNotificationsFromOthers() ) {
                TriggerData triggerData = new TriggerData( serverUrl, communityService, assignment, user );
                triggerData.setNotificationFromOther( triggerNotification );
                triggerData.initTrigger( communityService );
                triggers.add( triggerData );
            }
            // triggering requests
            for ( Flow triggerRequest : triggeringRequestsFromOthers() ) {
                TriggerData triggerData = new TriggerData( serverUrl, communityService, assignment, user );
                triggerData.setRequestFromOther( triggerRequest );
                triggerData.initTrigger( communityService );
                triggers.add( triggerData );
            }
            // follow ups
            for ( Flow triggerSelfNotification : triggeringNotificationsToSelf() ) {
                TriggerData triggerData = new TriggerData( serverUrl, communityService, assignment, user );
                triggerData.setNotificationToSelf( triggerSelfNotification );
                triggerData.initTrigger( communityService );
                triggers.add( triggerData );
            }
            // research
            for ( Flow triggerRequest : triggeringRequestsToSelf() ) {
                TriggerData triggerData = new TriggerData( serverUrl, communityService, assignment, user );
                triggerData.setRequestToSelf( triggerRequest );
                triggerData.initTrigger( communityService );
                triggers.add( triggerData );
            }
        }
    }

    private List<Flow> triggeringNotificationsFromOthers() {
        Set<Flow> triggerNotifications = new HashSet<Flow>();
        for ( CommunityCommitment commitment : benefitingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isNotification() && flow.isTriggeringToTarget() && !commitment.isToSelf() ) {
                triggerNotifications.add( commitment.getSharing() );
            }
        }
        return new ArrayList<Flow>( triggerNotifications );
    }

    private List<Flow> triggeringRequestsFromOthers() {
        Set<Flow> triggerRequests = new HashSet<Flow>();
        for ( CommunityCommitment commitment : committingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isAskedFor() && flow.isTriggeringToSource() && !commitment.isToSelf() ) {
                triggerRequests.add( commitment.getSharing() );
            }
        }
        return new ArrayList<Flow>( triggerRequests );
    }

    private List<Flow> triggeringNotificationsToSelf() {
        Set<Flow> triggerNotificationsToSelf = new HashSet<Flow>();
        for ( CommunityCommitment commitment : benefitingCommitments.toSelf() ) {
            Flow flow = commitment.getSharing();
            if ( flow.isNotification() && flow.isTriggeringToTarget() && commitment.isToSelf() ) {
                triggerNotificationsToSelf.add( commitment.getSharing() );
            }
        }
        return new ArrayList<Flow>( triggerNotificationsToSelf );
    }

    private List<Flow> triggeringRequestsToSelf() {
        Set<Flow> triggerRequestsToSelf = new HashSet<Flow>();
        for ( CommunityCommitment commitment : committingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isAskedFor() && flow.isTriggeringToSource() && commitment.isToSelf() ) {
                triggerRequestsToSelf.add( commitment.getSharing() );
            }
        }
        return new ArrayList<Flow>( triggerRequestsToSelf );
    }


    private void initSteps( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        steps = new ArrayList<ChecklistStepData>();
        for ( Step step : sortedSteps ) {
            steps.add( new ChecklistStepData( step, this, serverUrl, communityService, user ) );
        }
    }

    public int indexOfStep( Step step ) {
        return sortedSteps.indexOf( step );
    }

    @XmlElement( name = "step" )
    public List<ChecklistStepData> getSteps() {
        return steps;
    }

    @XmlElement
    public AssetsProvisionedData getAssetsProvisioned() {
        return assetsProvisioned;
    }

    public Checklist checklist() {
        return assignment.getPart().getEffectiveChecklist();
    }

    @XmlElement
    public String getAgentName() {
        return assignment.getAgent().getName();
    }

    @XmlElement( name = "actorId" )
    public Long getActorId() {
        return assignment.getAgent().getActorId();
    }


    @XmlElement( name = "trigger" )
    public List<TriggerData> getTriggers() {
        return triggers;
    }

    @XmlElement( name = "id" )
    public String getAnchor() {
        return Long.toString( assignment.getPart().getId() );
    }

    public Set<Long> allEventIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            Long eventId = trigger.getEventId();
            if ( eventId != null )
                ids.add( eventId );
        }
        ids.add( getTask().getEventId() );
        for ( ChecklistStepData step : getSteps() ) {
            ids.addAll( step.allEventIds() );
        }
        return ids;
    }

    public Set<Long> allPhaseIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            Long phaseId = trigger.getPhaseId();
            if ( phaseId != null )
                ids.add( phaseId );
        }
        ids.add( getTask().getPhaseId() );
        for ( ChecklistStepData step : getSteps() ) {
            ids.addAll( step.allPhaseIds() );
        }
        return ids;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allOrganizationIds() );
        }
        for ( ChecklistStepData step : getSteps() ) {
            ids.addAll( step.allOrganizationIds() );
        }
        ids.addAll( getEmployer().allOrganizationIds() );
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allActorIds() );
        }
        for ( ChecklistStepData step : getSteps() ) {
            ids.addAll( step.allActorIds() );
        }
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allRoleIds() );
        }
        for ( ChecklistStepData step : getSteps() ) {
            ids.addAll( step.allRoleIds() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allPlaceIds() );
        }
        ids.addAll( getTask().allPlaceIds() );
        for ( ChecklistStepData step : getSteps() ) {
            ids.addAll( step.allPlaceIds() );
        }
        return ids;
    }

    public Set<Long> allMediumIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allMediumIds() );
        }
        for ( ChecklistStepData step : getSteps() ) {
            ids.addAll( step.allMediumIds() );
        }
        ids.addAll( getEmployer().allMediumIds() );
        return ids;
    }

    public Set<Long> allFunctionIds() {
        Set<Long> ids = new HashSet<Long>();
        ids.addAll( getTask().allFunctionIds() );
        return ids;
    }

    public Set<Long> allInfoProductIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allInfoProductIds() );
        }
        for ( ChecklistStepData step : getSteps() ) {
            ids.addAll( step.allInfoProductIds() );
        }
        ids.addAll( getTask().allInfoProductIds() );
        return ids;
    }

    public Set<Long> allInfoFormatIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allInfoFormatIds() );
        }
        for ( ChecklistStepData step : getSteps() ) {
            ids.addAll( step.allInfoFormatIds() );
        }
        return ids;
    }

    public Set<Long> allAssetIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allAssetIds() );
        }
        for ( ChecklistStepData step : getSteps() ) {
            ids.addAll( step.allAssetIds() );
        }
        ids.addAll( assetsProvisioned.allAssetIds() );
        return ids;
    }


    public String getLabel() {
        return "I do task \"" + getTask().getLabel() + "\"";
    }

    @XmlElement( name = "assignment" )
    public AssignmentData getAssignmentData() {
        return assignmentData;
    }

    @XmlElement
    public String getEmployerName() {
        return employer != null
                ? employer.getName()
                : null;
    }

    public Set<ContactData> allContacts() {
        Set<ContactData> allContacts = new HashSet<ContactData>();
        for ( TriggerData trigger : getTriggers() ) {
            allContacts.addAll( trigger.allContacts() );
        }
        for ( ChecklistStepData stepData : getSteps() ) {
            allContacts.addAll( stepData.allContacts() );
        }
        allContacts.addAll( assetsProvisioned.allContacts() );
        return allContacts;
    }

    public AgencyData getEmployer() {
        return employer;
    }

    public List<Integer> prerequisiteIndicesOfStep( Step step ) {
        List<Integer> indices = new ArrayList<Integer>();
        for ( Step prerequisite : checklist.listPrerequisiteStepsFor( step ) ) {
            indices.add( indexOfStep( prerequisite ) );
        }
        return indices;
    }

    public CommunityAssignment getAssignment() {
        return assignment;
    }

    private TaskData getTask() {
        return getAssignmentData().getTask();
    }

    public boolean isOngoing() {
        return CollectionUtils.exists(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).getOngoing();
                    }
                } );
    }

    public boolean isRepeating() {
        return CollectionUtils.exists(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).getRepeating();
                    }
                } );
    }


    public boolean isTriggeredByObservation() {
        return CollectionUtils.exists(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).getOnObservation() != null;
                    }
                } );
    }


    public boolean isTriggeredByCommunication() {
        return CollectionUtils.exists(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).getOnNotification() != null
                                || ( (TriggerData) object ).getOnRequest() != null;
                    }
                } );
    }

    @SuppressWarnings( "unchecked" )
    public List<TriggerData> getObservationTriggers() {
        return (List<TriggerData>) CollectionUtils.select(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).isOnObserving();
                    }
                }
        );

    }

    @SuppressWarnings( "unchecked" )
    public List<TriggerData> getRequestTriggers() {
        return (List<TriggerData>) CollectionUtils.select(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).isOnRequestFromOther();
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    public List<TriggerData> getNotificationTriggers() {
        return (List<TriggerData>) CollectionUtils.select(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).isOnNotificationFromOther();
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    public List<TriggerData> getFollowUpTriggers() {
        return (List<TriggerData>) CollectionUtils.select(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).isOnFollowingUp();
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    public List<TriggerData> getResearchTriggers() {
        return (List<TriggerData>) CollectionUtils.select(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).isOnResearching();
                    }
                }
        );
    }



    public String getTaskLabel() {
        StringBuilder sb = new StringBuilder();
/*
        if ( isOngoing() ) {
            sb.append( "I constantly do task - " );
        }else {
            sb.append( "I do task - " );
        }
*/
        sb.append( getTask().getName() );
        return sb.toString();
    }

    public String getTitleOrRole() {
        return getAssignmentData().getTitle();
    }

    public String getOrganizationLabel() {
        return getAssignmentData().getAgencyLabel();
    }

    public boolean hasSends() {
        return getAssignmentData().hasReceives();
    }


    @XmlElement
    public boolean getConfirmed() {
        return checklist().isConfirmed();
    }

}

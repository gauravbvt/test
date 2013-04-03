package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.AgencyData;
import com.mindalliance.channels.api.procedures.AssignmentData;
import com.mindalliance.channels.api.procedures.TriggerData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.community.protocols.CommunityCommitments;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.CommunicationStep;
import com.mindalliance.channels.core.model.checklist.Step;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import javax.jws.WebMethod;
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
@XmlType( propOrder = {"anchor", "agentName", "actorId", "triggers", "assignment", "steps"} )
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

    /**'
     * All steps.
     */
    private List<StepData> steps;

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
        assignmentData = new AssignmentData( serverUrl, communityService, assignment, user, this );
        checklist = assignment.getPart().getChecklist();
        sortedSteps = checklist.listEffectiveSteps();
        checklist.sort( sortedSteps );
        initEmployer( serverUrl, communityService );
        initTriggers( serverUrl, communityService );
        initSteps( serverUrl, communityService, user );
    }

    private void initEmployer( String serverUrl, CommunityService communityService) {
        employer = new AgencyData( serverUrl, assignment.getAgency(),  communityService  );
    }

    private void initTriggers( String serverUrl, CommunityService communityService ) {
        triggers = new ArrayList<TriggerData>();
        // anytime
        if ( assignment.isOngoing() ) {
            TriggerData triggerData = new TriggerData( serverUrl, communityService, assignment, user );
            triggerData.setOngoing( true );
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
            // information discovery (notifications to self)
            for ( Flow triggerSelfNotification : triggeringNotificationsToSelf() ) {
                TriggerData triggerData = new TriggerData( serverUrl, communityService, assignment, user );
                triggerData.setNotificationToSelf( triggerSelfNotification );
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
            // triggering requests to self
            for ( Flow triggerRequest : triggeringRequestsToSelf() ) {
                TriggerData triggerData = new TriggerData( serverUrl, communityService, assignment, user );
                triggerData.setRequestToSelf( triggerRequest );
                triggerData.initTrigger( communityService );
                triggers.add( triggerData );
            }
        }
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


    @WebMethod( exclude = true )
    public CommunityCommitments getBenefitingCommitments() {
        return benefitingCommitments;
    }

    @WebMethod( exclude = true )
    public CommunityCommitments getCommittingCommitments() {
        return committingCommitments;
    }

    private void initSteps( String serverUrl, CommunityService communityService, ChannelsUser user ) {
        steps = new ArrayList<StepData>(  );
        Checklist checklist = checklist();
        for (Step step : sortedSteps ) {
           steps.add( makeStepData( step, serverUrl, communityService, user ) );
        }
    }

    public int indexOfStep( Step step ) {
        return sortedSteps.indexOf( step );
    }

    private StepData makeStepData( Step step, String serverUrl, CommunityService communityService, ChannelsUser user ) {
        if ( step.isActionStep() ) {
            return new ActionStepData( step, this, serverUrl, communityService, user );
        } else {
            CommunicationStep communicationStep = (CommunicationStep)step;
            if ( communicationStep.isNotification() ) {
                return new NotificationStepData( step, this, serverUrl, communityService, user );
            } else if ( communicationStep.isRequest() ) {
                return new RequestStepData( step, this, serverUrl, communityService, user );
            } else if ( communicationStep.isAnswer() ) {
                return new AnswerStepData( step, this, serverUrl, communityService, user );
            } else {
                throw new RuntimeException( "Unknown communication step" );
            }
        }
    }
    @XmlElement( name = "step" )
    public List<StepData> getSteps() {
        return steps;
    }

    public Checklist checklist() {
        return assignment.getPart().getChecklist();
    }

    @XmlElement
    public String getAgentName() {
        return assignment.getAgent().getName();
    }

    @XmlElement( name = "agentId" )
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
        ids.addAll( getAssignment().allEventIds() );
        for ( StepData step : getSteps() ) {
            ids.addAll(  step.allEventIds() );
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
        ids.addAll( getAssignment().allPhaseIds() );
        for ( StepData step : getSteps() ) {
            ids.addAll(  step.allPhaseIds() );
        }
        return ids;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allOrganizationIds() );
        }
        ids.addAll( getAssignment().allOrganizationIds() );
        for ( StepData step : getSteps() ) {
            ids.addAll(  step.allOrganizationIds() );
        }
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allActorIds() );
        }
        ids.addAll( getAssignment().allActorIds() );
        for ( StepData step : getSteps() ) {
            ids.addAll(  step.allActorIds() );
        }
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allRoleIds() );
        }
        ids.addAll( getAssignment().allRoleIds() );
        for ( StepData step : getSteps() ) {
            ids.addAll(  step.allRoleIds() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allPlaceIds() );
        }
        ids.addAll( getAssignment().allPlaceIds() );
        for ( StepData step : getSteps() ) {
            ids.addAll(  step.allPlaceIds() );
        }
        return ids;
    }

    public Set<Long> allMediumIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allMediumIds() );
        }
        ids.addAll( getAssignment().allMediumIds() );
        for ( StepData step : getSteps() ) {
            ids.addAll(  step.allMediumIds() );
        }
        return ids;
    }

    public Set<Long> allInfoProductIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allInfoProductIds() );
        }
        ids.addAll( getAssignment().allInfoProductIds() );
        for ( StepData step : getSteps() ) {
            ids.addAll(  step.allInfoProductIds() );
        }
        return ids;
    }

    public Set<Long> allInfoFormatIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allInfoFormatIds() );
        }
        ids.addAll( getAssignment().allInfoFormatIds() );
        for ( StepData step : getSteps() ) {
            ids.addAll(  step.allInfoFormatIds() );
        }
        return ids;
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

    public boolean isTriggeredByDiscovery() {
        return CollectionUtils.exists(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).getOnDiscovery() != null;
                    }
                } );
    }

    public boolean isTriggeredByResearch() {
        return CollectionUtils.exists(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).getOnResearch() != null;
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
    public List<TriggerData> getDiscoveryTriggers() {
        return (List<TriggerData>) CollectionUtils.select(
                getTriggers(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (TriggerData) object ).isOnDiscovering();
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

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        if ( isOngoing() ) {
            sb.append( "I constantly do task \"" );
        } else if ( isTriggeredByDiscovery() ) {
            sb.append( "I follow up with task \"" );
        } else if ( isTriggeredByResearch() ) {
            sb.append( "To find what you need, I do task \"" );
        } else {
            sb.append( "I do task \"" );
        }
        sb.append( getAssignment().getLabel() );
        return sb.toString();
    }

    @XmlElement
    public AssignmentData getAssignment() {
        return assignmentData;
    }

    public List<ContactData> allContacts() {
        return null;  //Todo
    }

    public AgencyData employer() {
        return employer;
    }

    public List<Integer> prerequisiteIndicesOfStep( Step step ) {
        List<Integer> indices = new ArrayList<Integer>(  );
        for ( Step prerequisite : checklist.listPrerequisiteStepsFor( step ) ) {
             indices.add( indexOfStep( prerequisite ) );
        }
        return indices;
    }
}

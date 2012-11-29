package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.participation.PlanParticipationService;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.PlanService;
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
 * Web Service data element for a procedure of an actor according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/5/11
 * Time: 3:05 PM
 */
@XmlType( propOrder = {"anchor", "actorId", "triggers", "assignment"} )
public class ProcedureData implements Serializable {

    private ChannelsUser user;
    /**
     * All triggers.
     */
    private List<TriggerData> triggers;
    /**
     * An assignment of the actor for which this procedure is being marshalled.
     */
    private Assignment assignment;
    /**
     * All commitments, including to self, benefiting this assignment.
     */
    private Commitments benefitingCommitments;
    /**
     * All commitments
     */
    private Commitments committingCommitments;

    private AssignmentData assignmentData;

    public ProcedureData() {
        // required
    }

    public ProcedureData(
            String serverUrl,
            Assignment assignment,
            Commitments benefitingCommitments,
            Commitments committingCommitments,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        this.assignment = assignment;
        this.benefitingCommitments = benefitingCommitments;
        this.committingCommitments = committingCommitments;
        this.user = user;
        initData( serverUrl, planService, planParticipationService );
    }

    private void initData( String serverUrl, PlanService planService, PlanParticipationService planParticipationService ) {
        assignmentData = new AssignmentData( serverUrl, assignment, planService, planParticipationService, user, this );
        initTriggers( serverUrl, planService, planParticipationService );
    }


    private void initTriggers( String serverUrl, PlanService planService, PlanParticipationService planParticipationService ) {
        triggers = new ArrayList<TriggerData>();
        // anytime
        if ( assignment.isOngoing() ) {
            TriggerData triggerData = new TriggerData( serverUrl,assignment, planService, planParticipationService, user );
            triggerData.setOngoing( true );
            triggerData.initTrigger( planService, planParticipationService );
            triggers.add( triggerData );
        } else {
            // event phase is trigger
            if ( assignment.isInitiatedByEventPhase() ) {
                TriggerData triggerData = new TriggerData( serverUrl,assignment, planService, planParticipationService, user );
                triggerData.setEventPhase( assignment.getEventPhase() );
                triggerData.setEventPhaseContext( assignment.getEventPhaseContext() );
                triggerData.initTrigger( planService, planParticipationService );
                triggers.add( triggerData );
            }
            // information discovery (notifications to self)
            for ( Flow triggerSelfNotification : triggeringNotificationsToSelf() ) {
                TriggerData triggerData = new TriggerData( serverUrl,assignment, planService, planParticipationService, user );
                triggerData.setNotificationToSelf( triggerSelfNotification );
                triggerData.initTrigger( planService, planParticipationService );
                triggers.add( triggerData );
            }
            // triggering notifications (from others)
            for ( Flow triggerNotification : triggeringNotificationsFromOthers() ) {
                TriggerData triggerData = new TriggerData( serverUrl,assignment, planService, planParticipationService, user );
                triggerData.setNotificationFromOther( triggerNotification );
                triggerData.initTrigger( planService, planParticipationService );
                triggers.add( triggerData );
            }
            // triggering requests
            for ( Flow triggerRequest : triggeringRequestsFromOthers() ) {
                TriggerData triggerData = new TriggerData( serverUrl,assignment, planService, planParticipationService, user );
                triggerData.setRequestFromOther( triggerRequest );
                triggerData.initTrigger( planService, planParticipationService );
                triggers.add( triggerData );
            }
            // triggering requests to self
            for ( Flow triggerRequest : triggeringRequestsToSelf() ) {
                TriggerData triggerData = new TriggerData( serverUrl,assignment, planService, planParticipationService, user );
                triggerData.setRequestToSelf( triggerRequest );
                triggerData.initTrigger( planService, planParticipationService );
                triggers.add( triggerData );
            }
        }
    }

    @XmlElement( name = "agentId" )
    public Long getActorId() {
        return assignment.getActor().getId();
    }


    @XmlElement( name = "trigger" )
    public List<TriggerData> getTriggers() {
        return triggers;
    }

    @XmlElement( name = "assignment" )
    public AssignmentData getAssignment() {
        return assignmentData;
    }

    @XmlElement( name = "id")
    public String getAnchor() {
        return getAssignment().getTask().getAnchor();
    }


    private List<Flow> triggeringNotificationsFromOthers() {
        Set<Flow> triggerNotifications = new HashSet<Flow>();
        for ( Commitment commitment : benefitingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isNotification() && flow.isTriggeringToTarget() && !commitment.isToSelf() ) {
                triggerNotifications.add( commitment.getSharing() );
            }
        }
        return new ArrayList<Flow>( triggerNotifications );
    }

    private List<Flow> triggeringRequestsFromOthers() {
        Set<Flow> triggerRequests = new HashSet<Flow>();
        for ( Commitment commitment : committingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isAskedFor() && flow.isTriggeringToSource() && !commitment.isToSelf() ) {
                triggerRequests.add( commitment.getSharing() );
            }
        }
        return new ArrayList<Flow>( triggerRequests );
    }


    private List<Flow> triggeringNotificationsToSelf() {
        Set<Flow> triggerNotificationsToSelf = new HashSet<Flow>();
        for ( Commitment commitment : benefitingCommitments.toSelf() ) {
            Flow flow = commitment.getSharing();
            if ( flow.isNotification() && flow.isTriggeringToTarget() && commitment.isToSelf() ) {
                triggerNotificationsToSelf.add( commitment.getSharing() );
            }
        }
        return new ArrayList<Flow>( triggerNotificationsToSelf );
    }

    private List<Flow> triggeringRequestsToSelf() {
        Set<Flow> triggerRequestsToSelf = new HashSet<Flow>();
        for ( Commitment commitment : committingCommitments ) {
            Flow flow = commitment.getSharing();
            if ( flow.isAskedFor() && flow.isTriggeringToSource() && commitment.isToSelf() ) {
                triggerRequestsToSelf.add( commitment.getSharing() );
            }
        }
        return new ArrayList<Flow>( triggerRequestsToSelf );
    }


    @WebMethod( exclude = true )
    public Commitments getBenefitingCommitments() {
        return benefitingCommitments;
    }

    @WebMethod( exclude = true )
    public Commitments getCommittingCommitments() {
        return committingCommitments;
    }

    public Set<Long> allEventIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            Long eventId = trigger.getEventId();
            if ( eventId != null )
                ids.add( eventId );
        }
        ids.addAll( getAssignment().allEventIds() );
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
        return ids;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allOrganizationIds() );
        }
        ids.addAll( getAssignment().allOrganizationIds() );
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allActorIds() );
        }
        ids.addAll( getAssignment().allActorIds() );
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allRoleIds() );
        }
        ids.addAll( getAssignment().allRoleIds() );
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allPlaceIds() );
        }
        ids.addAll( getAssignment().allPlaceIds() );
        return ids;
    }

    public Set<Long> allMediumIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allMediumIds() );
        }
        ids.addAll( getAssignment().allMediumIds() );
        return ids;
    }

    public Set<Long> allInfoProductIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allInfoProductIds() );
        }
        ids.addAll( getAssignment().allInfoProductIds() );
        return ids;
    }

    public Set<Long> allInfoFormatIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( TriggerData trigger : getTriggers() ) {
            ids.addAll( trigger.allInfoFormatIds() );
        }
        ids.addAll( getAssignment().allInfoFormatIds() );
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

     public boolean hasReceives() {
        return getAssignment().hasReceives();
    }

    public boolean hasSends() {
        return getAssignment().hasSends();
    }

    public String getTaskLabel() {
        StringBuilder sb = new StringBuilder();
        if ( isOngoing() ) {
            sb.append( "I constantly do task \"" );
        } else if ( isTriggeredByDiscovery() ) {
            sb.append( "I follow up with task \"" );
        } else if ( isTriggeredByResearch() ) {
            sb.append( "To find what I need, I do task \"" );
        } else {
            sb.append( "I do task \"" );
        }
        sb.append( getAssignment().getTask().getName() );
        sb.append( "\"" );
        return sb.toString();
    }

    public String getTitleOrRole() {
        return getAssignment().getTitleOrRole();
    }

    public String getOrganizationLabel() {
        return getAssignment().getOrganizationLabel();
    }

    @Override
    public boolean equals( Object object ) {
        return object instanceof ProcedureData
                && getAssignment().equals( ( (ProcedureData) object ).getAssignment() );
    }

    @Override
    public int hashCode() {
        return getAssignment().hashCode();
    }

    public Set<ContactData> allContacts() {
        return assignmentData.allContacts();
    }

    public Part part() {
        return assignmentData.getTask().part();
    }

}

package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.query.PlanService;
import org.apache.commons.collections.CollectionUtils;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Web Service data element for a trigger of a procedure of an actor according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 10:16 AM
 */
@XmlType( propOrder = {"situation", "anytime", "onObservation", "onDiscovery", "onResearch", "onNotification", "onRequest", "requestingTask", "ongoing"} )
public class TriggerData extends AbstractProcedureElementData {

    private Commitment notificationFromOther;
    private Commitment requestFromOther;
    private EventPhase eventPhase;
    private Commitment commitmentToSelf;
    private NotificationData onNotification;
    private RequestData onRequest;
    private Commitment requestToSelf;
    private boolean ongoing = false;
    private List<EventTiming> eventPhaseContext;
    private DiscoveryData discoveryData;
    private ResearchData researchData;
    private SituationData situationData;

    public TriggerData() {
        // required
    }

    public TriggerData(
            Assignment assignment, // task assignment being triggered
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        super( assignment, planService, planParticipationService, user );
    }


    private boolean equalOrBothNull( List<EventTiming> eventPhaseContext, List<EventTiming> otherEventPhaseContext ) {
        return eventPhaseContext == null && otherEventPhaseContext == null
                ||
                ( eventPhaseContext != null && otherEventPhaseContext != null
                && CollectionUtils.isEqualCollection( eventPhaseContext, otherEventPhaseContext ) );
    }

    private boolean equalOrBothNull( EventPhase eventPhase, EventPhase other ) {
        return eventPhase == null && other == null
                ||
                (eventPhase != null && other != null && eventPhase.equals(  other ) );
    }

    private boolean equalOrBothNull( Commitment commitment, Commitment otherCommitment ) {
        return commitment == null && otherCommitment == null
                ||
                ( commitment != null
                && otherCommitment != null
                && commitment.getSharing().equals( otherCommitment.getSharing() ) );
    }

    // Initialization

    public void setOngoing( boolean ongoing ) {
        this.ongoing = ongoing;
    }

    public void setNotificationFromOther( Commitment notificationFromOther ) {
        this.notificationFromOther = notificationFromOther;
        ongoing = false;
    }

    public void setRequestFromOther( Commitment requestFromOther ) {
        this.requestFromOther = requestFromOther;
        ongoing = false;
    }

    public void setEventPhase( EventPhase eventPhase ) {
        this.eventPhase = eventPhase;
        ongoing = false;
    }

    public void setEventPhaseContext( List<EventTiming> eventPhaseContext ) {
        this.eventPhaseContext = eventPhaseContext;
    }

    public void setCommitmentToSelf( Commitment commitmentToSelf ) {
        this.commitmentToSelf = commitmentToSelf;
        ongoing = false;
    }

    public void setRequestToSelf( Commitment requestToSelf ) {
        this.requestToSelf = requestToSelf;
        ongoing = false;
    }

    // Called after nature of trigger is set.
    public void initTrigger( PlanService planService, PlanParticipationService planParticipationService ) {
        initDiscoveryData( planService, planParticipationService );
        initResearchData( planService, planParticipationService );
        initOnNotification( planService, planParticipationService );
        initOnRequest( planService, planParticipationService );
        initSituationData( planService, planParticipationService );
    }

    private void initSituationData( PlanService planService, PlanParticipationService planParticipationService ) {
        if ( isSituationKnown() ) {
            situationData = new SituationData( getAssignment(), planService, planParticipationService, getUser() );
        } else {
            situationData = null;
        }
    }

    private void initOnRequest( PlanService planService, PlanParticipationService planParticipationService ) {
        if ( requestFromOther != null )
            onRequest = new RequestData(
                    requestFromOther,
                    true,
                    getAssignment(),
                    planService,
                    planParticipationService,
                    getUser() );
        else
            onRequest = null;

    }

    private void initOnNotification( PlanService planService, PlanParticipationService planParticipationService ) {

        if ( notificationFromOther != null && !notificationFromOther.isToSelf() )
            onNotification = new NotificationData(
                    notificationFromOther,
                    true,
                    getAssignment(),
                    planService,
                    planParticipationService,
                    getUser() );
        else
            onNotification = null;
    }

    private void initResearchData( PlanService planService, PlanParticipationService planParticipationService ) {
        if ( requestToSelf != null )
            researchData = new ResearchData(
                    requestToSelf,
                    getAssignment(),
                    planService,
                    planParticipationService,
                    getUser() );
        else
            researchData = null;

    }

    private void initDiscoveryData( PlanService planService, PlanParticipationService planParticipationService ) {
        if ( commitmentToSelf != null )
            discoveryData = new DiscoveryData( commitmentToSelf, planService, planParticipationService, getUser() );
        else
            discoveryData = null;

    }

    ///// END INITIALIZATION

    @XmlElement
    public boolean getOngoing() {
        return ongoing;
    }

    @XmlElement
    public String getAnytime() {
        return notificationFromOther == null && requestFromOther == null && eventPhase == null
                ? "true"
                : null;
    }

    @XmlElement
    public TaskData getRequestingTask() {
        return researchData != null ? researchData.getConsumingTask() : null;
    }

    @XmlElement
    public DiscoveryData getOnDiscovery() {
        return discoveryData;
    }

    @XmlElement
    public ObservationData getOnObservation() {
        if ( eventPhase != null )
            return new ObservationData( eventPhase, eventPhaseContext );
        else
            return null;
    }

    @XmlElement
    public ResearchData getOnResearch() {
        return researchData;
    }

    @XmlElement
    public NotificationData getOnNotification() {
        return onNotification;
    }

    @XmlElement
    public RequestData getOnRequest() {
        return onRequest;
    }

    @XmlElement
    public SituationData getSituation() {
        return situationData;
    }

    ////////////////////

    private boolean isSituationKnown() {
        return eventPhase != null
                || notificationFromOther != null && notificationFromOther.getSharing().isReferencesEventPhase()
                || requestFromOther != null && requestFromOther.getSharing().isReferencesEventPhase();
    }

    @WebMethod( exclude = true )
    public Long getEventId() {
        SituationData situation = getSituation();
        if ( situation != null )
            return situation.getEventId();
        else
            return null;
    }

    @WebMethod( exclude = true )
    public Long getPhaseId() {
        SituationData situation = getSituation();
        if ( situation != null )
            return situation.getPhaseId();
        else
            return null;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getOnNotification() != null ) {
            ids.addAll( getOnNotification().allOrganizationIds() );
        }
        if ( getOnRequest() != null ) {
            ids.addAll( getOnRequest().allOrganizationIds() );
        }
        return ids;
    }

    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getOnNotification() != null ) {
            ids.addAll( getOnNotification().allActorIds() );
        }
        if ( getOnRequest() != null ) {
            ids.addAll( getOnRequest().allActorIds() );
        }
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getOnNotification() != null ) {
            ids.addAll( getOnNotification().allRoleIds() );
        }
        if ( getOnRequest() != null ) {
            ids.addAll( getOnRequest().allRoleIds() );
        }
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getOnObservation() != null ) {
            ids.addAll( getOnObservation().allPlaceIds() );
        }
        if ( getOnNotification() != null ) {
            ids.addAll( getOnNotification().allPlaceIds() );
        }
        if ( getOnRequest() != null ) {
            ids.addAll( getOnRequest().allPlaceIds() );
        }
        return ids;
    }


    public Set<Long> allMediumIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getOnNotification() != null ) {
            ids.addAll( getOnNotification().getMediumIds() );
        }
        if ( getOnRequest() != null ) {
            ids.addAll( getOnRequest().getMediumIds() );
        }
        return ids;
    }

    public boolean isOnRequestFromOther() {
        return requestFromOther != null;
    }

    public boolean isOnNotificationFromOther() {
        return notificationFromOther != null;
    }

    public boolean isOnObserving() {
        return eventPhase != null;
    }

    public boolean isOnDiscovering() {
        return commitmentToSelf != null;
    }

    public boolean isOnResearching() {
        return requestToSelf != null;
    }


    @WebMethod( exclude = true )
    public String getLabel() {
        if ( eventPhase != null ) {
            return evenPhaseAndContextLabel();
        } else if ( requestFromOther != null ) {
            return requestFromOther.getSharing().getName();
        } else if ( notificationFromOther != null ) {
            return notificationFromOther.getSharing().getName();
        } else {
            return "???";
        }
    }

    /**
     * Get text about phase and event and context, if any.
     *
     * @return a string
     */
    public String evenPhaseAndContextLabel() {
        StringBuilder sb = new StringBuilder();
        Phase phase = eventPhase.getPhase();
        sb.append( phase.isPreEvent()
                ? "The possibility of event \""
                : phase.isConcurrent()
                ? "The beginning of event \""
                : "The ending of event \""
        );
        sb.append( eventPhase.getEvent().getLabel() );
        sb.append( "\"" );
        if ( eventPhaseContext != null && !eventPhaseContext.isEmpty() ) {
            sb.append( ", " );
            Iterator<EventTiming> eventTimings = eventPhaseContext.iterator();
            while ( eventTimings.hasNext() ) {
                sb.append( eventTimings.next() );
                if ( eventTimings.hasNext() ) {
                    sb.append( " and " );
                }
            }
        }
        return sb.toString();
    }

    public Commitment notificationFromOther() {
        return notificationFromOther;
    }

    public Commitment requestFromOther() {
        return requestFromOther;
    }

    public EventPhase eventPhase() {
        return eventPhase;
    }

    public Commitment commitmentToSelf() {
        return commitmentToSelf;
    }

    public Commitment requestToSelf() {
        return requestToSelf;
    }

    public List<EventTiming> eventPhaseContext() {
        return eventPhaseContext;
    }


    @WebMethod( exclude = true )
    public Assignment discoveringAssignment() {
        return discoveryData.getDiscoveringAssignment();
    }

    public boolean equals( Object object ) {
        if ( object instanceof TriggerData ) {
            TriggerData other = (TriggerData) object;
            return ongoing == other.getOngoing()
                    && equalOrBothNull( notificationFromOther, other.notificationFromOther() )
                    && equalOrBothNull( requestFromOther, other.requestFromOther() )
                    && equalOrBothNull( requestToSelf, other.requestToSelf() )
                    && equalOrBothNull( commitmentToSelf, other.commitmentToSelf() )
                    && equalOrBothNull( eventPhase, other.eventPhase() )
                    && equalOrBothNull( eventPhaseContext, other.eventPhaseContext() ) ;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 1;
        if ( ongoing ) result = 31 * result;
        if ( notificationFromOther != null ) result = 31 * result +  notificationFromOther.getSharing().hashCode();
        if ( requestFromOther != null ) result = 31 * result +  requestFromOther.getSharing().hashCode();
        if ( requestToSelf != null ) result = 31 * result +  requestToSelf.getSharing().hashCode();
        if ( commitmentToSelf != null ) result = 31 * result +  commitmentToSelf.getSharing().hashCode();
        if ( eventPhase != null ) result = 31 * result +  eventPhase.hashCode();
         if ( eventPhaseContext != null ) {
            for ( EventTiming eventTiming : eventPhaseContext ) {
                result = 31 * result + eventTiming.hashCode();
            }
        }
        return result;
    }


}

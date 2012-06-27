package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.query.PlanService;

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
@XmlType( propOrder = {"situation", "anytime", "onObservation", "onDiscovery", "onResearch", "onNotification", "onRequest", "ongoing"} )
public class TriggerData extends AbstractProcedureElementData {

    private Flow notificationFromOther;
    private Flow requestFromOther;
    private EventPhase eventPhase;
    private Flow notificationToSelf;
    private NotificationData onNotification;
    private RequestData onRequest;
    private Flow requestToSelf;
    private boolean ongoing = false;
    private List<EventTiming> eventPhaseContext;

    public TriggerData() {
        // required
    }

    public TriggerData(
            Assignment assignment,
            PlanService planService,
            PlanParticipationService planParticipationService,
            ChannelsUser user ) {
        super( assignment, planService, planParticipationService, user );
    }

    public boolean getOngoing() {
        return ongoing;
    }

    public void setOngoing( boolean ongoing ) {
        this.ongoing = ongoing;
    }

    public void setNotificationFromOther( Flow notificationFromOther ) {
        this.notificationFromOther = notificationFromOther;
        ongoing = false;
    }

    public void setRequestFromOther( Flow requestFromOther ) {
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

    public void setNotificationToSelf( Flow notificationToSelf ) {
        this.notificationToSelf = notificationToSelf;
        ongoing = false;
    }

    public void setRequestToSelf( Flow requestToSelf ) {
        this.requestToSelf = requestToSelf;
        ongoing = false;
    }

    @XmlElement
    public String getAnytime() {
        return notificationFromOther == null && requestFromOther == null && eventPhase == null
                ? "true"
                : null;
    }

    @XmlElement
    public DiscoveryData getOnDiscovery() {
        if ( notificationToSelf != null )
            return new DiscoveryData( notificationToSelf, getPlanService(), getPlanParticipationService(), getUser() );
        else
            return null;
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
        if ( requestToSelf != null )
            return new ResearchData(
                    requestToSelf,
                    getAssignment(),
                    getPlanService(),
                    getPlanParticipationService(),
                    getUser() );
        else
            return null;
    }

    @XmlElement
    public NotificationData getOnNotification() {
        if ( onNotification == null ) {
            if ( notificationFromOther != null && !notificationFromOther.isToSelf() )
                onNotification = new NotificationData(
                        notificationFromOther,
                        true,
                        getAssignment(),
                        getPlanService(),
                        getPlanParticipationService(),
                        getUser() );
            else
                onNotification = null;
        }
        return onNotification;
    }

    @XmlElement
    public RequestData getOnRequest() {
        if ( onRequest == null ) {
            if ( requestFromOther != null )
                onRequest = new RequestData(
                        requestFromOther,
                        true,
                        getAssignment(),
                        getPlanService(),
                        getPlanParticipationService(),
                        getUser() );
            else
                onRequest = null;
        }
        return onRequest;
    }

    @XmlElement
    public SituationData getSituation() {
        if ( isSituationKnown() ) {
            return new SituationData( getAssignment(), getPlanService(), getPlanParticipationService(), getUser() );
        } else {
            return null;
        }
    }

    private boolean isSituationKnown() {
        return eventPhase != null
                || notificationFromOther != null && notificationFromOther.isReferencesEventPhase()
                || requestFromOther != null && requestFromOther.isReferencesEventPhase();
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
        return notificationToSelf != null;
    }

    public boolean isOnResearching() {
        return requestToSelf != null;
    }



    public String getLabel() {
        if ( eventPhase != null ) {
            return evenPhaseAndContextLabel();
        } else if ( requestFromOther != null ) {
            return requestFromOther.getName();
        } else if ( notificationFromOther != null ) {
            return notificationFromOther.getName();
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
                ? "The possibility of "
                : phase.isConcurrent()
                ? "The beginning of "
                : "The ending of "
        ) ;
        sb.append( eventPhase.getEvent().getLabel() );
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


}

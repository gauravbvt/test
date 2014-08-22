package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.EventTiming;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Phase;
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
@XmlType( propOrder = {"situation", "onObservation", "onNotification", "onRequest", "onFollowUp", "onResearch", "requestingTask", "ongoing", "repeating", "repeatCycle"} )
public class TriggerData extends AbstractProcedureElementData {

    public static final String WHENEVER = "Whenever";
    private Flow notificationFromOther;
    private Flow requestFromOther;
    private EventPhase eventPhase;
    private Flow notificationToSelf;
    private NotificationData onNotification;
    private RequestData onRequest;
    private Flow requestToSelf;
    private boolean ongoing = false;
    private boolean repeating = false;
    private List<EventTiming> eventPhaseContext;
    private FollowUpData followUpData;
    private ResearchData researchData;
    private SituationData situationData;
    private String serverUrl;
    private CycleData cycleData;

    public TriggerData() {
        // required
    }

    public TriggerData(
            String serverUrl,
            CommunityService communityService,
            CommunityAssignment assignment, // task assignment being triggered
            ChannelsUser user ) {
        super( communityService, assignment, user );
        this.serverUrl = serverUrl;
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

    private boolean equalOrBothNull( Flow flow, Flow otherFlow ) {
        return flow == null && otherFlow == null
                ||
                ( flow != null
                && otherFlow != null
                && flow.equals( otherFlow ) );
    }

    // Initialization

    public void setOngoing( boolean ongoing ) {
        this.ongoing = ongoing;
        if (ongoing) repeating = false;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public void setRepeating( boolean repeating ) {
        this.repeating = repeating;
        if (repeating) ongoing = false;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setNotificationFromOther( Flow notificationFromOther ) {
        this.notificationFromOther = notificationFromOther;
        ongoing = false;
        repeating = false;
    }

    public void setRequestFromOther( Flow requestFromOther ) {
        this.requestFromOther = requestFromOther;
        ongoing = false;
        repeating = false;
    }

    public void setEventPhase( EventPhase eventPhase ) {
        this.eventPhase = eventPhase;
        ongoing = false;
        repeating = false;
    }

    public void setEventPhaseContext( List<EventTiming> eventPhaseContext ) {
        this.eventPhaseContext = eventPhaseContext;
    }

    public void setNotificationToSelf( Flow notificationToSelf ) {
        this.notificationToSelf = notificationToSelf;
        ongoing = false;
        repeating = false;
    }

    public void setRequestToSelf( Flow requestToSelf ) {
        this.requestToSelf = requestToSelf;
        ongoing = false;
        repeating = false;
    }

    // Called after nature of trigger is set.
    public void initTrigger( CommunityService communityService ) {
        initCycleData( communityService );
        initFollowUpData( communityService );
        initResearchData( communityService );
        initOnNotification( communityService );
        initOnRequest( communityService );
        initSituationData( communityService );
    }

    private void initCycleData( CommunityService communityService ) {
        if ( isRepeating() ) {
            cycleData = new CycleData( getAssignment().getPart().getRepeatsEvery() );
        } else {
            cycleData = null;
        }
    }

    private void initSituationData( CommunityService communityService) {
        if ( isSituationKnown() ) {
            situationData = new SituationData( communityService, getAssignment(), getUser() );
        } else {
            situationData = null;
        }
    }

    private void initOnRequest( CommunityService communityService ) {
        if ( requestFromOther != null )
            onRequest = new RequestData(
                    serverUrl,
                    communityService,
                    requestFromOther,
                    false,
                    getAssignment(),
                    getUser() );
        else
            onRequest = null;

    }

    private void initOnNotification( CommunityService communityService ) {

        if ( notificationFromOther != null && !notificationFromOther.isToSelf() )
            onNotification = new NotificationData(
                    serverUrl,
                    communityService,
                    notificationFromOther,
                    false,
                    getAssignment(),
                    getUser() );
        else
            onNotification = null;
    }

    private void initResearchData( CommunityService communityService ) {
        if ( requestToSelf != null )
            researchData = new ResearchData(
                    serverUrl,
                    communityService,
                    requestToSelf,
                    getAssignment(),
                    getUser() );
        else
            researchData = null;

    }

    private void initFollowUpData( CommunityService communityService ) {
        if ( notificationToSelf != null )
            followUpData = new FollowUpData(
                    serverUrl,
                    communityService,
                    notificationToSelf,
                    getAssignment(),
                    getUser() );
        else
            followUpData = null;

    }

    ///// END INITIALIZATION

    @XmlElement
    public boolean getOngoing() {
        return ongoing;
    }

    @XmlElement
    public boolean getRepeating() {
        return repeating;
    }


    @XmlElement
    public TaskData getRequestingTask() {
        return researchData != null ? researchData.getConsumingTask() : null;
    }

    @XmlElement
    public FollowUpData getOnFollowUp() {
        return followUpData;
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

    @XmlElement
    public CycleData getRepeatCycle() {
        return cycleData;
    }

    ////////////////////

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


    public Set<Long> allInfoProductIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getOnNotification() != null ) {
            ids.addAll( getOnNotification().getInfoProductIds() );
        }
        if ( getOnRequest() != null ) {
            ids.addAll( getOnRequest().getInfoProductIds() );
        }
        return ids;
    }

    public Set<Long> allInfoFormatIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getOnNotification() != null ) {
            ids.addAll( getOnNotification().getInfoFormatIds() );
        }
        if ( getOnRequest() != null ) {
            ids.addAll( getOnRequest().getInfoFormatIds() );
        }
        return ids;
    }

    public Set<Long> allAssetIds() {
        Set<Long> ids = new HashSet<Long>();
        if ( getOnNotification() != null ) {
            ids.addAll( getOnNotification().getAssetIds() );
        }
        if ( getOnRequest() != null ) {
            ids.addAll( getOnRequest().getAssetIds() );
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

    public boolean isOnFollowingUp() {
        return notificationToSelf != null;
    }

    @WebMethod( exclude = true )
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

    public Flow notificationFromOther() {
        return notificationFromOther;
    }

    public Flow requestFromOther() {
        return requestFromOther;
    }

    public EventPhase eventPhase() {
        return eventPhase;
    }

    public Flow commitmentToSelf() {
        return notificationToSelf;
    }

    public Flow requestToSelf() {
        return requestToSelf;
    }

    public List<EventTiming> eventPhaseContext() {
        return eventPhaseContext;
    }

    public Set<ContactData> allContacts() {
        Set<ContactData> allContacts = new HashSet<ContactData>(  );
        if ( onNotification != null ) {
            allContacts.addAll( onNotification.getContacts() );
        }
        if ( onRequest != null ) {
            allContacts.addAll( onRequest.getContacts() );
        }
        return allContacts;
    }

    public boolean isOnResearching() {
        return requestToSelf != null;
    }

    public String getSituationLabel() {
        return getSituation() == null
                ? "at any time"
                : getSituation().getSituationLabel();
    }

    public boolean equals( Object object ) {
        if ( object instanceof TriggerData ) {
            TriggerData other = (TriggerData) object;
            return ongoing == other.getOngoing()
                    && repeating == other.getRepeating()
                    && equalOrBothNull( notificationFromOther, other.notificationFromOther() )
                    && equalOrBothNull( requestFromOther, other.requestFromOther() )
                    && equalOrBothNull( requestToSelf, other.requestToSelf() )
                    && equalOrBothNull( notificationToSelf, other.commitmentToSelf() )
                    && equalOrBothNull( eventPhase, other.eventPhase() )
                    && equalOrBothNull( eventPhaseContext, other.eventPhaseContext() ) ;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 1;
        if ( ongoing ) result = 31 * result + "ongoing".hashCode();
        if ( repeating ) result = 31 * result + "repeating".hashCode();
        if ( notificationFromOther != null ) result = 31 * result +  notificationFromOther.hashCode();
        if ( requestFromOther != null ) result = 31 * result +  requestFromOther.hashCode();
        if ( requestToSelf != null ) result = 31 * result +  requestToSelf.hashCode();
        if ( notificationToSelf != null ) result = 31 * result +  notificationToSelf.hashCode();
        if ( eventPhase != null ) result = 31 * result +  eventPhase.hashCode();
         if ( eventPhaseContext != null ) {
            for ( EventTiming eventTiming : eventPhaseContext ) {
                result = 31 * result + eventTiming.hashCode();
            }
        }
        return result;
    }

 }

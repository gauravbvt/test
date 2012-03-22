package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.query.PlanService;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

/**
 * Web Service data element for a trigger of a procedure of an actor according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 10:16 AM
 */
@XmlType( propOrder = {"situation", "anytime", "onObservation", "onDiscovery", "onResearch", "onNotification", "onRequest"} )
public class TriggerData extends AbstractProcedureElementData {

    private Flow notificationFromOther;
    private Flow requestFromOther;
    private EventPhase eventPhase;
    private Flow notificationToSelf;
    private NotificationData onNotification;
    private RequestData onRequest;
    private Flow requestToSelf;

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

    public void setNotificationFromOther( Flow notificationFromOther ) {
        this.notificationFromOther = notificationFromOther;
    }

    public void setRequestFromOther( Flow requestFromOther ) {
        this.requestFromOther = requestFromOther;
    }

    public void setEventPhase( EventPhase eventPhase ) {
        this.eventPhase = eventPhase;
    }

    public void setNotificationToSelf( Flow notificationToSelf ) {
        this.notificationToSelf = notificationToSelf;
    }

    public void setRequestToSelf( Flow requestToSelf ) {
        this.requestToSelf = requestToSelf;
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
            return new ObservationData( eventPhase );
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
                        getUser());
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
                        getUser());
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
}

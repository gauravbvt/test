package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.query.PlanService;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;
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
public class TriggerData extends AbstractProcedureElementData {

    private Commitment notification;
    private Commitment request;
    private EventPhase eventPhase;
    private Commitment notificationToSelf;
    private NotificationData onNotification;
    private RequestData onRequest;
    private Commitment requestToSelf;

    public TriggerData() {
        // required
    }

    public TriggerData( Assignment assignment, PlanService planService ) {
        super( assignment, planService );
    }

    public void setNotification( Commitment notification ) {
        this.notification = notification;
    }

    public void setRequest( Commitment request ) {
        this.request = request;
    }

    public void setEventPhase( EventPhase eventPhase ) {
        this.eventPhase = eventPhase;
    }

    public void setNotificationToSelf( Commitment notificationToSelf ) {
        this.notificationToSelf = notificationToSelf;
    }

    public void setRequestToSelf( Commitment requestToSelf ) {
        this.requestToSelf = requestToSelf;
    }

    @XmlElement
    public String getAnytime() {
        return notification == null && request == null && eventPhase == null
                ? "true"
                : null;
    }

    @XmlElement
    public DiscoveryData getOnDiscovery() {
        if ( eventPhase != null )
            return new DiscoveryData( eventPhase, getPlanService() );
        else if ( notificationToSelf != null )
            return new DiscoveryData( notificationToSelf, getPlanService() );
        else
            return null;
    }

    @XmlElement
    public ResearchData getOnResearch() {
        if ( requestToSelf != null )
            return new ResearchData( requestToSelf, getAssignment(), getPlanService() );
        else
            return null;
    }

    @XmlElement
    public NotificationData getOnNotification() {
        if ( onNotification == null ) {
            if ( notification != null && !notification.isToSelf() )
                onNotification = new NotificationData( notification, true, getAssignment(), getPlanService() );
            else
                onNotification = null;
        }
        return onNotification;
    }

    @XmlElement
    public RequestData getOnRequest() {
        if ( onRequest == null ) {
            if ( request != null )
                onRequest = new RequestData( request, true, getAssignment(), getPlanService() );
            else
                onRequest = null;
        }
        return onRequest;
    }

    @WebMethod( exclude = true )
    public Long getEventId() {
        if ( eventPhase != null )
            return eventPhase.getEvent().getId();
        else
            return null;
    }

    @WebMethod( exclude = true )
    public Long getPhaseId() {
        if ( eventPhase != null )
            return eventPhase.getPhase().getId();
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

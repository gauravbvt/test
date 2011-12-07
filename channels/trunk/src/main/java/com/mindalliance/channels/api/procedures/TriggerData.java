package com.mindalliance.channels.api.procedures;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Web Service data element for a trigger of a procedure of an actor according to a plan.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/6/11
 * Time: 10:16 AM
 */
@XmlRootElement( name = "trigger", namespace = "http://mind-alliance.com/api/isp/v1/" )
public class TriggerData extends AbstractProcedureElementData {

    private Flow notification;
    private Flow request;
    private EventPhase eventPhase;
    private Commitment notificationToSelf;

    public TriggerData() {
        // required
    }

    public TriggerData( Assignment assignment, PlanService planService ) {
        super( assignment, planService );
    }

    public void setNotification( Flow notification ) {
        this.notification = notification;
    }

    public void setRequest( Flow request ) {
        this.request = request;
    }

    public void setEventPhase( EventPhase eventPhase ) {
        this.eventPhase = eventPhase;
    }

    public void setNotificationToSelf( Commitment notificationToSelf ) {
        this.notificationToSelf = notificationToSelf;
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
    public RequestData getOnResearch() {
        if ( request != null && request.isToSelf() )
            return new RequestData( request, true, getAssignment(), getPlanService() );
        else
            return null;
    }

    @XmlElement
    public NotificationData getOnNotification() {
        if ( notification != null && !notification.isToSelf() )
            return new NotificationData( notification, true, getAssignment(), getPlanService() );
        else
            return null;
    }

    @XmlElement
    public RequestData getOnRequest() {
        if ( request != null && !request.isToSelf() )
            return new RequestData( request, true, getAssignment(), getPlanService() );
        else
            return null;
    }

}

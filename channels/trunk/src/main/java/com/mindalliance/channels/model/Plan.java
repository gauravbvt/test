package com.mindalliance.channels.model;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A plan: events scenarios that respond to events and entities participating in the responses.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2009
 * Time: 3:43:39 PM
 */
@Entity
public class Plan extends ModelObject {

    /**
     * The scenarios, for convenience...
     */
    private Set<Scenario> scenarios = new HashSet<Scenario>();
    /**
     * Unplanned-for events.
     */
    private List<Event> incidents = new ArrayList<Event>();
    /**
     * Name of client sponsoring the plan.
     */
    private String client = "Unnamed";
    /**
     * Unique resource identifier for the plan.
     * Always set when application loads.
     */
    private String uri;

    public Plan() {
    }

    // TODO fix persistence, eventually
    @Transient
    public Set<Scenario> getScenarios() {
        return scenarios;
    }

    // TODO fix persistence, eventually
    @Transient
    public List<Event> getIncidents() {
        return incidents;
    }

    public void setIncidents( List<Event> incidents ) {
        this.incidents = incidents;
    }

    public String getClient() {
        return client;
    }

    public void setClient( String client ) {
        this.client = client;
    }

    public String getUri() {
        return uri;
    }

    public void setUri( String uri ) {
        this.uri = uri;
    }

    /**
     * Add event.
     *
     * @param event an event
     */
    public void addIncident( Event event ) {
        if ( !incidents.contains( event ) ) incidents.add( event );
    }

    /**
     * Get default event.
     *
     * @return a plan event
     */
    public Event getDefaultEvent() {
        assert incidents != null && !incidents.isEmpty();
        return incidents.iterator().next();
    }

    /**
     * Whether an event is an incident.
     *
     * @param event a plan event
     * @return a boolean
     */
    public boolean isIncident( Event event ) {
        return incidents.contains( event );
    }
}

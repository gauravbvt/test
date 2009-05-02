package com.mindalliance.channels.model;

import javax.persistence.Entity;
import java.util.HashSet;
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
     * Planned-for events.
     */
    private Set<PlanEvent> events = new HashSet<PlanEvent>();
    /**
     * Name of client sponsoring the plan.
     */
    private String client = "Unnamed";
    /**
     * Unique resource identifier for the plan.
     */
    private String uri = "";

    public Plan() {
    }

    public Set<Scenario> getScenarios() {
        return scenarios;
    }

    public Set<PlanEvent> getEvents() {
        return events;
    }

    public void setEvents( Set<PlanEvent> events ) {
        this.events = events;
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
}

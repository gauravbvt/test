package com.mindalliance.channels.model;

import java.util.Set;
import java.util.HashSet;

/**
 * A plan: events scenarios that respond to events and entities participating in the responses.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2009
 * Time: 3:43:39 PM
 */
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

    public Set<Scenario> getScenarios() {
        return scenarios;
    }

    public Set<PlanEvent> getEvents() {
        return events;
    }

    public String getClient() {
        return client;
    }

    public void setClient( String client ) {
        this.client = client;
    }
}

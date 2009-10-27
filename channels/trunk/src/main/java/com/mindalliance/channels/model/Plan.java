package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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
     * Name of the default phase of a plan.
     */
    private static final String DEFAULT_PHASE_NAME = "Responding";
    /**
     * Timing of the default phase.
     */
    private static final Phase.Timing DEFAULT_PHASE_TIMING = Phase.Timing.Concurrent;

    /**
     * The status of a (version of) plan.
     */
    public enum Status implements Serializable {
        /**
         * In development.
         */
        DEVELOPMENT,
        /**
         * In production.
         */
        PRODUCTION,
        /**
         * Retired.
         */
        RETIRED
    }

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
    private String uri = "default";
    /**
     * Whether dev, prod or retired.
     */
    @Transient
    private Status status;
    /**
     * Version number.
     * Implied from folder where persisted
     */
    @Transient
    private int version;
    /**
     * User names of planners who voted to put this plan into production.
     */
    private List<String> producers = new ArrayList<String>();
    /**
     * Date when version was in retirement, production or development.
     */
    private Date whenVersioned;
    /**
     * Phases defined for this plan.
     */
    private List<Phase> phases = new ArrayList<Phase>();
    /**
     * Organization whose involvement is expected.
     */
    private List<Organization> organizations = new ArrayList<Organization>();


    //-----------------------------
    public Plan() {
        whenVersioned = new Date();
    }

    public int getVersion() {
        return version;
    }

    @Transient
    public void setVersion( int version ) {
        this.version = version;
    }

    @Transient
    public boolean isDevelopment() {
        return status == Status.DEVELOPMENT;
    }

    public void setDevelopment() {
        status = Status.DEVELOPMENT;
    }

    @Transient
    public boolean isProduction() {
        return status == Status.PRODUCTION;
    }

    public void setProduction() {
        status = Status.PRODUCTION;
    }

    @Transient
    public boolean isRetired() {
        return status == Status.RETIRED;
    }

    public void setRetired() {
        status = Status.RETIRED;
    }

    /**
     * Name with version.
     *
     * @return a string
     */
    @Transient
    public String getVersionedName() {
        return getName() + " v." + getVersion() + "(" + getStatusString() + ")";
    }

    @Transient
    public Set<Scenario> getScenarios() {
        return scenarios;
    }

    @Transient
    public List<Event> getIncidents() {
        return incidents;
    }

    public void setIncidents( List<Event> incidents ) {
        this.incidents = incidents;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations( List<Organization> organizations ) {
        this.organizations = organizations;
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

    @Transient
    public String getVersionUri() {
        return uri + ":" + version;
    }

    public List<String> getProducers() {
        return producers;
    }

    public void setProducers( List<String> producers ) {
        this.producers = producers;
    }

    public void removeAllProducers() {
        producers = new ArrayList<String>();
    }

    public Date getWhenVersioned() {
        return whenVersioned;
    }

    public void setWhenVersioned( Date whenVersioned ) {
        this.whenVersioned = whenVersioned;
    }

    /**
     * Add planner as voting to put plan in production.
     *
     * @param username a string
     */
    public void addProducer( String username ) {
        if ( !producers.contains( username ) ) producers.add( username );
    }

    /**
     * Remove planner as voting to put plan in production.
     *
     * @param username a string
     */
    public void removeProducer( String username ) {
        producers.remove( username );
    }

    /**
     * Add event.
     *
     * @param event an event
     */
    public void addIncident( Event event ) {
        assert event.isType();
        if ( !incidents.contains( event ) ) incidents.add( event );
    }

    /**
     * Add an organization expected to be involved.
     *
     * @param organization an organization
     */
    public void addOrganization( Organization organization ) {
        assert organization.isActual();
        if ( !organizations.contains( organization ) ) organizations.add( organization );
    }

    /**
     * Get a scenario's default event.
     *
     * @return a plan event
     */
    @Transient
    public Event getDefaultEvent() {
        assert incidents != null && !incidents.isEmpty();
        Iterator<Event> eventIterator = incidents.iterator();
        return eventIterator.hasNext() ? eventIterator.next() : null;
    }

    /**
     * Get a scenario's default phase, adding it if needed.
     *
     * @param queryService a query service
     * @return a phase
     */
    public Phase getDefaultPhase( QueryService queryService ) {
        if ( phases.isEmpty() ) addDefaultPhase( queryService );
        return phases.get( 0 );
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

    /**
     * Add a scenario to list.
     *
     * @param scenario a scenario
     */
    public void addScenario( Scenario scenario ) {
        scenarios.add( scenario );
    }

    /**
     * Remove deleted scenario from list.
     *
     * @param scenario a scenario
     */
    public void removeScenario( Scenario scenario ) {
        scenarios.remove( scenario );
    }

    /**
     * Get the number of scenarios in this plan.
     *
     * @return the number of scenarios
     */
    @Transient
    public int getScenarioCount() {
        return scenarios.size();
    }

    /**
     * Return one of the scenarios.
     *
     * @return a scenario
     */
    @Transient
    public Scenario getDefaultScenario() {
        return getScenarios().iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isLockable() {
        return false;
    }

    public List<Phase> getPhases() {
        return phases;
    }

    public void setPhases( List<Phase> phases ) {
        this.phases = phases;
    }

    /**
     * Add default phase to plan.
     *
     * @param queryService a query service
     */
    public void addDefaultPhase( QueryService queryService ) {
        Phase defaultPhase = queryService.findOrCreate( Phase.class, DEFAULT_PHASE_NAME );
        defaultPhase.setTiming( DEFAULT_PHASE_TIMING );
        phases.add( defaultPhase );
    }

    /**
     * Add phase to plan.
     *
     * @param phase a phase
     */
    public void addPhase( Phase phase ) {
        phases.add( phase );
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getName()
                + " v."
                + getVersion()
                + " ("
                + getStatusString()
                + ")";
    }

    private String getStatusString() {
        return isDevelopment()
                ? "dev" : isProduction()
                ? "prod" : "ret";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        return this == obj
                || obj instanceof Plan
                && getVersionUri().equals( ( (Plan) obj ).getVersionUri() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getVersionUri().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public boolean references( final ModelObject mo ) {
        return
                CollectionUtils.exists(
                        scenarios,
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return ModelObject.areIdentical( (ModelObject) obj, mo );
                            }
                        } )
                        ||
                        CollectionUtils.exists(
                                incidents,
                                new Predicate() {
                                    public boolean evaluate( Object obj ) {
                                        return ModelObject.areIdentical( (ModelObject) obj, mo );
                                    }
                                } )
                        ||
                        CollectionUtils.exists(
                                organizations,
                                new Predicate() {
                                    public boolean evaluate( Object obj ) {
                                        return ModelObject.areIdentical( (ModelObject) obj, mo );
                                    }
                                } );
    }
}
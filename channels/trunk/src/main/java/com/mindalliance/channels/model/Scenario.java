// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.Model;

/**
 * One or more incidents that provide a starting point for
 * modeling information sharing needs and capabilities, possibly,
 * within environments that impose constraints on resources,
 * policies and communications.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 *
 * @opt attributes
 * @composed - - * ScenarioElement
 * @composed - - * Issue
 */
public class Scenario extends ModelElement
        implements Comparable<Scenario> {

    private String name;
    private List<Occurence> occurences = new ArrayList<Occurence>();
    private List<String> objectives = new ArrayList<String>();
    private List<Event> incidents = new ArrayList<Event>();
    private Set<Event> events = new TreeSet<Event>();
    private Set<Event> branchings = new TreeSet<Event>();
    private Set<Environment> environments = new TreeSet<Environment>();
    private Set<Assertion> assertions = new TreeSet<Assertion>();
    private Set<Task> tasks = new TreeSet<Task>();
    private List<Exchange> exchanges = new ArrayList<Exchange>();
    private Set<Issue> issues = new TreeSet<Issue>();

    /**
     * Default constructor.
     */
    public Scenario() {
        super();
    }

    /**
     * Default constructor.
     * @param model the model
     */
    public Scenario( Model model ) {
        super( model );
    }

    /**
     * Return the value of assertions.
     */
    public Set<Assertion> getAssertions() {
        return this.assertions;
    }

    /**
     * Set the value of assertions.
     * @param assertions The new value of assertions
     */
    public void setAssertions( Set<Assertion> assertions ) {
        this.assertions = assertions;
    }

    /**
     * Return the value of branchings.
     */
    public Set<Event> getBranchings() {
        return this.branchings;
    }

    /**
     * Set the value of branchings.
     * @param branchings The new value of branchings
     */
    public void setBranchings( Set<Event> branchings ) {
        this.branchings = branchings;
    }

    /**
     * Return the value of environments.
     */
    public Set<Environment> getEnvironments() {
        return this.environments;
    }

    /**
     * Set the value of environments.
     * @param environments The new value of environments
     */
    public void setEnvironments( Set<Environment> environments ) {
        this.environments = environments;
    }

    /**
     * Return the value of events.
     */
    public Set<Event> getEvents() {
        return this.events;
    }

    /**
     * Set the value of events.
     * @param events The new value of events
     */
    public void setEvents( Set<Event> events ) {
        this.events = events;
    }

    /**
     * Return the value of exchanges.
     */
    public List<Exchange> getExchanges() {
        return this.exchanges;
    }

    /**
     * Set the value of exchanges.
     * @param exchanges The new value of exchanges
     */
    public void setExchanges( List<Exchange> exchanges ) {
        this.exchanges = exchanges;
    }

    /**
     * Return the value of incidents.
     */
    public List<Event> getIncidents() {
        return this.incidents;
    }

    /**
     * Set the value of incidents.
     * @param incidents The new value of incidents
     */
    public void setIncidents( List<Event> incidents ) {
        this.incidents = incidents;
    }

    /**
     * Return the value of issues.
     */
    public Set<Issue> getIssues() {
        return this.issues;
    }

    /**
     * Set the value of issues.
     * @param issues The new value of issues
     */
    public void setIssues( Set<Issue> issues ) {
        this.issues = issues;
    }

    /**
     * Return the value of name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the value of name.
     * @param name The new value of name
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Return the value of objectives.
     */
    public List<String> getObjectives() {
        return this.objectives;
    }

    /**
     * Set the value of objectives.
     * @param objectives The new value of objectives
     */
    public void setObjectives( List<String> objectives ) {
        this.objectives = objectives;
    }

    /**
     * Return the value of tasks.
     */
    public Set<Task> getTasks() {
        return this.tasks;
    }

    /**
     * Set the value of tasks.
     * @param tasks The new value of tasks
     */
    public void setTasks( Set<Task> tasks ) {
        this.tasks = tasks;
    }

    /**
     * Compares this object with the specified object for order.
     * @param o the other scenario
     */
    public int compareTo( Scenario o ) {
        return getName().compareTo( o.getName() );
    }

    /**
     * Return the value of occurences.
     */
    public List<Occurence> getOccurences() {
        return this.occurences;
    }

    /**
     * Set the value of occurences.
     * @param occurences The new value of occurences
     */
    public void setOccurences( List<Occurence> occurences ) {
        for ( Occurence o : this.occurences )
            o.setScenario( null );
        this.occurences = new ArrayList<Occurence>( occurences );
        for ( Occurence o : this.occurences )
            o.setScenario( this );
    }

    /**
     * Add a new occurence.
     * @param occurence the occurence
     */
    public void addOccurence( Occurence occurence ) {
        this.occurences.add( occurence );
        occurence.setScenario( this );
    }

    /**
     * Remove an occurence.
     * @param occurence the occurence
     */
    public void removeOccurence( Occurence occurence ) {
        this.occurences.remove( occurence );
        occurence.setScenario( null );
    }
}

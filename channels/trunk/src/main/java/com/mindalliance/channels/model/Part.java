package com.mindalliance.channels.model;

import java.util.Set;
import java.text.MessageFormat;

/**
 * A part in a scenario.
 */
public class Part extends Node {

    /** Default task name. */
    static final String DEFAULT_TASK = "doing something";

    /** Default actor label, when unknown. */
    private static final String DEFAULT_ACTOR = "Unknown actor";

    /** The task label of this part (never null or empty). */
    private String task = DEFAULT_TASK;

    /** The actor assigned to this task (optional). */
    private Actor actor;

    /** The role of this task (optional). */
    private Role role;

    /** The organization (optional). */
    private Organization organization;

    /** The location (optional). */
    private Location location;

    /** The jurisdiction (optional). */
    private Jurisdiction jurisdiction;

    private Set<Flow> requirements;
    private Set<Flow> outcomes;

    public Part() {
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final String actorString = getActor() != null ? getActor().toString()
                                 : getRole()  != null ? getRole().toString()
                                 : DEFAULT_ACTOR;
        return MessageFormat.format( "{0} {1}", actorString, getTask() );
    }

    public String getTask() {
        return task;
    }

    public void setTask( String task ) {
        this.task = task == null || task.length() == 0 ? DEFAULT_TASK
                                                       : task ;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction( Jurisdiction jurisdiction ) {
        this.jurisdiction = jurisdiction;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation( Location location ) {
        this.location = location;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    public Role getRole() {
        return role;
    }

    public void setRole( Role role ) {
        this.role = role;
    }
}

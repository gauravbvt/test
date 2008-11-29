package com.mindalliance.channels;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Jurisdiction;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Location;

import java.text.MessageFormat;

/**
 * A part in a scenario.
 */
public class Part extends Node {

    // TODO Should describe severity level of failure

    /** Default actor label, when unknown. */
    public static final String DEFAULT_ACTOR = "Somebody";

    /** Default task name. */
    static final String DEFAULT_TASK = "doing something";

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

    public Part() {
        adjustName();
    }

    /**
     * Utility constructor for tests.
     * @param actor an actor
     * @param task a task
     */
    public Part( Actor actor, String task ) {
        this();
        setActor( actor );
        setTask( task );
    }

    /**
     * Utility constructor for tests.
     * @param role a role
     * @param task a task
     */
    public Part( Role role, String task ) {
        this();
        setRole( role );
        setTask( task );
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return MessageFormat.format( "{0} {1}", getName(), getTask() );
    }

    public final String getTask() {
        return task;
    }

    public final void setTask( String task ) {
        this.task = task == null || task.length() == 0 ? DEFAULT_TASK
                                                       : task ;
    }

    private void adjustName() {
        if ( getActor() != null )
            setName( getActor().toString() );
        else if ( getRole() != null )
            setName( getRole().toString() );
        else if ( getOrganization() != null )
            setName( getOrganization().toString() );
        else
            setName( DEFAULT_ACTOR );
    }

    public final Actor getActor() {
        return actor;
    }

    /**
     * Set the actor. Also resets name accordingly.
     * @param actor the new actor.
     */
    public final void setActor( Actor actor ) {
        this.actor = actor;
        adjustName();
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

    /**
     * Set the organization. Also resets name accordingly.
     * @param organization the new organization.
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
        adjustName();
    }

    public final Role getRole() {
        return role;
    }

    /**
     * Set the role. Also resets name accordingly.
     * @param role the new role.
     */
    public final void setRole( Role role ) {
        this.role = role;
        adjustName();
    }

    @Override
    public boolean isPart() {
        return true;
    }

    public boolean isUndefined() {
        return actor == null && role == null && organization == null;
    }
}

package com.mindalliance.channels;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.text.MessageFormat;

/**
 * A part in a scenario.
 */
@Entity
public class Part extends Node {

    // TODO Should describe severity level of failure

    /**
     * Default actor label, when unknown.
     */
    public static final String DEFAULT_ACTOR = "Somebody";

    /**
     * Default task name.
     */
    static final String DEFAULT_TASK = "doing something";

    /**
     * The task label of this part (never null or empty).
     */
    private String task = DEFAULT_TASK;

    /**
     * The actor assigned to this task (optional).
     */
    private Actor actor;

    /**
     * The role of this task (optional).
     */
    private Role role;

    /**
     * The organization (optional).
     */
    private Organization organization;

    /**
     * The location (optional).
     */
    private Place location;

    /**
     * The jurisdiction (optional).
     */
    private Place jurisdiction;
    /**
     * Whether the part's task completes on its own after some time.
     */
    private boolean selfTerminating;
    /**
     * Usual time for the task to complete on its own.
     * If null, the task must be terminated.
     */
    private Delay completionTime = new Delay();
    /**
     * Whether the part's task repeats (at fixed intervals).
     */
    private boolean repeating;
    /**
     * How long before the task is repeated.
     * Not repeated if null.
     */
    private Delay repeatsEvery = new Delay();

    public Part() {
        adjustName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public String getTitle() {
        return MessageFormat.format( "{0} {1}", getName(), getTask() );
    }

    public String getTask() {
        return task;
    }

    public void setTask( String task ) {
        this.task = task == null || task.length() == 0 ? DEFAULT_TASK
                : task;
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

    @ManyToOne( cascade = CascadeType.PERSIST )
    public Actor getActor() {
        return actor;
    }

    /**
     * Set the actor. Also resets name accordingly.
     *
     * @param actor the new actor.
     */
    public void setActor( Actor actor ) {
        this.actor = actor;
        adjustName();
    }

    @ManyToOne( cascade = CascadeType.PERSIST )
    public Place getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction( Place jurisdiction ) {
        this.jurisdiction = jurisdiction;
    }

    @ManyToOne( cascade = CascadeType.PERSIST )
    public Place getLocation() {
        return location;
    }

    public void setLocation( Place location ) {
        this.location = location;
    }

    @ManyToOne( cascade = CascadeType.PERSIST )
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Set the organization. Also resets name accordingly.
     *
     * @param organization the new organization.
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
        adjustName();
    }

    @ManyToOne( cascade = CascadeType.ALL )
    public Role getRole() {
        return role;
    }

    /**
     * Set the role. Also resets name accordingly.
     *
     * @param role the new role.
     */
    public void setRole( Role role ) {
        this.role = role;
        adjustName();
    }

    public Delay getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime( Delay completionTime ) {
        this.completionTime = completionTime;
    }

    public Delay getRepeatsEvery() {
        return repeatsEvery;
    }

    public void setRepeatsEvery( Delay repeatsEvery ) {
        this.repeatsEvery = repeatsEvery;
    }

    @Override
    @Transient
    public boolean isPart() {
        return true;
    }

    @Transient
    public boolean isUndefined() {
        return actor == null && role == null && organization == null && location == null;
    }

    /**
     * @return true if task has not been specified
     */
    public boolean hasDefaultTask() {
        return task.equals( DEFAULT_TASK );
    }

    /**
     * @return true if role contains "system"
     */
    @Transient
    public boolean isSystem() {
        Role r = getRole();
        return r != null
                && r.getName().toLowerCase().contains( "system" );
    }

    /**
     * @return true if part is only specified by a role.
     */
    @Transient
    public boolean isOnlyRole() {
        return role != null && actor == null;
    }

    /**
     * Gets the resourceSpec implied by the part
     *
     * @return a ResourceSpec
     */
    public ResourceSpec resourceSpec() {
        return new ResourceSpec( this );
    }

    /**
     * Test whether the resource spec of the part intersects a given resource spec
     *
     * @param resourceSpec a resource
     * @return a boolean
     */
    public boolean involves( ResourceSpec resourceSpec ) {
        ResourceSpec partResourceSpec = resourceSpec();
        return !partResourceSpec.isAnyone() && resourceSpec.intersects( partResourceSpec );
    }

    /**
     * Adapt definition to retracted resourceSpec, if applicable.
     *
     * @param resourceSpec a resourceSpec being retracted
     */
    public void removeResourceSpec( ResourceSpec resourceSpec ) {
        ResourceSpec partResourceSpec = resourceSpec();
        while ( partResourceSpec.narrowsOrEquals( resourceSpec ) ) {
            ModelObject entity = partResourceSpec.mostSpecificEntity();
            if ( entity instanceof Actor ) setActor( null );
            else if ( entity instanceof Role ) setRole( null );
            else if ( entity instanceof Organization ) setOrganization( null );
            else if ( entity instanceof Place ) setJurisdiction( null );
            else throw new IllegalArgumentException( "Can't unset entity " + entity );
            partResourceSpec = resourceSpec();
        }
    }

    /**
     * Whether the part is self-repeating.
     *
     * @return a boolean
     */
    @Transient
    public boolean isRepeating() {
        return repeating;
    }

    /**
     * Sets repeating attribute.
     *
     * @param val a boolean
     */
    @Transient
    public void setRepeating( boolean val ) {
        repeating = val;
        if ( val && repeatsEvery.getAmount() == 0 ) {
            repeatsEvery.setAmount( 1 );
            repeatsEvery.setUnit( Delay.Unit.days );
        }
    }

    /**
     * Whether the part is self-terminating.
     *
     * @return a boolean
     */
    @Transient
    public boolean isSelfTerminating() {
        return selfTerminating;
    }

    /**
     * Sets completion time attribute.
     *
     * @param val a boolean
     */
    @Transient
    public void setSelfTerminating( boolean val ) {
        selfTerminating = val;
        if ( val && completionTime.getAmount() == 0 ) {
            completionTime.setAmount( 1 );
            completionTime.setUnit( Delay.Unit.hours );
        }
    }

    /**
     * Test if this part is considered belonging to an organization.
     *
     * @param o the organization
     * @return true if belonging
     */
    public boolean isIn( Organization o ) {
        return organization == null ? Organization.UNKNOWN == o
                : o.equals( organization );
    }

    /**
     * Test if this part is played by a given role.
     *
     * @param r the role
     * @return true if played
     */
    public boolean isPlayedBy( Role r ) {
        return role == null ? r == Role.UNKNOWN
                : role.equals( r );
    }
}

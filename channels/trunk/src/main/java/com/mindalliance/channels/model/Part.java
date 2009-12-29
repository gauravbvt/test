package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.util.Matcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A part in a scenario.
 */
@Entity
public class Part extends Node implements GeoLocatable {

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
    /**
     * Whether this part is started by the onset of the scenario.
     */
    private boolean startsWithScenario;
    /**
     * Whether the part can terminate the event phase the scenario is about.
     */
    private boolean terminatesEventPhase;
    /**
     * Event this part initiates.
     */
    private Event initiatedEvent;
    /**
     * Scenario risks mitigated.
     */
    private List<Risk> mitigations = new ArrayList<Risk>();

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

    @ManyToOne
    public Actor getActor() {
        return actor;
    }

    /**
     * Set the actor. Also resets name accordingly.
     *
     * @param actor the new actor.
     */
    public void setActor( Actor actor ) {
        Actor old = this.actor;
        this.actor = actor;
        if ( old == null || !old.equals( actor ) )
            adjustName();
    }

    @ManyToOne( fetch = FetchType.LAZY )
    public Place getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction( Place jurisdiction ) {
        this.jurisdiction = jurisdiction;
    }

    @ManyToOne( fetch = FetchType.LAZY )
    public Place getLocation() {
        return location;
    }

    public void setLocation( Place location ) {
        this.location = location;
    }

    @ManyToOne( fetch = FetchType.LAZY )
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Set the organization. Also resets name accordingly.
     *
     * @param organization the new organization.
     */
    public void setOrganization( Organization organization ) {
        Organization old = this.organization;
        this.organization = organization;
        if ( old == null || !old.equals( organization ) )
            adjustName();
    }

    @ManyToOne( fetch = FetchType.LAZY )
    public Role getRole() {
        return role;
    }

    /**
     * Set the role. Also resets name accordingly.
     *
     * @param role the new role.
     */
    public void setRole( Role role ) {
        Role old = this.role;
        this.role = role;
        if ( old == null || !old.equals( role ) )
            adjustName();
    }

    public Delay getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime( Delay completionTime ) {
        this.completionTime = completionTime;
    }

    @AttributeOverrides( {
        @AttributeOverride( name = "unit", column = @Column( name = "r_unit" ) ),
        @AttributeOverride( name = "amount", column = @Column( name = "r_amount" ) )
            } )
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
    public boolean isEmpty() {
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
        Actor a = getActor();
        if ( a != null && a.isSystem() ) return true;
        Role r = getRole();
        return r != null
                && r.isSystem();
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
    public boolean isImpliedBy( ResourceSpec resourceSpec ) {
        ResourceSpec partResourceSpec = resourceSpec();
        return !partResourceSpec.isAnyone() && resourceSpec.narrowsOrEquals( partResourceSpec );
    }

    /**
     * Whether the part is self-repeating.
     *
     * @return a boolean
     */
    public boolean isRepeating() {
        return repeating;
    }

    /**
     * Sets repeating attribute.
     *
     * @param val a boolean
     */
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
    public boolean isSelfTerminating() {
        return selfTerminating;
    }

    /**
     * Sets completion time attribute.
     *
     * @param val a boolean
     */
    public void setSelfTerminating( boolean val ) {
        selfTerminating = val;
        if ( val && completionTime.getAmount() == 0 ) {
            completionTime.setAmount( 1 );
            completionTime.setUnit( Delay.Unit.hours );
        }
    }

    public boolean isStartsWithScenario() {
        return startsWithScenario;
    }

    public void setStartsWithScenario( boolean startsWithScenario ) {
        this.startsWithScenario = startsWithScenario;
    }

    public boolean isTerminatesEventPhase() {
        return terminatesEventPhase;
    }

    public void setTerminatesEventPhase( boolean terminatesEventPhase ) {
        this.terminatesEventPhase = terminatesEventPhase;
    }

    public Event getInitiatedEvent() {
        return initiatedEvent;
    }

    public void setInitiatedEvent( Event initiatedEvent ) {
        assert initiatedEvent == null || initiatedEvent.isType();
        this.initiatedEvent = initiatedEvent;
    }

    @OneToMany
    public List<Risk> getMitigations() {
        return mitigations;
    }

    public void setMitigations( List<Risk> mitigations ) {
        this.mitigations = mitigations;
    }

    /**
     * {@inheritDoc}
     *
     * @param queryService
     */
    public void beforeRemove( QueryService queryService ) {
        super.beforeRemove( queryService );
    }

    /**
     * Test if this part is considered belonging to an organization.
     *
     * @param o the organization
     * @return true if belonging
     */
    public boolean isIn( Organization o ) {
        return organization == null ? Organization.UNKNOWN == o
                : o.narrowsOrEquals( organization );
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

    /**
     * Iterate over all outcomes of the part of a given name.
     *
     * @param name a flow name
     * @return a boolean
     */
    @SuppressWarnings( "unchecked" )
    public Iterator<Flow> outcomesNamed( final String name ) {
        return new FilterIterator( outcomes(), new Predicate() {
            public boolean evaluate( Object object ) {
                Flow flow = (Flow) object;
                return Matcher.same( flow.getName(), name );
            }
        } );
    }

    /**
     * Iterate over all requirements of the part of a given name.
     *
     * @param name a flow name
     * @return a boolean
     */
    @SuppressWarnings( "unchecked" )
    public Iterator<Flow> requirementsNamed( final String name ) {
        return new FilterIterator( requirements(), new Predicate() {
            public boolean evaluate( Object object ) {
                Flow flow = (Flow) object;
                return Matcher.same( flow.getName(), name );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isUndefined() {
        return super.isUndefined()
                && isEmpty();
    }

    /**
     * Whether a flow triggers this part.
     *
     * @return a boolean
     */
    @Transient
    public boolean isTriggered() {
        Iterator<Flow> reqs = requirements();
        boolean triggered = false;
        while ( !triggered && reqs.hasNext() ) {
            Flow req = reqs.next();
            triggered = req.isTriggeringToTarget() && req.getSource().isPart();
        }
        if ( !triggered ) {
            Iterator<Flow> outs = outcomes();
            while ( !triggered && outs.hasNext() ) {
                Flow req = outs.next();
                triggered = req.isTriggeringToSource();
            }
        }
        return triggered;
    }

    /**
     * Whether a flow terminates this part.
     *
     * @return a boolean
     */
    @Transient
    public boolean isTerminated() {
        Iterator<Flow> reqs = requirements();
        boolean terminated = false;
        while ( !terminated && reqs.hasNext() ) {
            Flow req = reqs.next();
            terminated = req.isTerminatingToTarget();
        }
        if ( !terminated ) {
            Iterator<Flow> outs = outcomes();
            while ( !terminated && outs.hasNext() ) {
                Flow req = outs.next();
                terminated = req.isTerminatingToSource();
            }
        }
        return terminated;
    }

    /**
     * @return shorthand for role for jurisdiction
     */
    @Transient
    public String getRoleString() {
        StringBuilder b = new StringBuilder( 64 );

        if ( role != null )
            b.append( role );
        if ( jurisdiction != null ) {
            b.append( " for " );
            b.append( jurisdiction );
        }

        return b.toString();
    }

    /**
     * A part describes a Resource is not all of actor, roel and organization are null.
     *
     * @return boolean whether this part describes a resource
     */
    public boolean hasResource() {
        return !( actor == null
                && role == null
                && organization == null
                && jurisdiction == null );
    }

    /**
     * A part has a defined resource but with actor unknown.
     *
     * @return a boolean
     */
    public boolean hasNonActorResource() {
        return hasResource() && actor == null;
    }

    /**
     * Return the significance of a flow for this part.
     *
     * @param flow the flow
     * @return the significance
     */
    public Flow.Significance getSignificance( Flow flow ) {
        return equals( flow.getSource() ) ?
                flow.getSignificanceToSource() : flow.getSignificanceToTarget();
    }

    /**
     * Find explicit or implicit, single, actual actor, if any.
     *
     * @return an actor or null
     */
    @Transient
    public Actor getKnownActor() {
        if ( actor != null ) {
            return actor;
        } else {
            return getKnownActualActor();
        }
    }

    public Actor getKnownActualActor() {
        List<Actor> knownActors = getScenario().getQueryService().findAllActualActors( resourceSpec() );
        if ( knownActors.size() == 1 ) {
            return knownActors.get( 0 );
        } else {
            return null;
        }
    }

    /**
     * Get organization or unknown if null.
     *
     * @return an organization or null
     */
    @Transient
    public Organization getOrganizationOrUnknown() {
        return organization == null ? Organization.UNKNOWN : organization;
    }

    /**
     * Get role or unknown if null.
     *
     * @return a role or null
     */
    @Transient
    public Role getRoleOrUnknown() {
        return role == null ? Role.UNKNOWN : role;
    }

    /**
     * Get actor or unknown if null.
     *
     * @return an actor or null
     */
    @Transient
    public Actor getActorOrUnknown() {
        return actor == null ? Actor.UNKNOWN : actor;
    }

    /**
     * Get jurisdiction or unknown if null.
     *
     * @return a place or null
     */
    @Transient
    public Place getJurisdictionOrUnknown() {
        return jurisdiction == null ? Place.UNKNOWN : jurisdiction;
    }

    /**
     * Get extended title for the part.
     *
     * @param sep separator string
     * @return a string
     */
    @Transient
    public String getFullTitle( String sep ) {
        String label = "";
        if ( getActor() != null ) {
            label += getActor().getName();
            if ( getActor().isType() ) {
                Actor impliedActor = getKnownActualActor();
                if ( impliedActor != null ) {
                    label += " (" + impliedActor.getName() + ")";
                }
            }
        }
        if ( getRole() != null ) {
            if ( !label.isEmpty() ) label += sep;
            if ( getActor() == null ) {
                Actor impliedActor = getKnownActualActor();
                if ( impliedActor != null ) {
                    label += impliedActor.getName();
                    label += " ";
                }
            }
            if ( !label.isEmpty() ) label += "as ";
            label += getRole().getName();
        }
        if ( getJurisdiction() != null ) {
            if ( !label.isEmpty() ) label += ( sep + "for " );
            label += getJurisdiction().getName();
        }
        if ( getOrganization() != null ) {
            if ( !label.isEmpty() ) label += ( sep + "at " );
            label += getOrganization().getName();
        }
        if ( !label.isEmpty() ) label += sep;
        label += getTask();
        if ( isRepeating() ) {
            label += " (every " + getRepeatsEvery().toString() + ")";
        }
        return label;
    }

    /**
     * Get summary of the part.
     *
     * @return a string
     */
    @Transient
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append( "Task \"" );
        sb.append( getTask() );
        sb.append( "\" is executed" );
        if ( getLocation() != null ) {
            sb.append( " at location \"" );
            sb.append( getLocation().getName() );
            sb.append( "\"" );
        }
        sb.append( " by " );
        if ( getActor() != null ) {
            sb.append( getActor().getName() );
            if ( getActor().isType() ) {
                Actor impliedActor = getKnownActualActor();
                if ( impliedActor != null ) {
                    sb.append( " " );
                    sb.append( impliedActor.getName() );
                }
            }
        }
        if ( getRole() != null ) {
            if ( !sb.toString().isEmpty() ) sb.append( ' ' );
            if ( getActor() == null ) {
                Actor impliedActor = getKnownActualActor();
                if ( impliedActor != null ) {
                    sb.append( impliedActor.getName() );
                } else {
                    sb.append( "any " );
                }
            }
            if ( getKnownActualActor() != null ) {
                if ( getActor() != null ) {
                    sb.append( " as " );
                } else {
                    sb.append( " as the only " );
                }
            }
            sb.append( getRole().getName() );
        }
        if ( getActor() == null && getRole() == null ) {
            sb.append( "someone");
        }
        if ( getJurisdiction() != null ) {
            if ( !sb.toString().isEmpty() ) sb.append( " for " );
            sb.append( getJurisdiction().getName() );
        }
        if ( getOrganization() != null ) {
            if ( !sb.toString().isEmpty() ) sb.append( " at " );
            sb.append( getOrganization().getName() );
        }
        sb.append( "." );
        if ( isRepeating()
                || isSelfTerminating()
                || initiatesEvent()
                || isStartsWithScenario()
                || isTerminatesEventPhase() ) {
            sb.append( " The task" );
            StringBuilder sb1 = new StringBuilder();
            if ( isStartsWithScenario() ) {
                sb1.append( " starts with the scenario" );
            }
            if ( isRepeating() ) {
                if ( !sb1.toString().isEmpty() ) {
                    if ( !initiatesEvent() && !isSelfTerminating() && !isTerminatesEventPhase() ) {
                        sb1.append( " and" );
                    } else {
                        sb1.append( "," );
                    }
                }
                sb1.append( " is repeated every " );
                sb1.append( getRepeatsEvery().toString() );
            }
            if ( initiatesEvent() ) {
                if ( !sb1.toString().isEmpty() ) {
                    if ( !isSelfTerminating() && !isTerminatesEventPhase() ) {
                        sb1.append( " and" );
                    } else {
                        sb1.append( "," );
                    }
                }
                sb1.append( " initiates event \"" );
                sb1.append( getInitiatedEvent().getName() );
                sb1.append( "\"" );
            }
            if ( isTerminatesEventPhase() ) {
                if ( !sb1.toString().isEmpty() ) {
                    if ( !isSelfTerminating() ) {
                        sb1.append( " and" );
                    } else {
                        sb1.append( "," );
                    }
                }
                sb1.append( " can end this scenario" );
            }
            if ( isSelfTerminating() ) {
                if ( !sb1.toString().isEmpty() ) sb1.append( " and" );
                sb1.append( " terminates by itself" );
            }
            sb1.append( "." );
            sb.append( sb1 );
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public GeoLocation geoLocate() {
        return location != null ? location.geoLocate() : null;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends GeoLocatable> getImpliedGeoLocatables( QueryService queryService ) {
        List<Part> geoLocatables = new ArrayList<Part>();
        geoLocatables.add( this );
        return geoLocatables;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getGeoMarkerLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append( getFullTitle( " " ) );
        if ( location != null ) {
            sb.append( " at " );
            sb.append( getLocation().getName() );
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getModelObjectType() {
        return "Task";
    }

    /**
     * Whether this part initiates an event.
     *
     * @return a boolean
     */
    public boolean initiatesEvent() {
        return getInitiatedEvent() != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean references( final ModelObject mo ) {
        return ModelObject.areIdentical( actor, mo )
                || ModelObject.areIdentical( role, mo )
                || ModelObject.areIdentical( jurisdiction, mo )
                || ModelObject.areIdentical( organization, mo )
                || ModelObject.areIdentical( location, mo )
                || ModelObject.areIdentical( initiatedEvent, mo )
                ||
                CollectionUtils.exists(
                        mitigations,
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return ( (Risk) obj ).references( mo );
                            }
                        } );
    }

    /**
     * Has actual actor.
     *
     * @return a boolean
     */
    public boolean hasActualActor() {
        return actor != null && actor.isActual();
    }

    /**
     * Has actual role.
     *
     * @return a boolean
     */
    public boolean hasActualRole() {
        return role != null && role.isActual();
    }

    /**
     * Get all needs.
     *
     * @return a list of flows
     */
    @Transient
    @SuppressWarnings( "unchecked" )
    public List<Flow> getNeeds() {
        return (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList( requirements() ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Flow) obj ).isNeed();
                    }
                }
        );
    }
}

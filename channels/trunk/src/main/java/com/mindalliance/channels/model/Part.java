package com.mindalliance.channels.model;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.util.Matcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A part in a segment.
 */
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
     * Whether this part is started by the onset of the segment.
     */
    private boolean startsWithSegment;
    /**
     * Whether the part can terminate the event phase the segment is about.
     */
    private boolean terminatesEventPhase;
    /**
     * Event this part initiates.
     */
    private Event initiatedEvent;
    /**
     * Segment goals achieved - risks mitigated or gains made.
     */
    private List<Goal> goals = new ArrayList<Goal>();
    /**
     * Whether the assignees execute the task as a team vs individually.
     */
    private boolean asTeam;

    public Part() {
        adjustName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return MessageFormat.format( "{0} {1}", getName(), WordUtils.uncapitalize( getTask() ) );
    }

    /**
     * {@inheritDoc}
     */
    public String getTypeName() {
        return "task";
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

    public Place getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction( Place jurisdiction ) {
        this.jurisdiction = jurisdiction;
    }

    public Place getLocation() {
        return location;
    }

    public void setLocation( Place location ) {
        this.location = location;
    }

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

    public Delay getRepeatsEvery() {
        return repeatsEvery;
    }

    public void setRepeatsEvery( Delay repeatsEvery ) {
        this.repeatsEvery = repeatsEvery;
    }

    @Override
    public boolean isPart() {
        return true;
    }

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

    public boolean isStartsWithSegment() {
        return startsWithSegment;
    }

    public void setStartsWithSegment( boolean startsWithSegment ) {
        this.startsWithSegment = startsWithSegment;
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

    public boolean isAsTeam() {
        return asTeam;
    }

    public void setAsTeam( boolean asTeam ) {
        this.asTeam = asTeam;
    }

    /**
     * Get all goals that are risk mitigations.
     *
     * @return a list of goals
     */
    @SuppressWarnings( "unchecked" )
    public List<Goal> getMitigations() {
        return (List<Goal>) CollectionUtils.select(
                getGoals(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (Goal) object ).isRiskMitigation();
                    }
                }
        );
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals( List<Goal> goals ) {
        this.goals = goals;
    }

    /**
     * Test if this part is considered belonging to an organization.
     *
     * @param o the organization
     * @return true if belonging
     */
    public boolean isInOrganization( Organization o ) {
        return organization == null ? Organization.UNKNOWN == o
                : o.narrowsOrEquals( organization );
    }

    /**
     * Test if this part is considered belonging to an organization.
     *
     * @param j the jurisdiction
     * @return true if belonging
     */
    public boolean isInJurisdiction( Place j ) {
        return jurisdiction == null
                ? Place.UNKNOWN == j
                : j.narrowsOrEquals( jurisdiction );
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
     * Iterate over all sends of the part of a given name.
     *
     * @param name a flow name
     * @return a boolean
     */
    @SuppressWarnings( "unchecked" )
    public Iterator<Flow> sendsNamed( final String name ) {
        return new FilterIterator( sends(), new Predicate() {
            public boolean evaluate( Object object ) {
                Flow flow = (Flow) object;
                return Matcher.same( flow.getName(), name );
            }
        } );
    }

    /**
     * Iterate over all receives of the part of a given name.
     *
     * @param name a flow name
     * @return a boolean
     */
    @SuppressWarnings( "unchecked" )
    public Iterator<Flow> receivesNamed( final String name ) {
        return new FilterIterator( receives(), new Predicate() {
            public boolean evaluate( Object object ) {
                Flow flow = (Flow) object;
                return Matcher.same( flow.getName(), name );
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndefined() {
        return super.isUndefined()
                && isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public String displayString() {
        return displayString( Integer.MAX_VALUE );
    }

    /**
     * {@inheritDoc}
     */
    public String displayString( int maxItemLength ) {
        return resourceSpec().displayString( maxItemLength )
                + " (" + StringUtils.abbreviate( getTask(), maxItemLength ) + ")";
    }

    /**
     * {@inheritDoc}
     */
    public String fullDisplayString( int maxItemLength ) {
        return displayString( maxItemLength );
    }

    /**
     * Whether a flow triggers this part.
     *
     * @return a boolean
     */
    public boolean isTriggered() {
        Iterator<Flow> receives = receives();
        boolean triggered = false;
        while ( !triggered && receives.hasNext() ) {
            Flow receive = receives.next();
            triggered = receive.isTriggeringToTarget() && receive.getSource().isPart();
        }
        if ( !triggered ) {
            Iterator<Flow> sends = sends();
            while ( !triggered && sends.hasNext() ) {
                Flow send = sends.next();
                triggered = send.isTriggeringToSource();
            }
        }
        return triggered;
    }

    /**
     * Whether a flow terminates this part.
     *
     * @return a boolean
     */
    public boolean isTerminated() {
        Iterator<Flow> receives = receives();
        boolean terminated = false;
        while ( !terminated && receives.hasNext() ) {
            Flow receive = receives.next();
            terminated = receive.isTerminatingToTarget();
        }
        if ( !terminated ) {
            Iterator<Flow> sends = sends();
            while ( !terminated && sends.hasNext() ) {
                Flow send = sends.next();
                terminated = send.isTerminatingToSource();
            }
        }
        return terminated;
    }

    /**
     * @return shorthand for role for jurisdiction
     */
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
    public boolean hasNonActualActorResource() {
        return hasResource() && ( actor == null || actor.isType() );
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
    public Actor getKnownActor() {
        if ( actor != null ) {
            return actor;
        } else {
            return getKnownActualActor();
        }
    }

    public Actor getKnownActualActor() {
        List<Actor> knownActors = getSegment().getQueryService().findAllActualActors( resourceSpec() );
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
    public Organization getOrganizationOrUnknown() {
        return organization == null ? Organization.UNKNOWN : organization;
    }

    /**
     * Get role or unknown if null.
     *
     * @return a role or null
     */
    public Role getRoleOrUnknown() {
        return role == null ? Role.UNKNOWN : role;
    }

    /**
     * Get actor or unknown if null.
     *
     * @return an actor or null
     */
    public Actor getActorOrUnknown() {
        return actor == null ? Actor.UNKNOWN : actor;
    }

    /**
     * Get jurisdiction or unknown if null.
     *
     * @return a place or null
     */
    public Place getJurisdictionOrUnknown() {
        return jurisdiction == null ? Place.UNKNOWN : jurisdiction;
    }

    /**
     * Get extended title for the part.
     *
     * @param sep separator string
     * @return a string
     */
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
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
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
                    sb.append( "Any " );
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
            sb.append( "Someone" );
        }
        if ( getJurisdiction() != null ) {
            if ( !sb.toString().isEmpty() ) sb.append( " for " );
            sb.append( getJurisdiction().getName() );
        }
        if ( getOrganization() != null ) {
            if ( !sb.toString().isEmpty() ) sb.append( " at " );
            sb.append( getOrganization().getName() );
        }
        sb.append( " is assigned task \"" );
        sb.append( getTask() );
        sb.append( "\"" );
        if ( getLocation() != null ) {
            sb.append( " at location \"" );
            sb.append( getLocation().getName() );
            sb.append( "\"" );
        }
        sb.append( "." );
        if ( isRepeating()
                || isSelfTerminating()
                || initiatesEvent()
                || isStartsWithSegment()
                || isTerminatesEventPhase() ) {
            sb.append( " The task" );
            StringBuilder sb1 = new StringBuilder();
            if ( isStartsWithSegment() ) {
                sb1.append( " starts with the plan segment" );
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
                sb1.append( " can end this plan segment" );
            }
            if ( isSelfTerminating() ) {
                if ( !sb1.toString().isEmpty() ) sb1.append( " and" );
                sb1.append( " terminates by itself" );
            }
            sb1.append( "." );
            sb.append( sb1 );
        }
        if ( isAsTeam() ) {
            sb.append( " Assignees work as a team." );
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
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
                        goals,
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return ( (Goal) obj ).references( mo );
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
    @SuppressWarnings( "unchecked" )
    public List<Flow> getNeeds() {
        return (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList( receives() ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Flow) obj ).isNeed();
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    public List<Flow> getEssentialFlows( boolean assumeFails, QueryService queryService ) {
        return queryService.findEssentialFlowsFrom( this, assumeFails );
    }

    /**
     * A part is useful if it mitigates risks
     * or it terminates its segment and at least one of the risks terminates with it.
     *
     * @return a boolean
     */
    public boolean isUseful() {
        return !goals.isEmpty()
                || ( isTerminatesEventPhase() && getSegment().hasTerminatingRisks() );
    }

    /**
     * Get all explicit sharing sends from connected capabilities.
     *
     * @return a list of flows
     */
    public List<Flow> getAllSharingSends() {
        List<Flow> sharingSends = new ArrayList<Flow>();
        Iterator<Flow> sends = sends();
        while ( sends.hasNext() ) {
            Flow flow = sends.next();
            if ( flow.isSharing() ) {
                sharingSends.add( flow );
            } else if ( flow.isCapability() ) {
                Connector connector = (Connector) flow.getTarget();
                Iterator<ExternalFlow> externals = connector.externalFlows();
                while ( externals.hasNext() ) {
                    sharingSends.add( externals.next() );
                }
            }
        }
        return sharingSends;
    }

    /**
     * Get the maximum severity of risks mitigated. Null if none.
     *
     * @return a severity level
     */
    public Level getMaxMitigatedRiskSeverity() {
        Level maxSeverity = null;
        List<Goal> goals = new ArrayList<Goal>( getMitigations() );
        if ( !goals.isEmpty() ) {
            Collections.sort( goals, new Comparator<Goal>() {
                public int compare( Goal r1, Goal r2 ) {
                    return r1.getLevel().compareTo( r2.getLevel() ) * -1;
                }
            } );
            maxSeverity = goals.get( 0 ).getLevel();
        }
        return maxSeverity;
    }

    /**
     * Whether the failure of this part could possibly matter.
     *
     * @return a boolean
     */
    public boolean isImportant() {
        return isUseful() ||
                CollectionUtils.exists(
                        IteratorUtils.toList( sends() ),
                        new Predicate() {
                            public boolean evaluate( Object object ) {
                                return ( (Flow) object ).isImportant();
                            }
                        }
                );
    }

    /**
     * Get all goals achieved.
     *
     * @return a list of risks
     */
    public List<Goal> getGoalsAchieved() {
        Set<Goal> goals = new HashSet<Goal>();
        goals.addAll( getGoals() );
        if ( isTerminatesEventPhase() && getSegment().hasTerminatingRisks() ) {
            goals.addAll( getSegment().getRisks() );
        }
        return new ArrayList<Goal>( goals );
    }

    /**
     * Find a need for given info.
     *
     * @param info a string
     * @return a flow or null
     */
    public Flow findNeed( String info ) {
        Iterator<Flow> candidates = receivesNamed( info );
        while ( candidates.hasNext() ) {
            Flow candidate = candidates.next();
            if ( candidate.isNeed() ) return candidate;
        }
        return null;
    }

    /**
     * Find a capability for given info.
     *
     * @param info a string
     * @return a flow or null
     */
    public Flow findCapability( String info ) {
        Iterator<Flow> candidates = sendsNamed( info );
        while ( candidates.hasNext() ) {
            Flow candidate = candidates.next();
            if ( candidate.isCapability() ) return candidate;
        }
        return null;
    }
}

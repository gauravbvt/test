package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A part in a segment.
 */
public class Part extends Node implements GeoLocatable, Specable, Prohibitable {

    /**
     * Default actor label, when unknown.
     */
    public static final String DEFAULT_ACTOR = "Somebody";

    /**
     * Default task name.
     */
    static public String DEFAULT_TASK = "doing unnamed task";

    /**
     * The task label of this part (never null or empty).
     */
    private String task = DEFAULT_TASK;

    private Function function;

    private ResourceSpec spec = new ResourceSpec();

    /**
     * The location (optional).
     */
    private AssignedLocation location = new AssignedLocation();

    /**
     * Whether the part's task completes on its own after some time.
     */
    private boolean selfTerminating;

    /**
     * Usual time for the task to complete on its own. If null, the task must be terminated.
     */
    private Delay completionTime = new Delay();

    /**
     * Whether the part's task repeats (at fixed intervals).
     */
    private boolean repeating;

    /**
     * How long before the task is repeated. Not repeated if null.
     */
    private Delay repeatsEvery = new Delay();

    /**
     * Whether this part is started by the onset of the segment.
     */
    private boolean startsWithSegment;
    /**
     * Whether a part is ongoing, irrespective of the segment.
     */
    private boolean ongoing;

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

    /**
     * Task categorization.
     */
    private Category category;

    /**
     * Whether the part is prohibited.
     */
    private boolean prohibited = false;

    private Checklist checklist;

    public Part() {
        adjustName();
        checklist = new Checklist( this );
    }

    public static String classLabel() {
        return "tasks";
    }

    @Override
    public String getClassLabel() {
        return classLabel();
    }


    @Override
    public String getLabel() {
        return getTitle();
    }

    @Override
    public String getTitle() {
        return MessageFormat.format( "{0} - {1}", getName(), WordUtils.uncapitalize( task ) );
    }

    @Override
    public String getTypeName() {
        return "task";
    }

    @Override
    public String getKindLabel() {
        return "Task";
    }

    public String getTask() {
        return task;
    }

    public void setTask( String task ) {
        this.task = task == null || task.length() == 0 ? DEFAULT_TASK : task;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction( Function function ) {
        this.function = function;
    }

    public Checklist getChecklist() {
        return checklist;
    }

    public Checklist getEffectiveChecklist() {
        return checklist == null
                ? new Checklist( this )
                : checklist;
    }

    public void setChecklist( Checklist checklist ) {
        this.checklist = checklist;
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

    @Override
    public Actor getActor() {
        return spec.getActor();
    }

    /**
     * Set the actor. Also resets name accordingly.
     *
     * @param actor the new actor.
     */
    public void setActor( Actor actor ) {
        Actor old = getActor();
        spec = new ResourceSpec( actor, getRole(), getOrganization(), getJurisdiction() );
        if ( old == null || !old.equals( actor ) )
            adjustName();
    }

    @Override
    public Place getJurisdiction() {
        return spec.getJurisdiction();
    }

    public void setJurisdiction( Place jurisdiction ) {
        spec = new ResourceSpec( getActor(), getRole(), getOrganization(), jurisdiction );
    }

    public AssignedLocation getLocation() {
        return location;
    }

    public void setLocation( AssignedLocation location ) {
        this.location = location;
    }

    @Override
    public Organization getOrganization() {
        return spec.getOrganization();
    }

    /**
     * Set the organization. Also resets name accordingly.
     *
     * @param organization the new organization.
     */
    public void setOrganization( Organization organization ) {
        Organization old = getOrganization();
        spec = new ResourceSpec( getActor(), getRole(), organization, getJurisdiction() );
        if ( old == null || !old.equals( organization ) )
            adjustName();
    }

    @Override
    public Role getRole() {
        return spec.getRole();
    }

    /**
     * Set the role. Also resets name accordingly.
     *
     * @param role the new role.
     */
    public void setRole( Role role ) {
        Role old = getRole();
        spec = new ResourceSpec( getActor(), role, getOrganization(), getJurisdiction() );
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

    public Category getCategory() {
        return category;
    }

    public void setCategory( Category category ) {
        this.category = category;
    }

    @Override
    public boolean isPart() {
        return true;
    }

    public boolean isEmpty() {
        return spec.isAnyone() && spec.getJurisdiction() == null && location == null;
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
        return a != null && a.isSystem();
    }

    /**
     * @return true if part is only specified by a role or agent type.
     */
    public boolean isOnlyRoleOrAgentType() {
        return spec.isRole() || ( spec.getActor() != null && spec.getActor().isType() );
    }

    /**
     * Gets the resourceSpec implied by the part
     *
     * @return a ResourceSpec
     */
    public ResourceSpec resourceSpec() {
        return spec;
    }

    /**
     * Test whether the resource spec of the part intersects a given resource spec
     *
     * @param resourceSpec a resource
     * @param locale       the default location
     * @return a boolean
     */
    public boolean isImpliedBy( ResourceSpec resourceSpec, Place locale ) {
        return !spec.isAnyone() && resourceSpec.narrowsOrEquals( spec, locale );
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

    public boolean isOngoing() {
        return ongoing;
    }

    public void setOngoing( boolean ongoing ) {
        this.ongoing = ongoing;
    }

    public boolean isAutoStarted() {
        return isOngoing() || isStartsWithSegment();
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
        return (List<Goal>) CollectionUtils.select( goals, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (Goal) object ).isRiskMitigation();
            }
        } );
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals( List<Goal> goals ) {
        this.goals = goals;
    }

    public void addGoal( Goal goal ) {
        if ( !goals.contains( goal ) ) {
            goals.add( goal );
        }
    }

    public boolean isProhibited() {
        return prohibited;
    }

    public void setProhibited( boolean prohibited ) {
        this.prohibited = prohibited;
    }

    /**
     * Test if this part is considered belonging to an organization.
     *
     * @param o      the organization
     * @param locale the default location
     * @return true if belonging
     */
    public boolean isForOrganization( Organization o, Place locale ) {
        return o.narrowsOrEquals( spec.getOrganization(), locale );
    }

    /**
     * Test if this part is considered belonging to an organization.
     *
     * @param j      the jurisdiction
     * @param locale the default location
     * @return true if belonging
     */
    public boolean isInJurisdiction( Place j, Place locale ) {
        return j.narrowsOrEquals( spec.getJurisdiction(), locale );
    }

    /**
     * Test if this part is played by a given role.
     *
     * @param r the role
     * @return true if played
     */
    public boolean isPlayedBy( Role r ) {
        return r.narrowsOrEquals( spec.getRole(), null );
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
            @Override
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
            @Override
            public boolean evaluate( Object object ) {
                Flow flow = (Flow) object;
                return Matcher.same( flow.getName(), name );
            }
        } );
    }

    @Override
    public boolean isUndefined() {
        return super.isUndefined() && isEmpty();
    }

    @Override
    public String displayString() {
        return displayString( Integer.MAX_VALUE );
    }

    @Override
    public String displayString( int maxItemLength ) {
        return resourceSpec().displayString( maxItemLength ) + " (" + StringUtils.abbreviate( task, maxItemLength )
                + ")";
    }

    @Override
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

        Role role = getRole();
        if ( role != null )
            b.append( role );

        Place place = getJurisdiction();
        if ( place != null ) {
            b.append( " for " );
            b.append( place );
        }

        return b.toString();
    }

    /**
     * A part describes a Resource is not all of actor, roel and organization are null.
     *
     * @return boolean whether this part describes a resource
     */
    public boolean hasResource() {
        return !spec.isAnyone() || spec.getJurisdiction() != null;
    }

    /**
     * A part has a defined resource but with actor unknown.
     *
     * @return a boolean
     */
    public boolean hasNonActualActorResource() {
        return hasResource() && ( getActor() == null || getActor().isType() );
    }

    /**
     * Return the significance of a flow for this part.
     *
     * @param flow the flow
     * @return the significance
     */
    public Flow.Significance getSignificance( Flow flow ) {
        return equals( flow.getSource() ) ? flow.getSignificanceToSource() : flow.getSignificanceToTarget();
    }

    /**
     * Get organization or unknown if null.
     *
     * @return an organization or null
     */
    public Organization getOrganizationOrUnknown() {
        return spec.isAnyOrganization() ? Organization.UNKNOWN : getOrganization();
    }

    /**
     * Get role or unknown if null.
     *
     * @return a role or null
     */
    public Role getRoleOrUnknown() {
        return spec.isAnyRole() ? Role.UNKNOWN : getRole();
    }

    /**
     * Get actor or unknown if null.
     *
     * @return an actor or null
     */
    public Actor getActorOrUnknown() {
        return spec.isAnyActor() ? Actor.UNKNOWN : getActor();
    }

    /**
     * Get jurisdiction or unknown if null.
     *
     * @return a place or null
     */
    public Place getJurisdictionOrUnknown() {
        return spec.isAnyJurisdiction() ? Place.UNKNOWN : getJurisdiction();
    }

    @Override
    public Place getPlaceBasis() {
        return location == null ? null : location.getPlaceBasis();
    }

    @Override
    public List<? extends GeoLocatable> getImpliedGeoLocatables() {
        List<Part> geoLocatables = new ArrayList<Part>();
        geoLocatables.add( this );
        return geoLocatables;
    }

    @Override
    public String getGeoMarkerLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append( getTitle() );
        if ( getKnownLocation() != null ) {
            sb.append( " at " );
            sb.append( getKnownLocation().getName() );
        }
        return sb.toString();
    }

    @Override
    public String getModelObjectType() {
        return "Task";
    }

    /**
     * Whether this part initiates an event.
     *
     * @return a boolean
     */
    public boolean initiatesEvent() {
        return initiatedEvent != null;
    }

    @Override
    public boolean references( final ModelObject mo ) {
        return ModelObject.areIdentical( getActor(), mo ) || ModelObject.areIdentical( getRole(), mo )
                || ModelObject.areIdentical( getJurisdiction(), mo )
                || ModelObject.areIdentical( getOrganization(), mo )
                || ModelObject.areIdentical( location.getNamedPlace(), mo )
                || ModelObject.areIdentical( initiatedEvent, mo )
                || ModelObject.areIdentical( function, mo )
                || CollectionUtils.exists( goals, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (Goal) object ).references( mo );
            }
        } );
    }

    /**
     * Has actual actor.
     *
     * @return a boolean
     */
    public boolean hasActualActor() {
        return getActor() != null && getActor().isActual();
    }

    /**
     * Has actual role.
     *
     * @return a boolean
     */
    public boolean hasActualRole() {
        return getRole() != null && getRole().isActual();
    }

    /**
     * Get all needs.
     *
     * @return a list of flows
     */
    @SuppressWarnings( "unchecked" )
    public List<Flow> getNeeds() {
        return (List<Flow>) CollectionUtils.select( IteratorUtils.toList( receives() ), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (Flow) object ).isNeed();
            }
        } );
    }

    /**
     * Get all capabilities.
     *
     * @return a list of flows
     */
    @SuppressWarnings( "unchecked" )
    public List<Flow> getCapabilities() {
        return (List<Flow>) CollectionUtils.select( IteratorUtils.toList( sends() ), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (Flow) object ).isCapability();
            }
        } );
    }

    /**
     * A part is useful if it mitigates risks or it terminates its segment and at least one of the risks terminates with
     * it.
     *
     * @return a boolean
     */
    public boolean isUseful() {
        return !goals.isEmpty() || terminatesEventPhase && getSegment().hasTerminatingGoals();
    }

    /**
     * Get all explicit sharing sends plus those from connected capabilities.
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
     * Get all explicit sharing receives.
     *
     * @return a list of flows
     */
    public List<Flow> getAllSharingReceives() {
        List<Flow> sharingReceives = new ArrayList<Flow>();
        Iterator<Flow> receives = receives();
        while ( receives.hasNext() ) {
            Flow flow = receives.next();
            if ( flow.isSharing() ) {
                sharingReceives.add( flow );
            }
        }
        return sharingReceives;
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
                @Override
                public int compare( Goal o1, Goal o2 ) {
                    return o1.getLevel().compareTo( o2.getLevel() ) * -1;
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
        return isUseful() || CollectionUtils.exists( IteratorUtils.toList( sends() ), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (Flow) object ).isImportant();
            }
        } );
    }

    /**
     * Get all goals achieved.
     *
     * @return a list of risks
     */
    public List<Goal> getGoalsAchieved() {
        Set<Goal> goals = new HashSet<Goal>();
        goals.addAll( this.goals );
        if ( terminatesEventPhase ) {
            goals.addAll( getSegment().getTerminatingGoals() );
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
            if ( candidate.isNeed() )
                return candidate;
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
            if ( candidate.isCapability() )
                return candidate;
        }
        return null;
    }

    @Override
    public List<AttachmentImpl.Type> getAttachmentTypes() {
        List<AttachmentImpl.Type> types = super.getAttachmentTypes();
        types.add( AttachmentImpl.Type.PolicyMust );
        types.add( AttachmentImpl.Type.PolicyCant );
        return types;
    }

    /**
     * Serialize the state of the part, minus its flows
     *
     * @return a map of attribute names and values
     */
    public Map<String, Object> mapState() {
        Map<String, Object> state = super.mapState();
        state.put( "task", task );
        state.put( "repeatsEvery", new Delay( repeatsEvery ) );
        state.put( "completionTime", new Delay( completionTime ) );
        state.put( "selfTerminating", selfTerminating );
        state.put( "repeating", repeating );
        state.put( "terminatesEventPhase", terminatesEventPhase );
        state.put( "startsWithSegment", startsWithSegment );
        state.put( "ongoing", ongoing );
        state.put( "asTeam", asTeam );
        state.put( "prohibited", asTeam );
        state.put( "category", getCategory() );
        List<Map<String, Object>> goalMaps = new ArrayList<Map<String, Object>>();
        for ( Goal goal : goals ) {
            goalMaps.add( goal.toMap() );
        }
        state.put( "goals", goalMaps );
        if ( initiatedEvent != null )
            state.put( "initiatedEvent", initiatedEvent.getName() );
        if ( getActor() != null )
            state.put( "actor", Arrays.asList( getActor().getName(), getActor().isType() ) );
        if ( getRole() != null )
            state.put( "role", Arrays.asList( getRole().getName(), getRole().isType() ) );
        if ( getOrganization() != null )
            state.put( "organization", Arrays.asList( getOrganization().getName(), getOrganization().isType() ) );
        if ( getJurisdiction() != null )
            state.put( "jurisdiction", Arrays.asList( getJurisdiction().getName(), getJurisdiction().isType() ) );
        if ( location != null )
            state.put( "location", location.mapState() );
        return state;
    }

    @SuppressWarnings( "unchecked" )
    public void initFromMap( Map<String, Object> state, CommunityService communityService ) {
        super.initFromMap( state, communityService );
        PlanService planService = communityService.getPlanService();
        setTask( (String) state.get( "task" ) );
        setRepeatsEvery( (Delay) state.get( "repeatsEvery" ) );
        setCompletionTime( (Delay) state.get( "completionTime" ) );
        setSelfTerminating( (Boolean) state.get( "selfTerminating" ) );
        setRepeating( (Boolean) state.get( "repeating" ) );
        setTerminatesEventPhase( (Boolean) state.get( "terminatesEventPhase" ) );
        setStartsWithSegment( (Boolean) state.get( "startsWithSegment" ) );
        setOngoing( (Boolean) state.get( "ongoing" ) );
        setAsTeam( (Boolean) state.get( "asTeam" ) );
        setProhibited( (Boolean) state.get( "prohibited" ) );
        setCategory( (Category) state.get( "category" ) );
        for ( Map<String, Object> goalMap : (List<Map<String, Object>>) state.get( "goals" ) )
            addGoal( planService.goalFromMap( goalMap ) );
        if ( state.get( "initiatedEvent" ) == null )
            setInitiatedEvent( null );
        else
            setInitiatedEvent( planService.findOrCreateType( Event.class,
                    (String) state.get( "initiatedEvent" ) ) );
        if ( state.get( "actor" ) != null )
            setActor( planService.retrieveEntity( Actor.class, state, "actor" ) );
        else
            setActor( null );
        if ( state.get( "role" ) != null )
            setRole( planService.retrieveEntity( Role.class, state, "role" ) );
        else
            setRole( null );
        if ( state.get( "organization" ) != null )
            setOrganization( planService.retrieveEntity( Organization.class, state, "organization" ) );
        else
            setOrganization( null );
        if ( state.get( "jurisdiction" ) != null )
            setJurisdiction( planService.retrieveEntity( Place.class, state, "jurisdiction" ) );
        else
            setJurisdiction( null );
        if ( state.get( "location" ) != null ) {
            AssignedLocation assignedLocation = new AssignedLocation();
            assignedLocation.initFromMap(
                    (Map<String, Object>) state.get( "location" ),
                    communityService );
            setLocation( assignedLocation );
        } else
            setLocation( null );
    }


    /**
     * Get task with category, if any.
     *
     * @return a string
     */
    public String getTaskWithCategory() {
        String str = task;
        if ( category != null ) {
            str += " (" + category.getLabel().toLowerCase() + ')';
        }
        return str;
    }

    /**
     * Whether a flow is a send of the part.
     *
     * @param flow a flow
     * @return a boolean
     */
    public boolean isSend( Flow flow ) {
        Iterator<Flow> sends = sends();
        while ( sends.hasNext() ) {
            if ( sends.next().equals( flow ) )
                return true;
        }
        return false;
    }

    /**
     * List all subjects (info+eoi content) shared with or by the part.
     *
     * @param sent a boolean
     * @return a sorted list of unique subjects
     */
    public List<Subject> getAllSubjectsShared( boolean sent ) {
        Set<Subject> subjects = new HashSet<Subject>();
        List<Flow> flows = sent ? getAllSharingSends() : getAllSharingReceives();
        for ( Flow flow : flows ) {
            for ( ElementOfInformation eoi : flow.getEffectiveEois() ) {
                Subject subject = new Subject( flow.getName(), eoi.getContent() );
                subject.setRoot( sent );
                subjects.add( subject );
            }
        }
        List<Subject> results = new ArrayList<Subject>( subjects );
        Collections.sort( results );
        return results;
    }

    /**
     * List all subjects (info+eoi content) known to the part.
     *
     * @param sent a boolean
     * @return a sorted list of unique subjects
     */
    public List<Subject> getAllSubjects( boolean sent ) {
        Set<Subject> subjects = new HashSet<Subject>();
        Iterator<Flow> flows = sent ? sends() : receives();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            for ( ElementOfInformation eoi : flow.getEffectiveEois() ) {
                Subject subject = new Subject( flow.getName(), eoi.getContent() );
                subject.setRoot( sent );
                subjects.add( subject );
            }
        }
        List<Subject> results = new ArrayList<Subject>( subjects );
        Collections.sort( results );
        return results;
    }

    /**
     * Find a need for a subject, if any.
     *
     * @param subject a Subject (info name + element name)
     * @return a flow or null
     */
    public Flow findNeedFor( final Subject subject ) {
        return (Flow) CollectionUtils.find( getNeeds(), new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                Flow need = (Flow) object;
                return Matcher.same( subject.getInfo(), need.getName() )
                        && CollectionUtils.exists( need.getEffectiveEois(), new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return Matcher.same( ( (ElementOfInformation) object ).getContent(),
                                subject.getContent() );
                    }
                } );
            }
        } );
    }

    public boolean matchesTaskOf( Part other, Place locale ) {
        return Matcher.same( task, other.getTask() )
                && getSegment().impliesEventPhaseAndContextOf( other.getSegment(), locale );
    }

    /**
     * Whether this overrides another part.
     *
     * @param other  a part
     * @param locale a place
     * @return a boolean
     */
    public boolean overrides( Part other, Place locale ) {
        return !equals( other ) && !resourceSpec().equals( other.resourceSpec() ) && matchesTaskOf( other, locale )
                && resourceSpec().narrowsOrEquals( other.resourceSpec(), locale );
    }

    /**
     * Whether this equals or overrides another part.
     *
     * @param other  a part
     * @param locale a place
     * @return a boolean
     */
    public boolean overridesOrEquals( Part other, Place locale ) {
        return matchesTaskOf( other, locale ) && resourceSpec().narrowsOrEquals( other.resourceSpec(), locale );
    }

    public boolean hasActualOrganization() {
        return getOrganization() != null && getOrganization().isActual();
    }

    public boolean hasRole() {
        return getRole() != null;
    }

    public Set<ResourceSpec> getContactSpecs() {
        Set<ResourceSpec> specs = new HashSet<ResourceSpec>();
        for ( Flow receive : getAllSharingReceives() )
            if ( receive.isAskedFor() ) {
                Node source = receive.getSource();
                if ( source.isConnector() ) {
                    for ( ExternalFlow flow : ( (Connector) source ).getExternalFlows() ) {
                        Node externalSource = flow.getSource();
                        if ( !externalSource.isConnector() )
                            specs.add( new ResourceSpec( (Specable) externalSource ) );
                    }
                } else
                    specs.add( new ResourceSpec( (Specable) source ) );
            }
        for ( Flow send : getAllSharingSends() ) {
            Node target = send.getTarget();
            if ( target.isConnector() )
                for ( ExternalFlow flow : ( (Connector) target ).getExternalFlows() ) {
                    Node externalTarget = flow.getTarget();
                    if ( !externalTarget.isConnector() )
                        specs.add( new ResourceSpec( (Specable) externalTarget ) );
                }
            else
                specs.add( new ResourceSpec( (Specable) target ) );
        }
        return specs;
    }

    public Place getKnownLocation() {
        if ( location != null && location.isNamed() ) {
            return location.getNamedPlace();
        } else {
            return null;
        }
    }

    @SuppressWarnings( "unchecked" )
    public List<Flow> getAllFlows() {
        List<Flow> allFlows = new ArrayList<Flow>();
        allFlows.addAll( (List<Flow>) IteratorUtils.toList( receives() ) );
        allFlows.addAll( (List<Flow>) IteratorUtils.toList( sends() ) );
        return allFlows;
    }

    /**
     * Find all send flows that are required.
     *
     * @return a list of flows
     */
    public List<Flow> requiredSends() {
        List<Flow> requiredFlows = new ArrayList<Flow>();
        for ( Flow out : getAllSharingSends() ) {
            if ( out.isRequired() ) {
                requiredFlows.add( out );
            }
        }
        return requiredFlows;
    }

    /**
     * Does the execution of the part depend on a given infoProduct?
     *
     * @param modelEntity  an info product
     * @param queryService a query service
     * @return a boolean
     */
    public boolean dependsOnEntity( ModelEntity modelEntity, QueryService queryService ) {
        if ( modelEntity instanceof InfoProduct )
            return dependsOnInfoProduct( (InfoProduct) modelEntity, queryService );
        else if ( modelEntity instanceof InfoFormat )
            return dependsOnInfoFormat( (InfoFormat) modelEntity, queryService );
        else if ( modelEntity instanceof TransmissionMedium )
            return dependsOnMedium( (TransmissionMedium) modelEntity, queryService );
        else
            return false;
    }

    private boolean dependsOnMedium( TransmissionMedium medium, QueryService queryService ) {
        return !findDependentReceives( medium, queryService ).isEmpty();
    }

    private boolean dependsOnInfoProduct( InfoProduct infoProduct, QueryService queryService ) {
        return !findDependentReceives( infoProduct, queryService ).isEmpty();
    }

    @SuppressWarnings( "unchecked" )
    private List<Flow> findDependentReceives( final InfoProduct infoProduct, final QueryService queryService ) {
        return (List<Flow>) CollectionUtils.select(
                getAllSharingReceives(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Flow receive = (Flow) object;
                        return receive.isImportant()
                                && receive.getInfoProduct() != null
                                && receive.getInfoProduct().narrowsOrEquals(
                                infoProduct,
                                queryService.getPlanLocale() );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private boolean dependsOnInfoFormat( final InfoFormat infoFormat, final QueryService queryService ) {
        return !findDependentReceives( infoFormat, queryService ).isEmpty();
    }

    private List<Flow> findDependentReceives( final InfoFormat infoFormat, final QueryService queryService ) {
        List<Flow> dependentReceives = new ArrayList<Flow>();
        final Place locale = queryService.getPlanLocale();
        for ( Flow receive : getAllSharingReceives() ) {
            if ( receive.isImportant() ) {
                for ( Channel channel : receive.getEffectiveChannels() ) {
                    InfoFormat format = channel.getFormat();
                    if ( format != null && format.narrowsOrEquals( infoFormat, locale ) )
                        dependentReceives.add( receive );
                }
            }
        }
        return dependentReceives;
    }

    private List<Flow> findDependentReceives( final TransmissionMedium transmissionMedium, final QueryService queryService ) {
        List<Flow> dependentReceives = new ArrayList<Flow>();
        final Place locale = queryService.getPlanLocale();
        for ( Flow receive : getAllSharingReceives() ) {
            if ( receive.isImportant() ) {
                for ( Channel channel : receive.getEffectiveChannels() ) {
                    TransmissionMedium medium = channel.getMedium();
                    if ( medium != null && medium.narrowsOrEquals( transmissionMedium, locale ) )
                        dependentReceives.add( receive );
                }
            }
        }
        return dependentReceives;
    }


    /**
     * Does the part have an alternative for entity to successfully execute?
     *
     * @param modelEntity  an info product
     * @param queryService a query service
     * @return a boolean
     */
    public boolean hasAlternativesForEntity( final ModelEntity modelEntity, final QueryService queryService ) {
        if ( modelEntity instanceof InfoProduct )
            return hasAlternativesForInfoProduct( (InfoProduct) modelEntity, queryService );
        else if ( modelEntity instanceof InfoFormat )
            return hasAlternativesForInfoFormat( (InfoFormat) modelEntity, queryService );
        else if ( modelEntity instanceof TransmissionMedium )
            return hasAlternativesForMedium( (TransmissionMedium) modelEntity, queryService );
        else
            return false;
    }

    private boolean hasAlternativesForInfoProduct( final InfoProduct infoProduct, final QueryService queryService ) {
        List<Flow> dependentReceives = findDependentReceives( infoProduct, queryService );
        assert !dependentReceives.isEmpty();
        // For each dependent receive, there is an equivalent flow not using the info product.
        // i.e., there is no dependent receive for which there is no equivalent flow not using the info product.
        final Place locale = queryService.getPlanLocale();
        return !CollectionUtils.exists(
                dependentReceives,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Flow dependentReceive = (Flow) object;
                        return !CollectionUtils.exists(
                                getAllSharingReceives(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        Flow otherReceive = (Flow) object;
                                        InfoProduct otherInfoProduct = otherReceive.getInfoProduct();
                                        return otherReceive.isAlternativeSharingTo( dependentReceive, false, queryService )
                                                && ( otherInfoProduct == null
                                                || !otherInfoProduct.narrowsOrEquals( infoProduct, locale ) );
                                    }
                                }
                        );
                    }
                }
        );

    }

    private boolean hasAlternativesForInfoFormat( final InfoFormat infoFormat, final QueryService queryService ) {
        List<Flow> dependentReceives = findDependentReceives( infoFormat, queryService );
        assert !dependentReceives.isEmpty();
        final Place locale = queryService.getPlanLocale();
        // For each dependent receive, there is an alternate channel not requiring the format
        // or there is an equivalent flow not using the info format.
        // i.e., there is no dependent receive for which there is no equivalent flow not using the info format.
        return !CollectionUtils.exists(
                dependentReceives,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Flow dependentReceive = (Flow) object;
                        return !hasChannelNotUsingFormat( dependentReceive, infoFormat, locale )
                                && !CollectionUtils.exists(
                                getAllSharingReceives(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        Flow otherReceive = (Flow) object;
                                        return otherReceive.isAlternativeSharingTo( dependentReceive, false, queryService )
                                                && hasChannelNotUsingFormat( otherReceive, infoFormat, locale );
                                    }
                                }
                        );
                    }
                }
        );
    }

    private boolean hasChannelNotUsingFormat( Flow flow, final InfoFormat infoFormat, final Place locale ) {
        return CollectionUtils.exists(
                flow.getEffectiveChannels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        InfoFormat format = ( (Channel) object ).getFormat();
                        return format == null || !format.narrowsOrEquals( infoFormat, locale );
                    }
                } );
    }

    private boolean hasAlternativesForMedium( final TransmissionMedium transmissionMedium, final QueryService queryService ) {
        List<Flow> dependentReceives = findDependentReceives( transmissionMedium, queryService );
        assert !dependentReceives.isEmpty();
        final Place locale = queryService.getPlanLocale();
        // For each dependent receive, there is an alternate channel not requiring the medium
        // or there is an equivalent flow not using the medium.
        // i.e., there is no dependent receive for which there is no equivalent flow not using the medium.
        return !CollectionUtils.exists(
                dependentReceives,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        final Flow dependentReceive = (Flow) object;
                        return !hasChannelNotUsingMedium( dependentReceive, transmissionMedium, locale )
                                && !CollectionUtils.exists(
                                getAllSharingReceives(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        Flow otherReceive = (Flow) object;
                                        return otherReceive.isAlternativeSharingTo( dependentReceive, false, queryService )
                                                && hasChannelNotUsingMedium( otherReceive, transmissionMedium, locale );
                                    }
                                }
                        );
                    }
                }
        );
    }

    private boolean hasChannelNotUsingMedium( Flow flow, final TransmissionMedium transmissionMedium, final Place locale ) {
        return CollectionUtils.exists(
                flow.getEffectiveChannels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        TransmissionMedium medium = ( (Channel) object ).getMedium();
                        return medium == null || !medium.narrowsOrEquals( transmissionMedium, locale );
                    }
                } );
    }

    public int countChecklistIssues( Analyst analyst, PlanService planService ) {
        return CollectionUtils.select(
                analyst.listIssues( planService, this, true, false ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Issue issue = (Issue) object;
                        return issue.hasTag( "checklist" );
                    }
                }
        ).size();
    }

    public List<InfoNeed> getInfoNeeds() {
        List<InfoNeed> explicitNeeds = new ArrayList<InfoNeed>();
        for ( Flow need : getNeeds() ) {
            explicitNeeds.add( new InfoNeed( need ) );
        }
        Set<InfoNeed> sharedInfoList = new HashSet<InfoNeed>();
        for ( Flow sharing : getAllSharingReceives() ) {
            final InfoNeed sharedInfo = new InfoNeed( sharing );
            if ( !CollectionUtils.exists(
                    explicitNeeds,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return sharedInfo.narrowsOrEquals( (InfoNeed) object );
                        }
                    }
            ) ) {
                sharedInfoList.add( sharedInfo );
            }
        }
        List<InfoNeed>neededInfoList = new ArrayList<InfoNeed>( explicitNeeds );
        neededInfoList.addAll( sharedInfoList );
        return neededInfoList;
    }

    public List<InfoCapability> getInformationCapabilities() {
        List<InfoCapability> explicitCapabilities = new ArrayList<InfoCapability>(  );
        for ( Flow capability : getCapabilities() ) {
            explicitCapabilities.add( new InfoCapability( capability ) );
        }
        Set<InfoCapability> sharedInfoList = new HashSet<InfoCapability>();
        for ( Flow sharing : getAllSharingSends() ) {
            final InfoCapability sharedInfo = new InfoCapability( sharing );
            if ( !CollectionUtils.exists(
                    explicitCapabilities,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return sharedInfo.narrowsOrEquals( (InfoCapability) object );
                        }
                    }
            ) ) {
                sharedInfoList.add( sharedInfo );
            }
        }
        List<InfoCapability>infoCapabilities = new ArrayList<InfoCapability>( explicitCapabilities );
        infoCapabilities.addAll( sharedInfoList );
        return infoCapabilities;
    }


    /**
     * Category of tasks.
     */
    public enum Category {

        Audit,
        Analysis,
        Direction,
        InterOperationsCoordination,
        Operations,
        OperationsManagement,
        PlanningPreparing,
        PolicySetting;

        public String getLabel() {
            switch ( this ) {
                case OperationsManagement:
                    return "Operations management";
                case InterOperationsCoordination:
                    return "Operational coordination";
                case PlanningPreparing:
                    return "Planning/preparing";
                case PolicySetting:
                    return "Policy setting";
                default:
                    return name();
            }
        }

        public String getHint() {
            switch ( this ) {
                case Operations:
                    return "Primary activities: The work that directly accomplishes the mission of the organization";
                case OperationsManagement:
                    return "Control, regulation and measurement of operations";
                case InterOperationsCoordination:
                    return "Avoiding and resolving operational conflicts without involving higher ups";
                case Direction:
                    return "What higher ups do: Operational cohesion, resource allocation, setting performance targets";
                case Audit:
                    return "Verification of operational performance by higher ups";
                case Analysis:
                    return "Making sense of the external environment";
                case PlanningPreparing:
                    return "Forward looking activities: Market research, forward planning, strategy, R&D...";
                case PolicySetting:
                    return "Ultimate authority: mission definition, ethics, balancing current vs future needs...";
                default:
                    return name();
            }
        }

        public static List<String> getAllLabels() {
            List<String> labels = new ArrayList<String>();
            for ( Category category : Category.values() ) {
                labels.add( category.getLabel() );
            }
            Collections.sort( labels );
            return labels;
        }

        public static Category valueOfLabel( String label ) {
            for ( Category category : Category.values() ) {
                if ( category.getLabel().equals( label ) )
                    return category;
            }
            return null;
        }

    }
}

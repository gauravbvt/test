package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Attachment.Type;
import com.mindalliance.channels.core.ModelObjectContext;
import com.mindalliance.channels.core.model.Phase.Timing;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A plan: events segments that respond to events and entities participating in the responses.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2009
 * Time: 3:43:39 PM
 */
public class Plan extends ModelObject implements ModelObjectContext, Comparable<ModelObject> {

    /** Logger. */
    // private static final Logger LOG = LoggerFactory.getLogger( Plan.class );

    /**
     * Name of the default phase of a plan.
     */
    public static final String DEFAULT_PHASE_NAME = "Responding";

    /**
     * Timing of the default phase.
     */
    public static final Timing DEFAULT_PHASE_TIMING = Timing.Concurrent;
    /**
     * Default default spoken language.
     */
    public static final String DEFAULT_LANGUAGE = "English";

    /**
     * Whether the plan is meant as a template.
     */
    private boolean viewableByAll;

    /**
     * Whether an organization is expected to have agents with assigned tasks.
     *
     * @param organization an organization
     * @return a boolean
     */
    public boolean isInScope( Organization organization ) {
        return getOrganizations().contains( organization );
    }

    public static String classLabel() {
        return "this plan";
    }

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
     * The segments, for convenience...
     */
    private final Set<Segment> segments = new HashSet<Segment>();

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
    private Status status;

    /**
     * History of shifts in assignable id lower bounds.
     */
    private Map<Date,Long> idShifts = new HashMap<Date, Long>();

    /**
     * Version number.
     * Implied from folder where persisted
     */
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

    /**
     * Classifications supported.
     */
    private List<Classification> classifications = new ArrayList<Classification>();

    /**
     * The plan's locale.
     */
    private Place locale;
    /**
     * The plan's default spoken language.
     */
    private String defaultLanguage = DEFAULT_LANGUAGE;

    private String plannerSupportCommunity = "";

    private String userSupportCommunity = "";

    private String communityCalendar = "";

    private String communityCalendarHost = "";

    private String communityCalendarPrivateTicket = "";

    //-----------------------------
    public Plan() {
    }

    @Override
    public void recordIdShift( long lowerBound ) {
        idShifts.put( new Date(), lowerBound  );
    }

    public Map<Date, Long> getIdShifts() {
        return idShifts;
    }

    public void setIdShifts( Map<Date, Long> idShifts ) {
        this.idShifts = idShifts;
    }

    @Override
    public long getIdShiftSince( Date dateOfRecord ) {
        long shift = 0;
        for ( Date shiftDate : getIdShifts().keySet() ) {
            if ( dateOfRecord.before( shiftDate ) ) {
                shift = shift + idShifts.get( shiftDate );
            }
        }
        return shift;
    }

    @Override
    public String getClassLabel() {
        return classLabel();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion( int version ) {
        this.version = version;
    }

    public boolean isDevelopment() {
        return status == Status.DEVELOPMENT;
    }

    public void setDevelopment() {
        status = Status.DEVELOPMENT;
    }

    public boolean isProduction() {
        return status == Status.PRODUCTION;
    }

    public void setProduction() {
        status = Status.PRODUCTION;
    }

    public boolean isRetired() {
        return status == Status.RETIRED;
    }

    public void setRetired() {
        status = Status.RETIRED;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus( Status status ) {
        this.status = status;
    }

    public Place getLocale() {
        return locale;
    }

    public void setLocale( Place locale ) {
        this.locale = locale;
    }

    public String getDefaultLanguage() {
        return defaultLanguage == null || defaultLanguage.isEmpty()
                ? DEFAULT_LANGUAGE
                : defaultLanguage;
    }

    public void setDefaultLanguage( String defaultLanguage ) {
        if ( defaultLanguage != null )
            this.defaultLanguage = defaultLanguage;
    }

    public String getPlannerSupportCommunity() {
        return plannerSupportCommunity == null ? "" : plannerSupportCommunity;
    }

    public void setPlannerSupportCommunity( String plannerSupportCommunity ) {
        this.plannerSupportCommunity = plannerSupportCommunity;
    }

    public String getCommunityCalendar() {
        return communityCalendar == null ? "" : communityCalendar;
    }

    public void setCommunityCalendar( String communityCalendar ) {
        this.communityCalendar = communityCalendar;
    }

    public String getCommunityCalendarHost() {
        return communityCalendarHost == null ? "" : communityCalendarHost;
    }

    public void setCommunityCalendarHost( String communityCalendarHost ) {
        this.communityCalendarHost = communityCalendarHost;
    }

    public String getPlannerSupportCommunity( String defaultName ) {
        String name = getPlannerSupportCommunity();
        return name.isEmpty() ? defaultName : name;
    }

    public String getUserSupportCommunity() {
        return userSupportCommunity == null ? "" : userSupportCommunity;
    }

    public void setUserSupportCommunity( String supportCommunity ) {
        this.userSupportCommunity = supportCommunity;
    }

    public String getUserSupportCommunity( String defaultName ) {
        String name = getUserSupportCommunity();
        return name.isEmpty() ? defaultName : name;
    }

    public String getCommunityCalendar( String defaultCalendar ) {
        String name = getCommunityCalendar();
        return name.isEmpty() ? defaultCalendar : name;
    }

    public String getCommunityCalendarHost( String defaultCalendarHost ) {
        String name = getCommunityCalendarHost();
        return name.isEmpty() ? defaultCalendarHost : name;
    }

    public String getCommunityCalendarPrivateTicket(
            String defaultCommunityCalendarPrivateTicket ) {

        String ticket = getCommunityCalendarPrivateTicket();
        return ticket.isEmpty() ? defaultCommunityCalendarPrivateTicket : ticket;
    }

    public String getCommunityCalendarPrivateTicket() {
        return communityCalendarPrivateTicket == null ? "" : communityCalendarPrivateTicket;
    }

    public void setCommunityCalendarPrivateTicket( String communityCalendarPrivateTicket ) {
        this.communityCalendarPrivateTicket = communityCalendarPrivateTicket;
    }

    /**
     * Name with version.
     *
     * @return a string
     */
    public String getVersionedName() {
        return getName() + " v." + version + " (" + getStatusString() + ')';
    }

    public String getSimpleVersionedName() {
        return getName() + " v." + version;
    }


    public Set<Segment> getSegments() {
        return segments;
    }

    public List<Event> getIncidents() {
        return incidents;
    }

    public void setIncidents( List<Event> incidents ) {
        this.incidents = new ArrayList<Event>( incidents );
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations( List<Organization> organizations ) {
        this.organizations = new ArrayList<Organization>( organizations );
    }

    public String getClient() {
        return client == null ? "UNKNOWN" : client;
    }

    public void setClient( String client ) {
        this.client = client;
    }

    public String getUri() {
        return uri;
    }

    public void setUri( String uri ) {
        this.uri = ChannelsUtils.sanitize( uri );
    }

    public String getVersionUri() {
        return getUri() + ':' + version;
    }

    public List<String> getProducers() {
        return producers == null ? new ArrayList<String>() : producers;
    }

    public void setProducers( List<String> producers ) {
        this.producers = new ArrayList<String>( producers );
    }

    public void removeAllProducers() {
        producers = new ArrayList<String>();
    }

    public Date getWhenVersioned() {
        return whenVersioned == null ? new Date() : whenVersioned;
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
        if ( !producers.contains( username ) )
            producers.add( username );
    }

    /**
     * Remove planner as voting to put plan in production.
     *
     * @param username a string
     */
    public void removeProducer( String username ) {
        producers.remove( username );
    }

    public boolean isViewableByAll() {
        return viewableByAll;
    }

    public void setViewableByAll( boolean val ) {
        this.viewableByAll = val;
    }

    public boolean isVisibleToUsers() {
        return isViewableByAll() && isProduction();
    }

    /**
     * Add event.
     *
     * @param event an event
     */
    public void addIncident( Event event ) {
        assert event.isType();
        if ( !incidents.contains( event ) )
            incidents.add( event );
    }

    /**
     * Add an organization expected to be involved.
     *
     * @param organization an organization
     */
    public void addOrganization( Organization organization ) {
        assert organization.isActual();
        if ( !organizations.contains( organization ) )
            organizations.add( organization );
    }

    public List<Classification> getClassifications() {
        return classifications;
    }

    public void setClassifications( List<Classification> classifications ) {
        this.classifications = new ArrayList<Classification>( classifications );
    }

    /**
     * Find classification given system and name.
     *
     * @param system a string
     * @param name   a string
     * @return a classification or null
     */
    public Classification getClassification( String system, final String name ) {
        return (Classification) CollectionUtils.find( classificationsFor( system ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Classification) object )
                                .getName().equals( name );
                    }
                } );
    }

    /**
     * Get a segment's default event.
     *
     * @return a plan event
     */
    public Event getDefaultEvent() {
        // TODO  revise loading strategy so this does not return null on import
        assert incidents != null && !incidents.isEmpty();
        Iterator<Event> eventIterator = incidents.iterator();
        return eventIterator.hasNext() ? eventIterator.next() : null;
    }

    /**
     * Get a segment's default phase, adding it if needed.
     *
     * @return a phase
     */
    public Phase getDefaultPhase() {
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
     * Add a segment to list.
     *
     * @param segment a segment
     */
    public void addSegment( Segment segment ) {
        segments.add( segment );
    }

    /**
     * Remove deleted segment from list.
     *
     * @param segment a segment
     */
    public void removeSegment( Segment segment ) {
        segments.remove( segment );
    }

    /**
     * Get the number of segment in this plan.
     *
     * @return the number of segment
     */
    public int getSegmentCount() {
        return segments.size();
    }

    /**
     * Return one of the segments.
     *
     * @return a segment
     */
    public Segment getDefaultSegment() {
        return segments.iterator().next();
    }

    public List<Phase> getPhases() {
        return phases;
    }

    public void setPhases( List<Phase> phases ) {
        this.phases = new ArrayList<Phase>( phases );
    }

    /**
     * Add phase to plan.
     *
     * @param phase a phase
     */
    public void addPhase( Phase phase ) {
        phases.add( phase );
    }

    @Override
    public String toString() {
        return getName() + " v." + version + " (" + getStatusString() + ')';
    }

    private String getStatusString() {
        return isDevelopment() ? "dev" : isProduction() ? "prod" : "ret";
    }

    @Override
    public boolean equals( Object obj ) {
        return this == obj || obj != null && obj instanceof Plan && getVersionUri()
                .equals( ( (Plan) obj ).getVersionUri() );
    }

    @Override
    public int hashCode() {
        return getVersionUri().hashCode();
    }

    @Override
    public boolean references( final ModelObject mo ) {
        return ModelObject.areIdentical( locale, mo )
                || CollectionUtils.exists( segments, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ModelObject.areIdentical( (ModelObject) object, mo );
            }
        } )
                || CollectionUtils.exists( incidents, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ModelObject.areIdentical( (ModelObject) object, mo );
            }
        } )
                || CollectionUtils.exists( organizations, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ModelObject.areIdentical( (ModelObject) object, mo );
            }
        } )
                || CollectionUtils.exists( phases, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ModelObject.areIdentical( (ModelObject) object, mo );
            }
        } );
    }

    @Override
    public boolean isSegmentObject() {
        return false;
    }

    /**
     * List all classification systems (sorted).
     *
     * @return a list of strings
     */
    public List<String> classificationSystems() {
        Set<String> systems = new HashSet<String>();
        for ( Classification classification : classifications )
            systems.add( classification.getSystem() );
        List<String> classificationSystems = new ArrayList<String>( systems );
        Collections.sort( classificationSystems );
        return classificationSystems;
    }

    /**
     * Find all classifications in a given classification system.
     *
     * @param classificationSystem a string
     * @return a list of classifications
     */
    public List<Classification> classificationsFor( String classificationSystem ) {
        List<Classification> list = new ArrayList<Classification>();
        for ( Classification classification : classifications ) {
            if ( classification.getSystem().equals( classificationSystem ) ) {
                list.add( classification );
            }
        }
        Collections.sort( list );
        return list;
    }

    /**
     * Add a classification if unique. Return whether added.
     *
     * @param classification a classification
     * @return a boolean
     */
    public boolean addClassification( Classification classification ) {
        if ( classifications.contains( classification ) )
            return false;
        else {
            classifications.add( classification );
            return true;
        }
    }

    /**
     * Remove a classification.
     *
     * @param classification a classification
     */
    public void removeClassification( Classification classification ) {
        classifications.remove( classification );
    }

    /**
     * Get the level of the top classification in a system.
     *
     * @param system a string
     * @return an integer
     */
    public int topLevelFor( String system ) {
        List<Classification> list = classificationsFor( system );
        Classification top = list.get( 0 );
        return top.getLevel();
    }

    /**
     * The non-conflicting, default level for a new classification in a given system.
     *
     * @param system a string
     * @return an integer
     */
    public int defaultLevelFor( String system ) {
        List<Classification> list = classificationsFor( system );
        if ( list.isEmpty() )
            return 0;
        int max = Integer.MIN_VALUE;
        for ( Classification classification : list ) {
            max = Math.max( max, classification.getLevel() );
        }
        return max + 1;
    }

    @Override
    public List<Type> getAttachmentTypes() {
        List<Type> types = super.getAttachmentTypes();
        types.add( Type.TAGS );
        types.add( Type.Image );
        return types;
    }

    @Override
    public int compareTo( ModelObject mo ) {
        Plan other = (Plan)mo;
        if ( getUri().equals( other.getUri() ) ) {
            return isDevelopment()
                    ? -1
                    : 1;
        } else {
            return getName().compareTo( other.getName() );
        }
    }

    @Override
    public String getKindLabel() {
        return "Model";
    }

}
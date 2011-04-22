package com.mindalliance.channels.model;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.nlp.Matcher;
import com.mindalliance.channels.util.InfoStandardsLoader;
import com.mindalliance.channels.util.Loader;
import com.mindalliance.channels.util.TagLoader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A plan: events segments that respond to events and entities participating in the responses.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2009
 * Time: 3:43:39 PM
 */
public class Plan extends ModelObject {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( Plan.class );

    /**
     * Name of the default phase of a plan.
     */
    public static final String DEFAULT_PHASE_NAME = "Responding";
    /**
     * Timing of the default phase.
     */
    public static final Phase.Timing DEFAULT_PHASE_TIMING = Phase.Timing.Concurrent;
    /**
     * Whether the plan is meant as a template.
     */
    private boolean template = false;

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
    private Set<Segment> segments = new HashSet<Segment>();

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

    private String plannerSupportCommunity = "";

    private String userSupportCommunity = "";

    private String surveyApiKey = "";

    private String surveyUserKey = "";

    private String surveyTemplate = "";

    private String surveyDefaultEmailAddress = "";

    private String communityCalendar = "";

    private String communityCalendarHost = "";

    private String communityCalendarPrivateTicket = "";


    //-----------------------------
    public Plan() {
        whenVersioned = new Date();
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
        return name.isEmpty()
                ? defaultName
                : name;
    }


    public String getUserSupportCommunity() {
        return userSupportCommunity == null ? "" : userSupportCommunity;
    }

    public void setUserSupportCommunity( String supportCommunity ) {
        this.userSupportCommunity = supportCommunity;
    }

    public String getUserSupportCommunity( String defaultName ) {
        String name = getUserSupportCommunity();
        return name.isEmpty()
                ? defaultName
                : name;
    }

    public String getCommunityCalendar( String defaultCalendar ) {
        String name = getCommunityCalendar();
        return name.isEmpty()
                ? defaultCalendar
                : name;
    }


    public String getCommunityCalendarHost( String defaultCalendarHost ) {
        String name = getCommunityCalendarHost();
        return name.isEmpty()
                ? defaultCalendarHost
                : name;
    }

    public String getCommunityCalendarPrivateTicket( String defaultCommunityCalendarPrivateTicket ) {
        String ticket = getCommunityCalendarPrivateTicket();
        return ticket.isEmpty()
                ? defaultCommunityCalendarPrivateTicket
                : ticket;
    }

    public String getCommunityCalendarPrivateTicket() {
        return communityCalendarPrivateTicket == null ? "" : communityCalendarPrivateTicket;
    }

    public void setCommunityCalendarPrivateTicket( String communityCalendarPrivateTicket ) {
        this.communityCalendarPrivateTicket = communityCalendarPrivateTicket;
    }

    public String getSurveyApiKey() {
        return surveyApiKey == null ? "" : surveyApiKey;
    }

    public void setSurveyApiKey( String surveyApiKey ) {
        this.surveyApiKey = surveyApiKey;
    }

    public String getSurveyUserKey() {
        return surveyUserKey == null ? "" : surveyUserKey;
    }

    public void setSurveyUserKey( String surveyUserKey ) {
        this.surveyUserKey = surveyUserKey;
    }

    public String getSurveyTemplate() {
        return surveyTemplate == null ? "" : surveyTemplate;
    }

    public void setSurveyTemplate( String surveyTemplate ) {
        this.surveyTemplate = surveyTemplate;
    }

    public String getSurveyDefaultEmailAddress() {
        return surveyDefaultEmailAddress == null ? "" : surveyDefaultEmailAddress;
    }

    public void setSurveyDefaultEmailAddress( String surveyDefaultEmailAddress ) {
        this.surveyDefaultEmailAddress = surveyDefaultEmailAddress;
    }

    /**
     * Name with version.
     *
     * @return a string
     */
    public String getVersionedName() {
        return getName() + " v." + getVersion() + "(" + getStatusString() + ")";
    }

    public Set<Segment> getSegments() {
        return segments;
    }

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

    public InfoStandard getInfoStandard( final String name ) {
        final Matcher matcher = Matcher.getInstance();
        return (InfoStandard) CollectionUtils.find( getTags(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Tag tag = (Tag) object;
                        return tag.isInfoStandard() && matcher.same( tag.getName(), name );
                    }
                } );
    }

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate( boolean template ) {
        this.template = template;
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

    public List<Classification> getClassifications() {
        return classifications;
    }

    public void setClassifications( List<Classification> classifications ) {
        this.classifications = classifications;
    }

    /**
     * Find classification given system and name.
     *
     * @param system a string
     * @param name   a string
     * @return a classification or null
     */
    public Classification getClassification( String system, final String name ) {
        return (Classification) CollectionUtils.find(
                classificationsFor( system ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Classification) obj ).getName().equals( name );
                    }
                }
        );
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
        return getSegments().iterator().next();
    }

    public List<Phase> getPhases() {
        return phases;
    }

    public void setPhases( List<Phase> phases ) {
        this.phases = phases;
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
                || obj != null
                && obj instanceof Plan
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
        return ModelObject.areIdentical( locale, mo )
                ||
                CollectionUtils.exists(
                        segments,
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
                        } )
                || CollectionUtils.exists(
                phases,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ModelObject.areIdentical( (ModelObject) obj, mo );
                    }
                } );
    }

    /**
     * List all classification systems (sorted).
     *
     * @return a list of strings
     */
    public List<String> classificationSystems() {
        Set<String> systems = new HashSet<String>();
        for ( Classification classification : classifications ) {
            systems.add( classification.getSystem() );
        }
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
        if ( classifications.contains( classification ) ) {
            return false;
        } else {
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
        if ( list.isEmpty() ) return 0;
        int max = Integer.MIN_VALUE;
        for ( Classification classification : list ) {
            max = Math.max( max, classification.getLevel() );
        }
        return max + 1;
    }

    /**
     * {@inheritDoc}
     */
    public List<Attachment.Type> getAttachmentTypes() {
        List<Attachment.Type> types = super.getAttachmentTypes();
        types.add( Attachment.Type.TAGS );
        types.add( Attachment.Type.InfoStandards );
        types.add( Attachment.Type.Image );
        return types;
    }

    public void reloadTags( AttachmentManager attachmentManager ) {
        setTags( new ArrayList<Tag>() );
        for ( Attachment attachment : getAttachments() ) {
            if ( attachment.isTags() ) {
                String url = attachment.getUrl();
                reloadTagsFromUrl( url, attachmentManager, new TagLoader( this ) );
            }
            if ( attachment.isInfoStandards() ) {
                String url = attachment.getUrl();
                reloadTagsFromUrl( url, attachmentManager, new InfoStandardsLoader( this ) );
            }
        }
    }

    private void reloadTagsFromUrl( String url, AttachmentManager attachmentManager, Loader loader ) {
        BufferedReader in = null;
        try {
            InputStreamReader reader;
            if ( attachmentManager.isUploadedFileDocument( url ) ) {
                File file = attachmentManager.getUploadedFile( url );
                reader = new InputStreamReader( new FileInputStream( file ) );
            } else {
                reader = new InputStreamReader( new URL( url ).openStream() );
            }
            in = new BufferedReader( reader );
            loader.load( in );
        } catch ( Exception e ) {
            LOG.error( "Failed to load tags file " + url, e );
        } finally {
            if ( in != null ) {
                try {
                    in.close();
                } catch ( IOException e ) {
                    LOG.warn( "Failed to close tags file " + url, e );
                }
            }
        }
    }

    @Override
    protected void attachmentAdded( Attachment attachment, AttachmentManager attachmentManager ) {
        super.attachmentAdded( attachment, attachmentManager );
        if ( attachment.isTags() || attachment.isInfoStandards() )
            reloadTags( attachmentManager );
    }

    @Override
    protected void attachmentRemoved( Attachment attachment, AttachmentManager attachmentManager ) {
        super.attachmentRemoved( attachment, attachmentManager );
        if ( attachment.isTags() || attachment.isInfoStandards() )
            reloadTags( attachmentManager );
    }
}
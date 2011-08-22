package com.mindalliance.channels.model;

import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Someone or something playing a part in a segment.
 */
public class Actor extends AbstractUnicastChannelable implements Classifiable, Specable, Available {

    /**
     * The name of the unknown actor.
     */
    public static final String UnknownName = "(unknown contact)";

    /**
     * Bogus actor used to signify that the actor is not known...
     */
    public static Actor UNKNOWN;

    /**
     * Whether the actor is a system, vs. a person.
     */
    private boolean system;

    /**
     * Whether this (actual) agent is an archetype.
     */
    private boolean archetype = false;
    /**
     * Whether this (actual) agent is a place holder (to be named by assigned participant).
     */
    private boolean placeHolder = false;
    /**
     * The actor's time-based availability.
     * Null means 24/7.
     */
    private Availability availability = new Availability();

    /**
     * Clearances.
     */
    private List<Classification> clearances = new ArrayList<Classification>();

    /**
     * Whether placeholder is singular.
     */
    private boolean placeHolderSingular = false;

    public Actor() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Actor( String name ) {
        super( name );
    }

    /**
     * {@inheritDoc}
     */
    public String getKindLabel() {
        return "agent";
    }

    @Override
    public boolean isActual() {
        return !equals( UNKNOWN ) && super.isActual();
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem( boolean system ) {
        this.system = system;
    }

    public boolean isArchetype() {
        return archetype;
    }

    public void setArchetype( boolean archetype ) {
        this.archetype = archetype;
        if ( archetype ) placeHolder = false;
    }

    public boolean isPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder( boolean placeHolder ) {
        this.placeHolder = placeHolder;
        if ( placeHolder ) archetype = false;
    }

    public boolean isPlaceHolderSingular() {
        return placeHolderSingular;
    }

    public void setPlaceHolderSingular( boolean placeHolderSingular ) {
        this.placeHolderSingular = placeHolderSingular;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability( Availability val ) {
        availability = ( val == null )
                ? new Availability()
                : val;
    }

    public List<Classification> getClearances() {
        return clearances;
    }

    public void setClearances( List<Classification> clearances ) {
        this.clearances = clearances;
    }

    /**
     * Add a classification to the actor's clearances if unique.
     *
     * @param classification a classification
     * @return a boolean - whether added
     */
    public boolean addClearance( Classification classification ) {
        if ( !clearances.contains( classification ) ) {
            clearances.add( classification );
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return a normalized version of the name.
     *
     * @return a string
     */
    public String getNormalizedName() {
        String name = getName().trim();
        if ( this == UNKNOWN || name.indexOf( ',' ) >= 0 ) return name;
        else if ( isType() || isArchetype() || isSystem() || isPlaceHolder() ) return name;
        else {
            int index = name.lastIndexOf( ' ' );
            if ( index >= 0 ) {
                String s = name.substring( 0, index );
                return name.substring( index + 1 ) + ", " + s;
            } else
                return name;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeRemove( QueryService queryService ) {
        super.beforeRemove( queryService );
        for ( Job job : queryService.findAllConfirmedJobs( this ) ) {
            job.setActor( null );
        }
        for ( Part part : queryService.findAllParts( null, this, true ) ) {
            part.setActor( null );
        }
    }

    public String getLastName() {
        String name = getName().trim();
        if ( this == UNKNOWN || name.indexOf( ',' ) >= 0 ) return name;
        else {
            int index = name.lastIndexOf( ' ' );
            if ( index >= 0 ) {
                return name.substring( index + 1 );
            } else
                return name;
        }
    }

    /**
     * Whether the actor is a person (i.e. not a system).
     *
     * @return a boolean
     */
    public boolean isPerson() {
        return !isSystem();
    }

    /**
     * {@inheritDoc}
     */
    public List<Attachment.Type> getAttachmentTypes() {
        List<Attachment.Type> types = new ArrayList<Attachment.Type>();
        if ( !hasImage() )
            types.add( Attachment.Type.Image );
        types.addAll( super.getAttachmentTypes() );
        return types;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isIconized() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public List<Classification> getClassifications() {
        return getClearances();
    }

    /**
     * Whether the actor is cleared for the information in a flow.
     *
     * @param flow a flow
     * @param plan
     * @return a boolean
     */
    public boolean isClearedFor( Flow flow, final Plan plan ) {
        // No eoi in the flow has classification that is not encompassed by the actor's clearance.
        return !CollectionUtils.exists(
                flow.getEois(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        ElementOfInformation eoi = (ElementOfInformation) obj;
                        return !Classification.encompass(
                                getClearances(),
                                eoi.getClassifications(), plan );
                    }
                }
        );
    }

    public Actor getActor() {
        return this;
    }

    public Role getRole() {
        return null;
    }

    public Organization getOrganization() {
        return null;
    }

    public Place getJurisdiction() {
        return null;
    }

    /**
     * Whether can be associated with at most one user.
     *
     * @return a boolean
     */
    public boolean isSingular() {
        return !isArchetype() && ( !isPlaceHolder() || isPlaceHolderSingular() );
    }
}

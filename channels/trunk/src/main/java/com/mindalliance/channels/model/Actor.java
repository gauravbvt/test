package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.attachments.Attachment;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Someone or something playing a part in a segment.
 */
@Entity
public class Actor extends AbstractUnicastChannelable implements Classifiable {

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
     * Name of the user, if any, represented by this actor.
     */
    private String userName;

    /**
     * Clearances.
     */
    private List<Classification> clearances = new ArrayList<Classification>();

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
     * Create immutables.
     *
     * @param queryService a query service
     */
    public static void createImmutables( QueryService queryService ) {
        UNKNOWN = queryService.findOrCreate( Actor.class, UnknownName );
        UNKNOWN.makeImmutable();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getKindLabel() {
        return "individual";
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

    public String getUserName() {
        return userName;
    }

    public void setUserName( String userName ) {
        this.userName = userName;
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
     * {@inheritDoc}
     */
    @Override
    @Transient
    public boolean isEntity() {
        return true;
    }

    /**
     * Return a normalized version of the name.
     *
     * @return a string
     */
    @Transient
    public String getNormalizedName() {
        String name = getName().trim();
        if ( this == UNKNOWN || name.indexOf( ',' ) >= 0 ) return name;
        else if ( isType() ) return name;
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
    public void beforeRemove( QueryService queryService ) {
        super.beforeRemove( queryService );
        for ( Job job : queryService.findAllConfirmedJobs( ResourceSpec.with( this ) ) ) {
            job.setActor( null );
        }
        for ( Part part : queryService.findAllParts( null, ResourceSpec.with( this ), true ) ) {
            part.setActor( null );
        }
    }

    @Transient
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
    @Transient
    public boolean isPerson() {
        return !isSystem();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
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
    @Transient
    public boolean isIconized() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public List<Classification> getClassifications() {
        return getClearances();
    }

    /**
     * Whether the actor is cleared for the information in a flow.
     *
     * @param flow a flow
     * @return a boolean
     */
    public boolean isClearedFor( Flow flow ) {
        // No eoi in the flow has classification that exceeds the actor's clearance.
        return !CollectionUtils.exists(
                flow.getEois(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        ElementOfInformation eoi = (ElementOfInformation) obj;
                        return Classification.hasHigherOrEqualClassification(
                                eoi.getClassifications(),
                                getClearances()
                        );
                    }
                }
        );
    }
}

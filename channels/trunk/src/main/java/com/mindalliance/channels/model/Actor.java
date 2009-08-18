package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.attachments.Attachment;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Someone or something playing a part in a scenario.
 */
@Entity
public class Actor extends AbstractUnicastChannelable {

    /**
     * The name of the unknown actor.
     */
    public static final String UnknownName = "(unknown contact)";

    /**
     * Bogus actor used to signify that the actor is not known...
     */
    public static final Actor UNKNOWN;

    /**
     * Whether the actor is a system, vs. a person.
     */
    private boolean system;
    /**
     * Name of the user, if any, represented by this actor.
     */
    private String userName;

    static {
        UNKNOWN = new Actor( UnknownName );
        UNKNOWN.setId( 10000000L - 1L );
    }

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
        for ( Part part : queryService.findAllParts( null, ResourceSpec.with( this ) ) ) {
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

}

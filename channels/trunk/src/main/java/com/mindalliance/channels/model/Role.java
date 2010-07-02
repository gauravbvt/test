package com.mindalliance.channels.model;

import com.mindalliance.channels.query.QueryService;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic role.
 */
public class Role extends ModelEntity {

    /**
      * Bogus role used to signify that the role is not known...
      */
    public static Role UNKNOWN;

    /**
     * Name of unknown role.
     */
    public static String UnknownName = "(unknown)";

    public Role() {
    }

    /**
     * Utility constructor for tests.
     *
     * @param name the name of the new object
     */
    public Role( String name ) {
        super( name );
    }

    /**
     * Whether the role is to be played by a system actor.
     * @return a boolean
     */
    public boolean isSystem() {
        return getName().toLowerCase().contains( "system" );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeRemove( QueryService queryService ) {
        super.beforeRemove( queryService );
        for ( Job job : queryService.findAllConfirmedJobs( ResourceSpec.with( this ) ) ) {
            job.setRole( null );
        }
        for ( Part part : queryService.findAllParts( null, ResourceSpec.with( this ), true ) ) {
            part.setRole( null );
        }
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


}

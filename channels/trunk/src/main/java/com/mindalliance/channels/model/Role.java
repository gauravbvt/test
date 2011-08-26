package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic role.
 */
public class Role extends ModelEntity implements Specable {

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
     * Get the implied actor.
     * @return the actor, or null if any
     */
    public Actor getActor() {
        return null;
    }

    /**
     * Get the implied role.
     * @return the role, or null if any
     */
    public Role getRole() {
        return this;
    }

    /**
     * Get the implied organization.
     * @return the organization, or null if any
     */
    public Organization getOrganization() {
        return null;
    }

    /**
     * Get the implied jurisdiction.
     * @return the jurisdiction, or null if any
     */
    public Place getJurisdiction() {
        return null;
    }

    /**
     * Get a standardized print string for reports.
     * @return "A role"
     */
    public String reportString() {
        String s = toString().toLowerCase();
        return ( "aeiouy".contains( s.substring( 0, 1 ) ) ? "An " : "A " ) + s ;
    }
}

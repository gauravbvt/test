package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.model.Attachment.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * A plan event.
 */
public class Event extends ModelEntity implements GeoLocatable {

    /**
     * Bogus event used to signify that the event is not known...
     */
    public static Event UNKNOWN;

    /**
     * Name of unknown event.
     */
    public static String UnknownName = "(unknown)";

    /**
     * The official lower-cased version of this event's name.
     */
    private String lowerCasedName;

    /**
     * Where the event is considered to occur. Null means that its scope is universal.
     */
    private Place scope;

    /**
     * Does this even self-terminate?
     */
    private boolean selfTerminating;

    //-------------------------------
    public Event() {
    }

    public Event( String name ) {
        super( name );
    }

    //-------------------------------
    @Override
    public List<Type> getAttachmentTypes() {
        List<Type> types = new ArrayList<Type>();
        if ( !hasImage() )
            types.add( Type.Image );
        types.addAll( super.getAttachmentTypes() );
        return types;
    }

    @Override
    public String getGeoMarkerLabel() {
        return scope == null ? "" : getName() + " in " + scope.getGeoMarkerLabel();
    }

    @Override
    public List<? extends GeoLocatable> getImpliedGeoLocatables() {
        List<Event> geoLocatables = new ArrayList<Event>();
        geoLocatables.add( this );
        return geoLocatables;
    }

    @Override
    public Place getPlaceBasis() {
        return scope == null ? null : scope.getPlaceBasis();
    }

    @Override
    public boolean references( ModelObject mo ) {
        return super.references( mo ) || ModelObject.areIdentical( scope, mo );
    }

    @Override
    public void setName( String name ) {
        super.setName( name );
    }

    @Override
    public boolean validates( ModelEntity entity, Place locale ) {
        Event event = (Event) entity;
        return super.validates( event, locale ) 
               && selfTerminating == event.isSelfTerminating() 
               && ModelEntity.implies( event.getScope(), scope, locale );
    }

    //-------------------------------
    public Place getScope() {
        return scope;
    }

    public void setScope( Place scope ) {
        this.scope = scope;
    }

    public boolean isSelfTerminating() {
        return selfTerminating;
    }

    public void setSelfTerminating( boolean selfTerminating ) {
        this.selfTerminating = selfTerminating;
    }

}

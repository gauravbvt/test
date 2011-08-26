package com.mindalliance.channels.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A participation represents a Channels user who is also an actor.
 */
public class Participation extends AbstractUnicastChannelable {

    /**
     * Unknown participation. Not meaningful but needed for consistency with other entities.
     */
    public static Participation UNKNOWN;

    /**
     * Name of unknown participation.
     */
    public static final String UnknownName = "(unknown)";

    /**
     * The actual actor, possibly an archetype, played by the user.
     */
    private Actor actor;

    /**
     * Name of the user, if any, represented by this actor.
     */
    private String username;

    //-------------------------------
    public Participation() {
    }

    public Participation( String username ) {
        this.username = username;
    }

    //-------------------------------
    @Override
    public List<Channel> getModifiableChannels() {
        return getChannels();
    }

    @Override
    public String getName() {
        return username;
    }

    public boolean hasActor( Actor a ) {
        return actor != null && actor.equals( a );
    }

    @Override
    public boolean isModifiableInProduction() {
        return true;
    }

    @Override
    public boolean isUndefined() {
        return username == null;
    }

    @Override
    public boolean references( final ModelObject mo ) {
        return super.references( mo ) || ModelObject.areIdentical( actor, mo );
    }

    @Override
    public void setName( String name ) {
        username = name;
    }

    //-------------------------------
    public Actor getActor() {
        return actor;
    }

    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    @Override
    public List<Channel> getEffectiveChannels() {
        List<Channel> effectiveChannels = new ArrayList<Channel>( getChannels() );
        if ( actor != null ) {
            for ( final Channel channel : actor.getEffectiveChannels() ) {
                if ( !CollectionUtils.exists( effectiveChannels, new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Channel) object ).getMedium().equals( channel.getMedium() );
                    }
                } ) ) {
                    effectiveChannels.add( channel );
                }
            }
        }
        return effectiveChannels;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }
}

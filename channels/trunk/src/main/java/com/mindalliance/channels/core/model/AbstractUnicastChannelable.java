package com.mindalliance.channels.core.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for channelable entity with only unicast channels. Copyright (C) 2008 Mind-Alliance Systems. All
 * Rights Reserved. Proprietary and Confidential. User: jf Date: Mar 17, 2009 Time: 11:25:24 AM
 */
public abstract class AbstractUnicastChannelable extends ModelEntity implements Channelable {

    /** Channels. */
    private List<Channel> channels = new ArrayList<Channel>();

    protected AbstractUnicastChannelable() {
    }

    protected AbstractUnicastChannelable( String name ) {
        super( name );
    }

    public List<Channel> getChannels() {
        return channels;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Channel> getEffectiveChannels() {
        return (List<Channel>) CollectionUtils.collect(
                getChannels(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        Channel channel = (Channel) input;
                        return hasAddresses()
                                ? channel
                                : new Channel( channel.getMedium() );
                    }
                } );
    }

    @Override
    public List<Channel> getModifiableChannels() {
        return getChannels();
    }

    @Override
    public void addChannel( Channel channel ) {
        // assert channel.isUnicast();
        if ( !hasAddresses() ) channel.setAddress( null );
        if ( !channels.contains( channel ) )
            channels.add( channel );
    }

    @Override
    public void removeChannel( Channel channel ) {
        channels.remove( channel );
    }

    @Override
    public String getChannelsString() {
        return Channel.toString( getEffectiveChannels() );
    }

    @Override
    public List<Channel> allChannels() {
        return getEffectiveChannels();
    }

    public void setChannels( List<Channel> channels ) {
        this.channels = channels;
    }

    @Override
    public boolean canBeUnicast() {
        return true;
    }

    @Override
    public boolean canSetChannels() {
        return true;
    }

    @Override
    public String validate( Channel channel ) {
        return channel.isValid() ? null : "Invalid address";
    }

    @Override
    public boolean isModelObject() {
        return true;
    }

    @Override
    public boolean hasChannelFor( final TransmissionMedium medium, final Place planLocale ) {
        return CollectionUtils.exists(
                getEffectiveChannels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Channel)object ).getMedium().narrowsOrEquals( medium, planLocale );
                    }
                }
                );
    }

    @Override
    public boolean canBeLocked() {
        return true;
    }

    @Override
    public boolean hasAddresses() {
        return true;
    }

    @Override
    public void setAddress( Channel channel, String address ) {
        channel.setAddress( address );
    }

    @Override
    public boolean canSetFormat() {
        return false;
    }

    @Override
    public boolean isUndefined() {
        return super.isUndefined() && channels.isEmpty();
    }

    @Override
    public boolean references( final ModelObject mo ) {
        return super.references( mo ) || mo instanceof TransmissionMedium && CollectionUtils
                .exists( channels, new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return mo.equals( ( (Channel) object ).getMedium() );
                    }
                } );
    }

    @Override
    public String getKindLabel() {
        return getTypeName();
    }

}

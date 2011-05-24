package com.mindalliance.channels.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for channelable entity with only unicast channels.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 17, 2009
 * Time: 11:25:24 AM
 */
public abstract class AbstractUnicastChannelable extends ModelEntity implements Channelable {

    /**
     * Channels.
     */
    private List<Channel> channels = new ArrayList<Channel>();

    protected AbstractUnicastChannelable() {
    }

    protected AbstractUnicastChannelable( String name ) {
        super( name );
    }

    public List<Channel> getChannels() {
        return channels;
    }

    /**
     * {@inheritDoc}
     */
    public List<Channel> getEffectiveChannels() {
        return getChannels();
    }

    @Override
    public List<Channel> getModifiableChannels() {
        return getEffectiveChannels();
    }

    /**
     * {@inheritDoc}
     */
    public void addChannel( Channel channel ) {
        // assert channel.isUnicast();
        if ( !channels.contains( channel ) ) channels.add( channel );
    }

    /**
     * {@inheritDoc}
     */
    public void removeChannel( Channel channel ) {
        channels.remove( channel );
    }

    /**
     * {@inheritDoc}
     */
    public String getChannelsString() {
        return Channel.toString( getEffectiveChannels() );
    }

    /**
     * {@inheritDoc }
     */
    public List<Channel> allChannels() {
        return getEffectiveChannels();
    }

    public void setChannels( List<Channel> channels ) {
        this.channels = channels;
    }

    /**
     * {@inheritDoc }
     */
    public boolean canBeUnicast() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetChannels() {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    public String validate( Channel channel ) {
        return channel.isValid() ? null : "Invalid address";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndefined() {
        return super.isUndefined() && channels.isEmpty();
    }

    @Override
    public boolean references( final ModelObject mo ) {
        return super.references( mo )
                || ( mo instanceof TransmissionMedium
                && CollectionUtils.exists(
                channels,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Channel) object ).getMedium().equals( mo );
                    }
                } ) );
    }
}

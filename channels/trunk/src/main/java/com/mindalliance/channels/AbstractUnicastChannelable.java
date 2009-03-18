package com.mindalliance.channels;

import javax.persistence.Transient;
import javax.persistence.Entity;
import java.util.List;
import java.util.ArrayList;

/**
 * Abstract class for channelable model objects with only unicast channels.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 17, 2009
 * Time: 11:25:24 AM
 */
@Entity
public abstract class AbstractUnicastChannelable extends ModelObject implements Channelable {

    public AbstractUnicastChannelable() {
        super();
    }


    public AbstractUnicastChannelable( String name ) {
        super( name );
    }

    /**
     * Channels.
     */
    private List<Channel> channels = new ArrayList<Channel>();

    public List<Channel> getChannels() {
        return channels;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public List<Channel> getEffectiveChannels() {
        return channels;
    }

    /**
     * {@inheritDoc}
     */
    public void addChannel( Channel channel ) {
        assert channel.isUnicast();
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
    @Transient
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
     * {@inheritDoc }
     */
    public String validate( Channel channel ) {
        return channel.isValid() ? null : "Invalid address";
    }
}

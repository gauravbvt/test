package com.mindalliance.channels.model;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Channels.
     */
    private List<Channel> channels = new ArrayList<Channel>();

    protected AbstractUnicastChannelable() {
    }

    protected AbstractUnicastChannelable( String name ) {
        super( name );
    }

    @OneToMany @JoinTable( name = "AUC_CHANNELS" )
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

    /** {@inheritDoc} */
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
    @Transient
    public boolean isUndefined() {
        return super.isUndefined() && channels.isEmpty();
    }

}

package com.mindalliance.channels.model;

import java.util.List;

/**
 * An object with channels.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 2, 2009
 * Time: 8:07:42 PM
 */
public interface Channelable extends Identifiable {

    /**
     * Get the channels that apply.
     *
     * @return a list of channels
     */
    List<Channel> getEffectiveChannels();

    /**
     * Get the channels that can be modified.
     *
     * @return a list of channels
     */
    List<Channel> getModifiableChannels();

    /**
     * Add a channel.
     *
     * @param channel to add
     */
    void addChannel( Channel channel );

    /**
     * Remove a channel.
     *
     * @param channel to remove
     */
    void removeChannel( Channel channel );

    /**
     * Produces a summary string of the channels.
     *
     * @return a String
     */
    String getChannelsString();

    /**
     * Get all explicit and implied channels
     *
     * @return list of channels
     */
    List<Channel> allChannels();

    /**
     * Whether the channelable can be the recipient of a unicast communication.
     *
     * @return a boolean
     */
    boolean canBeUnicast();

    /**
     * @return true if the channels in this object can be modified.
     */
    boolean canSetChannels();

    /**
     * Validate a channel.
     *
     * @param channel a channel
     * @return a string indicating a problem or null if none
     */
    String validate( Channel channel );

    /**
     * Whether the channelable is an entity.
     *
     * @return a boolean
     */
    boolean isEntity();
}

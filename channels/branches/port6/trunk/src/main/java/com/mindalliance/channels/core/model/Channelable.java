package com.mindalliance.channels.core.model;

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

    /**
     * Whether the channelable is a model object (vs db-persistent object).
     *
     * @return a boolean
     */
    boolean isModelObject();

    /**
     * Whether it has a channel for the given medium.
     *
     * @param medium     a transmission medium
     * @param planLocale a place
     * @return a boolean
     */
    boolean hasChannelFor( TransmissionMedium medium, Place planLocale );

    /**
     * Can this be locked by a user?
     *
     * @return a boolean
     */
    boolean canBeLocked();

    /**
     * Whether channels have addresses.
     *
     * @return a boolean
     */
    boolean hasAddresses();

    /**
     * Set the address of a channel.
     *
     * @param channel a channel
     * @param address an address
     */
    void setAddress( Channel channel, String address );

    /**
     * Whether format can be set.
     * @return a boolean
     */
    boolean canSetFormat();
}

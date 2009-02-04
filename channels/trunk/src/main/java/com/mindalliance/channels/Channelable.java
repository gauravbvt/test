package com.mindalliance.channels;

import java.util.List;
import java.io.Serializable;

/**
 * An object with channels
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 2, 2009
 * Time: 8:07:42 PM
 */
public interface Channelable extends Serializable {
    /**
     * Get the channels
     * @return a set of channels
     */
    List<Channel> getChannels();

    /**
     * Add a channel
     * @param channel to add
     */
    void addChannel( Channel channel );

    /**
     * remove a channel
     * @param channel to remove
     */
    void removeChannel( Channel channel );

    /**
     * Produces a summary string of the channels
     * @return a String
     */
    String getChannelsString();
}

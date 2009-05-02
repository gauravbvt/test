package com.mindalliance.channels;

/**
 * An abstract service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 1, 2009
 * Time: 3:41:14 PM
 */
public class AbstractService implements Service {
    /**
     * An instance of Channels.
     */
    private Channels channels;

    public Channels getChannels() {
        return channels;
    }

    public void setChannels( Channels channels ) {
        this.channels = channels;
    }
}

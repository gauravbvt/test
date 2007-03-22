// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.reference;

/**
 * A kind of channel.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public abstract class ChannelType extends Type {

    /**
     * Default constructor.
     */
    public ChannelType() {
    }

    /**
     * Default constructor.
     * @param name the name of the channel type
     */
    public ChannelType( String name ) {
        super( name );
    }

}
